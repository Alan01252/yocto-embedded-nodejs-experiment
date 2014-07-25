import unittest
import re
from oeqa.oetest import oeRuntimeTest, skipModule
from oeqa.utils.decorators import *
from oeqa.utils.httpserver import HTTPService

def setUpModule():
    if not oeRuntimeTest.hasFeature("package-management"):
        skipModule("Image doesn't have package management feature")
    if not oeRuntimeTest.hasPackage("smart"):
        skipModule("Image doesn't have smart installed")
    if "package_rpm" != oeRuntimeTest.tc.d.getVar("PACKAGE_CLASSES", True).split()[0]:
        skipModule("Rpm is not the primary package manager")

class SmartTest(oeRuntimeTest):

    @skipUnlessPassed('test_smart_help')
    def smart(self, command, expected = 0):
        command = 'smart %s' % command
        status, output = self.target.run(command, 1500)
        message = os.linesep.join([command, output])
        self.assertEqual(status, expected, message)
        self.assertFalse("Cannot allocate memory" in output, message)
        return output

class SmartBasicTest(SmartTest):

    @testcase(716)
    @skipUnlessPassed('test_ssh')
    def test_smart_help(self):
        self.smart('--help')

    def test_smart_version(self):
        self.smart('--version')

    @testcase(721)
    def test_smart_info(self):
        self.smart('info python-smartpm')

    @testcase(421)
    def test_smart_query(self):
        self.smart('query python-smartpm')

    @testcase(720)
    def test_smart_search(self):
        self.smart('search python-smartpm')

    @testcase(722)
    def test_smart_stats(self):
        self.smart('stats')

class SmartRepoTest(SmartTest):

    @classmethod
    def setUpClass(self):
        self.repo_server = HTTPService(oeRuntimeTest.tc.d.getVar('DEPLOY_DIR', True), oeRuntimeTest.tc.target.server_ip)
        self.repo_server.start()

    @classmethod
    def tearDownClass(self):
        self.repo_server.stop()

    def test_smart_channel(self):
        self.smart('channel', 1)

    @testcase(719)
    def test_smart_channel_add(self):
        image_pkgtype = self.tc.d.getVar('IMAGE_PKGTYPE', True)
        deploy_url = 'http://%s:%s/%s' %(self.target.server_ip, self.repo_server.port, image_pkgtype)
        pkgarchs = self.tc.d.getVar('PACKAGE_ARCHS', True).replace("-","_").split()
        for arch in os.listdir('%s/%s' % (self.repo_server.root_dir, image_pkgtype)):
            if arch in pkgarchs:
                self.smart('channel -y --add {a} type=rpm-md baseurl={u}/{a}'.format(a=arch, u=deploy_url))
        self.smart('update')

    def test_smart_channel_help(self):
        self.smart('channel --help')

    def test_smart_channel_list(self):
        self.smart('channel --list')

    def test_smart_channel_show(self):
        self.smart('channel --show')

    @testcase(717)
    def test_smart_channel_rpmsys(self):
        self.smart('channel --show rpmsys')
        self.smart('channel --disable rpmsys')
        self.smart('channel --enable rpmsys')

    @skipUnlessPassed('test_smart_channel_add')
    def test_smart_install(self):
        self.smart('remove -y psplash-default')
        self.smart('install -y psplash-default')

    @testcase(728)
    @skipUnlessPassed('test_smart_install')
    def test_smart_install_dependency(self):
        self.smart('remove -y psplash')
        self.smart('install -y psplash-default')

    @testcase(723)
    @skipUnlessPassed('test_smart_channel_add')
    def test_smart_install_from_disk(self):
        self.smart('remove -y psplash-default')
        self.smart('download psplash-default')
        self.smart('install -y ./psplash-default*')

    @testcase(725)
    @skipUnlessPassed('test_smart_channel_add')
    def test_smart_install_from_http(self):
        output = self.smart('download --urls psplash-default')
        url = re.search('(http://.*/psplash-default.*\.rpm)', output)
        self.assertTrue(url, msg="Couln't find download url in %s" % output)
        self.smart('remove -y psplash-default')
        self.smart('install -y %s' % url.group(0))

    @testcase(729)
    @skipUnlessPassed('test_smart_install')
    def test_smart_reinstall(self):
        self.smart('reinstall -y psplash-default')
