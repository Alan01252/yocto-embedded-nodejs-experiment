from oeqa.selftest.base import oeSelfTest
from oeqa.utils.commands import runCmd, bitbake, get_bb_var
import os
import glob
import re
import shutil
import tempfile
from oeqa.utils.decorators import testcase


class Signing(oeSelfTest):

    gpg_dir = ""
    pub_key_name = 'key.pub'
    secret_key_name = 'key.secret'

    @classmethod
    def setUpClass(cls):
        # Import the gpg keys

        cls.gpg_dir = os.path.join(cls.testlayer_path, 'files/signing/')

        # key.secret key.pub are located in gpg_dir
        pub_key_location = cls.gpg_dir + cls.pub_key_name
        secret_key_location = cls.gpg_dir + cls.secret_key_name
        runCmd('gpg --homedir %s --import %s %s' % (cls.gpg_dir, pub_key_location, secret_key_location))

    @classmethod
    def tearDownClass(cls):
        # Delete the files generated by 'gpg --import'

        gpg_files = glob.glob(cls.gpg_dir + '*.gpg*')
        random_seed_file = cls.gpg_dir + 'random_seed'
        gpg_files.append(random_seed_file)

        for gpg_file in gpg_files:
            runCmd('rm -f ' + gpg_file)

    @testcase(1362)
    def test_signing_packages(self):
        """
        Summary:     Test that packages can be signed in the package feed
        Expected:    Package should be signed with the correct key
        Product:     oe-core
        Author:      Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        package_classes = get_bb_var('PACKAGE_CLASSES')
        if 'package_rpm' not in package_classes:
            self.skipTest('This test requires RPM Packaging.')

        test_recipe = 'ed'

        feature = 'INHERIT += "sign_rpm"\n'
        feature += 'RPM_GPG_PASSPHRASE_FILE = "%ssecret.txt"\n' % self.gpg_dir
        feature += 'RPM_GPG_NAME = "testuser"\n'
        feature += 'RPM_GPG_PUBKEY = "%s%s"\n' % (self.gpg_dir, self.pub_key_name)
        feature += 'GPG_PATH = "%s"\n' % self.gpg_dir

        self.write_config(feature)

        bitbake('-c cleansstate %s' % test_recipe)
        bitbake(test_recipe)
        self.add_command_to_tearDown('bitbake -c clean %s' % test_recipe)

        pf = get_bb_var('PF', test_recipe)
        deploy_dir_rpm = get_bb_var('DEPLOY_DIR_RPM', test_recipe)
        package_arch = get_bb_var('PACKAGE_ARCH', test_recipe).replace('-', '_')
        staging_bindir_native = get_bb_var('STAGING_BINDIR_NATIVE')

        pkg_deploy = os.path.join(deploy_dir_rpm, package_arch, '.'.join((pf, package_arch, 'rpm')))

        # Use a temporary rpmdb
        rpmdb = tempfile.mkdtemp(prefix='oeqa-rpmdb')

        runCmd('%s/rpm --define "_dbpath %s" --import %s%s' %
               (staging_bindir_native, rpmdb, self.gpg_dir, self.pub_key_name))

        ret = runCmd('%s/rpm --define "_dbpath %s" --checksig %s' %
                     (staging_bindir_native, rpmdb, pkg_deploy))
        # tmp/deploy/rpm/i586/ed-1.9-r0.i586.rpm: rsa sha1 md5 OK
        self.assertIn('rsa sha1 md5 OK', ret.output, 'Package signed incorrectly.')
        shutil.rmtree(rpmdb)

    @testcase(1382)
    def test_signing_sstate_archive(self):
        """
        Summary:     Test that sstate archives can be signed
        Expected:    Package should be signed with the correct key
        Product:     oe-core
        Author:      Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        AutomatedBy: Daniel Istrate <daniel.alexandrux.istrate@intel.com>
        """

        test_recipe = 'ed'

        builddir = os.environ.get('BUILDDIR')
        sstatedir = os.path.join(builddir, 'test-sstate')

        self.add_command_to_tearDown('bitbake -c clean %s' % test_recipe)
        self.add_command_to_tearDown('bitbake -c cleansstate %s' % test_recipe)
        self.add_command_to_tearDown('rm -rf %s' % sstatedir)

        # Determine the pub key signature
        ret = runCmd('gpg --homedir %s --list-keys' % self.gpg_dir)
        pub_key = re.search(r'^pub\s+\S+/(\S+)\s+', ret.output, re.M)
        self.assertIsNotNone(pub_key, 'Failed to determine the public key signature.')
        pub_key = pub_key.group(1)

        feature = 'SSTATE_SIG_KEY ?= "%s"\n' % pub_key
        feature += 'SSTATE_SIG_PASSPHRASE ?= "test123"\n'
        feature += 'SSTATE_VERIFY_SIG ?= "1"\n'
        feature += 'GPG_PATH = "%s"\n' % self.gpg_dir
        feature += 'SSTATE_DIR = "%s"\n' % sstatedir

        self.write_config(feature)

        bitbake('-c cleansstate %s' % test_recipe)
        bitbake(test_recipe)

        recipe_sig = glob.glob(sstatedir + '/*/*:ed:*_package.tgz.sig')
        recipe_tgz = glob.glob(sstatedir + '/*/*:ed:*_package.tgz')

        self.assertEqual(len(recipe_sig), 1, 'Failed to find .sig file.')
        self.assertEqual(len(recipe_tgz), 1, 'Failed to find .tgz file.')

        ret = runCmd('gpg --homedir %s --verify %s %s' % (self.gpg_dir, recipe_sig[0], recipe_tgz[0]))
        # gpg: Signature made Thu 22 Oct 2015 01:45:09 PM EEST using RSA key ID 61EEFB30
        # gpg: Good signature from "testuser (nocomment) <testuser@email.com>"
        self.assertIn('gpg: Good signature from', ret.output, 'Package signed incorrectly.')

