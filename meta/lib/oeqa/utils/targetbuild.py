# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# Provides a class for automating build tests for projects

import os
import re
import bb.utils
import subprocess
from abc import ABCMeta, abstractmethod

class BuildProject():

    __metaclass__ = ABCMeta

    def __init__(self, d, uri, foldername=None, tmpdir="/tmp/"):
        self.d = d
        self.uri = uri
        self.archive = os.path.basename(uri)
        self.localarchive = os.path.join(tmpdir,self.archive)
        self.fname = re.sub(r'.tar.bz2|tar.gz$', '', self.archive)
        if foldername:
            self.fname = foldername

    # Download self.archive to self.localarchive
    def _download_archive(self):

        dl_dir = self.d.getVar("DL_DIR", True)
        if dl_dir and os.path.exists(os.path.join(dl_dir, self.archive)):
            bb.utils.copyfile(os.path.join(dl_dir, self.archive), self.localarchive)
            return

        exportvars = ['HTTP_PROXY', 'http_proxy',
                      'HTTPS_PROXY', 'https_proxy',
                      'FTP_PROXY', 'ftp_proxy',
                      'FTPS_PROXY', 'ftps_proxy',
                      'NO_PROXY', 'no_proxy',
                      'ALL_PROXY', 'all_proxy',
                      'SOCKS5_USER', 'SOCKS5_PASSWD']

        cmd = ''
        for var in exportvars:
            val = self.d.getVar(var, True)
            if val:
                cmd = 'export ' + var + '=\"%s\"; %s' % (val, cmd)

        cmd = cmd + "wget -O %s %s" % (self.localarchive, self.uri)
        subprocess.check_call(cmd, shell=True)

    # This method should provide a way to run a command in the desired environment.
    @abstractmethod
    def _run(self, cmd):
        pass

    # The timeout parameter of target.run is set to 0 to make the ssh command
    # run with no timeout.
    def run_configure(self, configure_args=''):
        return self._run('cd %s; ./configure %s' % (self.targetdir, configure_args))

    def run_make(self, make_args=''):
        return self._run('cd %s; make %s' % (self.targetdir, make_args))

    def run_install(self, install_args=''):
        return self._run('cd %s; make install %s' % (self.targetdir, install_args))

    def clean(self):
        self._run('rm -rf %s' % self.targetdir)
        subprocess.call('rm -f %s' % self.localarchive, shell=True)
        pass

class TargetBuildProject(BuildProject):

    def __init__(self, target, d, uri, foldername=None):
        self.target = target
        self.targetdir = "~/"
        BuildProject.__init__(self, d, uri, foldername, tmpdir="/tmp")

    def download_archive(self):

        self._download_archive()

        (status, output) = self.target.copy_to(self.localarchive, self.targetdir)
        if status != 0:
            raise Exception("Failed to copy archive to target, output: %s" % output)

        (status, output) = self.target.run('tar xf %s%s -C %s' % (self.targetdir, self.archive, self.targetdir))
        if status != 0:
            raise Exception("Failed to extract archive, output: %s" % output)

        #Change targetdir to project folder
        self.targetdir = self.targetdir + self.fname

    # The timeout parameter of target.run is set to 0 to make the ssh command
    # run with no timeout.
    def _run(self, cmd):
        return self.target.run(cmd, 0)[0]


class SDKBuildProject(BuildProject):

    def __init__(self, testpath, sdkenv, d, uri, foldername=None):
        self.sdkenv = sdkenv
        self.testdir = testpath
        self.targetdir = testpath
        bb.utils.mkdirhier(testpath)
        self.datetime = d.getVar('DATETIME', True)
        self.testlogdir = d.getVar("TEST_LOG_DIR", True)
        bb.utils.mkdirhier(self.testlogdir)
        self.logfile = os.path.join(self.testlogdir, "sdk_target_log.%s" % self.datetime)
        BuildProject.__init__(self, d, uri, foldername, tmpdir=testpath)

    def download_archive(self):

        self._download_archive()

        cmd = 'tar xf %s%s -C %s' % (self.targetdir, self.archive, self.targetdir)
        subprocess.check_call(cmd, shell=True)

        #Change targetdir to project folder
        self.targetdir = self.targetdir + self.fname

    def run_configure(self, configure_args=''):
        return super(SDKBuildProject, self).run_configure(configure_args=(configure_args or '$CONFIGURE_FLAGS'))

    def run_install(self, install_args=''):
        return super(SDKBuildProject, self).run_install(install_args=(install_args or "DESTDIR=%s/../install" % self.targetdir))

    def log(self, msg):
        if self.logfile:
            with open(self.logfile, "a") as f:
               f.write("%s\n" % msg)

    def _run(self, cmd):
        self.log("Running source %s; " % self.sdkenv + cmd)
        return subprocess.call("source %s; " % self.sdkenv + cmd, shell=True)

