# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#

import os

from wic import msger
from wic.pluginbase import SourcePlugin
from wic.utils.oe.misc import *

class RawCopyPlugin(SourcePlugin):
    name = 'rawcopy'

    @classmethod
    def do_install_disk(self, disk, disk_name, cr, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image. Do nothing.
        """
        pass

    @classmethod
    def do_configure_partition(self, part, source_params, cr, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        """
        Called before do_prepare_partition(). Possibly prepare
        configuration files of some sort.
        """
        pass

    @classmethod
    def do_prepare_partition(self, part, source_params, cr, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        if not bootimg_dir:
            bootimg_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not bootimg_dir:
                msger.error("Couldn't find DEPLOY_DIR_IMAGE, exiting\n")

        msger.debug('Bootimg dir: %s' % bootimg_dir)

        if ('file' not in source_params):
            msger.error("No file specified\n")
            return

        src = os.path.join(bootimg_dir, source_params['file'])
        dst = src

        if ('skip' in source_params):
            dst = os.path.join(cr_workdir, source_params['file'])
            dd_cmd = "dd if=%s of=%s ibs=%s skip=1 conv=notrunc" % \
                    (src, dst, source_params['skip'])
            exec_cmd(dd_cmd)

        # get the size in the right units for kickstart (kB)
        du_cmd = "du -Lbks %s" % dst
        out = exec_cmd(du_cmd)
        filesize = out.split()[0]

        if filesize > part.size:
            part.size = filesize

        part.source_file = dst

