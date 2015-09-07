import datetime
import unittest
import os
import re
import shutil

import oeqa.utils.ftools as ftools
from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var, get_test_layer
from oeqa.selftest.sstate import SStateBase
from oeqa.utils.decorators import testcase

class SStateTests(SStateBase):

    # Test sstate files creation and their location
    def run_test_sstate_creation(self, targets, distro_specific=True, distro_nonspecific=True, temp_sstate_location=True, should_pass=True):
        self.config_sstate(temp_sstate_location)

        if  self.temp_sstate_location:
            bitbake(['-cclean'] + targets)
        else:
            bitbake(['-ccleansstate'] + targets)

        bitbake(targets)
        file_tracker = self.search_sstate('|'.join(map(str, targets)), distro_specific, distro_nonspecific)
        if should_pass:
            self.assertTrue(file_tracker , msg="Could not find sstate files for: %s" % ', '.join(map(str, targets)))
        else:
            self.assertTrue(not file_tracker , msg="Found sstate files in the wrong place for: %s" % ', '.join(map(str, targets)))

    @testcase(975)
    def test_sstate_creation_distro_specific_pass(self):
        targetarch = get_bb_var('TUNE_ARCH')
        self.run_test_sstate_creation(['binutils-cross-'+ targetarch, 'binutils-native'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True)

    @testcase(975)
    def test_sstate_creation_distro_specific_fail(self):
        targetarch = get_bb_var('TUNE_ARCH')
        self.run_test_sstate_creation(['binutils-cross-'+ targetarch, 'binutils-native'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True, should_pass=False)

    @testcase(976)
    def test_sstate_creation_distro_nonspecific_pass(self):
        self.run_test_sstate_creation(['glibc-initial'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True)

    @testcase(976)
    def test_sstate_creation_distro_nonspecific_fail(self):
        self.run_test_sstate_creation(['glibc-initial'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True, should_pass=False)


    # Test the sstate files deletion part of the do_cleansstate task
    def run_test_cleansstate_task(self, targets, distro_specific=True, distro_nonspecific=True, temp_sstate_location=True):
        self.config_sstate(temp_sstate_location)

        bitbake(['-ccleansstate'] + targets)

        bitbake(targets)
        tgz_created = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(tgz_created, msg="Could not find sstate .tgz files for: %s" % ', '.join(map(str, targets)))

        siginfo_created = self.search_sstate('|'.join(map(str, [s + '.*?\.siginfo$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(siginfo_created, msg="Could not find sstate .siginfo files for: %s" % ', '.join(map(str, targets)))

        bitbake(['-ccleansstate'] + targets)
        tgz_removed = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific, distro_nonspecific)
        self.assertTrue(not tgz_removed, msg="do_cleansstate didn't remove .tgz sstate files for: %s" % ', '.join(map(str, targets)))

    @testcase(977)
    def test_cleansstate_task_distro_specific_nonspecific(self):
        targetarch = get_bb_var('TUNE_ARCH')
        self.run_test_cleansstate_task(['binutils-cross-' + targetarch, 'binutils-native', 'glibc-initial'], distro_specific=True, distro_nonspecific=True, temp_sstate_location=True)

    @testcase(977)
    def test_cleansstate_task_distro_nonspecific(self):
        self.run_test_cleansstate_task(['glibc-initial'], distro_specific=False, distro_nonspecific=True, temp_sstate_location=True)

    @testcase(977)
    def test_cleansstate_task_distro_specific(self):
        targetarch = get_bb_var('TUNE_ARCH')
        self.run_test_cleansstate_task(['binutils-cross-'+ targetarch, 'binutils-native', 'glibc-initial'], distro_specific=True, distro_nonspecific=False, temp_sstate_location=True)


    # Test rebuilding of distro-specific sstate files
    def run_test_rebuild_distro_specific_sstate(self, targets, temp_sstate_location=True):
        self.config_sstate(temp_sstate_location)

        bitbake(['-ccleansstate'] + targets)

        bitbake(targets)
        self.assertTrue(self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific=False, distro_nonspecific=True) == [], msg="Found distro non-specific sstate for: %s" % ', '.join(map(str, targets)))
        file_tracker_1 = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific=True, distro_nonspecific=False)
        self.assertTrue(len(file_tracker_1) >= len(targets), msg = "Not all sstate files ware created for: %s" % ', '.join(map(str, targets)))

        self.track_for_cleanup(self.distro_specific_sstate + "_old")
        shutil.copytree(self.distro_specific_sstate, self.distro_specific_sstate + "_old")
        shutil.rmtree(self.distro_specific_sstate)

        bitbake(['-cclean'] + targets)
        bitbake(targets)
        file_tracker_2 = self.search_sstate('|'.join(map(str, [s + '.*?\.tgz$' for s in targets])), distro_specific=True, distro_nonspecific=False)
        self.assertTrue(len(file_tracker_2) >= len(targets), msg = "Not all sstate files ware created for: %s" % ', '.join(map(str, targets)))

        not_recreated = [x for x in file_tracker_1 if x not in file_tracker_2]
        self.assertTrue(not_recreated == [], msg="The following sstate files ware not recreated: %s" % ', '.join(map(str, not_recreated)))

        created_once = [x for x in file_tracker_2 if x not in file_tracker_1]
        self.assertTrue(created_once == [], msg="The following sstate files ware created only in the second run: %s" % ', '.join(map(str, created_once)))

    @testcase(175)
    def test_rebuild_distro_specific_sstate_cross_native_targets(self):
        targetarch = get_bb_var('TUNE_ARCH')
        self.run_test_rebuild_distro_specific_sstate(['binutils-cross-' + targetarch, 'binutils-native'], temp_sstate_location=True)

    @testcase(175)
    def test_rebuild_distro_specific_sstate_cross_target(self):
        targetarch = get_bb_var('TUNE_ARCH')
        self.run_test_rebuild_distro_specific_sstate(['binutils-cross-' + targetarch], temp_sstate_location=True)

    @testcase(175)
    def test_rebuild_distro_specific_sstate_native_target(self):
        self.run_test_rebuild_distro_specific_sstate(['binutils-native'], temp_sstate_location=True)


    # Test the sstate-cache-management script. Each element in the global_config list is used with the corresponding element in the target_config list
    # global_config elements are expected to not generate any sstate files that would be removed by sstate-cache-management.sh (such as changing the value of MACHINE)
    def run_test_sstate_cache_management_script(self, target, global_config=[''], target_config=[''], ignore_patterns=[]):
        self.assertTrue(global_config)
        self.assertTrue(target_config)
        self.assertTrue(len(global_config) == len(target_config), msg='Lists global_config and target_config should have the same number of elements')
        self.config_sstate(temp_sstate_location=True, add_local_mirrors=[self.sstate_path])

        # If buildhistory is enabled, we need to disable version-going-backwards QA checks for this test. It may report errors otherwise.
        if ('buildhistory' in get_bb_var('USER_CLASSES')) or ('buildhistory' in get_bb_var('INHERIT')):
            remove_errors_config = 'ERROR_QA_remove = "version-going-backwards"'
            self.append_config(remove_errors_config)

        # For not this only checks if random sstate tasks are handled correctly as a group.
        # In the future we should add control over what tasks we check for.

        sstate_archs_list = []
        expected_remaining_sstate = []
        for idx in range(len(target_config)):
            self.append_config(global_config[idx])
            self.append_recipeinc(target, target_config[idx])
            sstate_arch = get_bb_var('SSTATE_PKGARCH', target)
            if not sstate_arch in sstate_archs_list:
                sstate_archs_list.append(sstate_arch)
            if target_config[idx] == target_config[-1]:
                target_sstate_before_build = self.search_sstate(target + '.*?\.tgz$')
            bitbake("-cclean %s" % target)
            result = bitbake(target, ignore_status=True)
            if target_config[idx] == target_config[-1]:
                target_sstate_after_build = self.search_sstate(target + '.*?\.tgz$')
                expected_remaining_sstate += [x for x in target_sstate_after_build if x not in target_sstate_before_build if not any(pattern in x for pattern in ignore_patterns)]
            self.remove_config(global_config[idx])
            self.remove_recipeinc(target, target_config[idx])
            self.assertEqual(result.status, 0, msg = "build of %s failed with %s" % (target, result.output))

        runCmd("sstate-cache-management.sh -y --cache-dir=%s --remove-duplicated --extra-archs=%s" % (self.sstate_path, ','.join(map(str, sstate_archs_list))))
        actual_remaining_sstate = [x for x in self.search_sstate(target + '.*?\.tgz$') if not any(pattern in x for pattern in ignore_patterns)]

        actual_not_expected = [x for x in actual_remaining_sstate if x not in expected_remaining_sstate]
        self.assertFalse(actual_not_expected, msg="Files should have been removed but ware not: %s" % ', '.join(map(str, actual_not_expected)))
        expected_not_actual = [x for x in expected_remaining_sstate if x not in actual_remaining_sstate]
        self.assertFalse(expected_not_actual, msg="Extra files ware removed: %s" ', '.join(map(str, expected_not_actual)))

    @testcase(973)
    def test_sstate_cache_management_script_using_pr_1(self):
        global_config = []
        target_config = []
        global_config.append('')
        target_config.append('PR = "0"')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    @testcase(978)
    def test_sstate_cache_management_script_using_pr_2(self):
        global_config = []
        target_config = []
        global_config.append('')
        target_config.append('PR = "0"')
        global_config.append('')
        target_config.append('PR = "1"')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    @testcase(979)
    def test_sstate_cache_management_script_using_pr_3(self):
        global_config = []
        target_config = []
        global_config.append('MACHINE = "qemux86-64"')
        target_config.append('PR = "0"')
        global_config.append(global_config[0])
        target_config.append('PR = "1"')
        global_config.append('MACHINE = "qemux86"')
        target_config.append('PR = "1"')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    @testcase(974)
    def test_sstate_cache_management_script_using_machine(self):
        global_config = []
        target_config = []
        global_config.append('MACHINE = "qemux86-64"')
        target_config.append('')
        global_config.append('MACHINE = "qemux86"')
        target_config.append('')
        self.run_test_sstate_cache_management_script('m4', global_config,  target_config, ignore_patterns=['populate_lic'])

    @testcase(1270)
    def test_sstate_32_64_same_hash(self):
        """
        The sstate checksums for both native and target should not vary whether
        they're built on a 32 or 64 bit system. Rather than requiring two different 
        build machines and running a builds, override the variables calling uname()
        manually and check using bitbake -S.
        """

        topdir = get_bb_var('TOPDIR')
        targetvendor = get_bb_var('TARGET_VENDOR')
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
BUILD_ARCH = \"x86_64\"
BUILD_OS = \"linux\"
""")
        self.track_for_cleanup(topdir + "/tmp-sstatesamehash")
        bitbake("core-image-sato -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
BUILD_ARCH = \"i686\"
BUILD_OS = \"linux\"
""")
        self.track_for_cleanup(topdir + "/tmp-sstatesamehash2")
        bitbake("core-image-sato -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                f.extend(os.path.join(root, name) for name in files)
            return f
        files1 = get_files(topdir + "/tmp-sstatesamehash/stamps/")
        files2 = get_files(topdir + "/tmp-sstatesamehash2/stamps/")
        files2 = [x.replace("tmp-sstatesamehash2", "tmp-sstatesamehash").replace("i686-linux", "x86_64-linux").replace("i686" + targetvendor + "-linux", "x86_64" + targetvendor + "-linux", ) for x in files2]
        self.assertItemsEqual(files1, files2)


    @testcase(1271)
    def test_sstate_nativelsbstring_same_hash(self):
        """
        The sstate checksums should be independent of whichever NATIVELSBSTRING is
        detected. Rather than requiring two different build machines and running 
        builds, override the variables manually and check using bitbake -S.
        """

        topdir = get_bb_var('TOPDIR')
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
NATIVELSBSTRING = \"DistroA\"
""")
        self.track_for_cleanup(topdir + "/tmp-sstatesamehash")
        bitbake("core-image-sato -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
NATIVELSBSTRING = \"DistroB\"
""")
        self.track_for_cleanup(topdir + "/tmp-sstatesamehash2")
        bitbake("core-image-sato -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                f.extend(os.path.join(root, name) for name in files)
            return f
        files1 = get_files(topdir + "/tmp-sstatesamehash/stamps/")
        files2 = get_files(topdir + "/tmp-sstatesamehash2/stamps/")
        files2 = [x.replace("tmp-sstatesamehash2", "tmp-sstatesamehash") for x in files2]
        self.assertItemsEqual(files1, files2)

    def test_sstate_allarch_samesigs(self):
        """
        The sstate checksums off allarch packages should be independent of whichever 
        MACHINE is set. Check this using bitbake -S.
        """

        topdir = get_bb_var('TOPDIR')
        targetos = get_bb_var('TARGET_OS')
        targetvendor = get_bb_var('TARGET_VENDOR')
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash\"
MACHINE = \"qemux86\"
""")
        self.track_for_cleanup(topdir + "/tmp-sstatesamehash")
        bitbake("world -S none")
        self.write_config("""
TMPDIR = \"${TOPDIR}/tmp-sstatesamehash2\"
MACHINE = \"qemuarm\"
""")
        self.track_for_cleanup(topdir + "/tmp-sstatesamehash2")
        bitbake("world -S none")

        def get_files(d):
            f = []
            for root, dirs, files in os.walk(d):
                for name in files:
                    if "do_build" not in name:
                        f.append(os.path.join(root, name))
            return f
        files1 = get_files(topdir + "/tmp-sstatesamehash/stamps/all" + targetvendor + "-" + targetos)
        files2 = get_files(topdir + "/tmp-sstatesamehash2/stamps/all" + targetvendor + "-" + targetos)
        files2 = [x.replace("tmp-sstatesamehash2", "tmp-sstatesamehash") for x in files2]
        self.maxDiff = None
        self.assertItemsEqual(files1, files2)
