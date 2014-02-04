#!/usr/bin/python -tt
#
# Copyright (c) 2011 Intel, Inc.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; version 2 of the License
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc., 59
# Temple Place - Suite 330, Boston, MA 02111-1307, USA.

import os
import shutil
from mic import msger
from mic.utils import errors

class _Plugin(object):
    class __metaclass__(type):
        def __init__(cls, name, bases, attrs):
            if not hasattr(cls, 'plugins'):
                cls.plugins = {}

            elif 'mic_plugin_type' in attrs:
                if attrs['mic_plugin_type'] not in cls.plugins:
                    cls.plugins[attrs['mic_plugin_type']] = {}

            elif hasattr(cls, 'mic_plugin_type') and 'name' in attrs:
                cls.plugins[cls.mic_plugin_type][attrs['name']] = cls

        def show_plugins(cls):
            for cls in cls.plugins[cls.mic_plugin_type]:
                print cls

        def get_plugins(cls):
            return cls.plugins

class ImagerPlugin(_Plugin):
    mic_plugin_type = "imager"

    @classmethod
    def check_image_exists(self, destdir, apacking=None,
                                          images=(),
                                          release=None):

        # if it's a packing file, reset images
        if apacking:
            images = [apacking]

        # release option will override images
        if release is not None:
            images = [os.path.basename(destdir.rstrip('/'))]
            destdir = os.path.dirname(destdir.rstrip('/'))

        for name in images:
            if not name:
                continue

            image = os.path.join(destdir, name)
            if not os.path.exists(image):
                continue

            if msger.ask("Target image/dir: %s already exists, "
                         "clean up and continue?" % image):
                if os.path.isdir(image):
                    shutil.rmtree(image)
                else:
                    os.unlink(image)
            else:
                raise errors.Abort("Cancled")

    def do_create(self):
        pass

    def do_chroot(self):
        pass

class SourcePlugin(_Plugin):
    mic_plugin_type = "source"
    """
    The methods that can be implemented by --source plugins.

    Any methods not implemented in a subclass inherit these.
    """

    @classmethod
    def do_install_disk(self, disk, disk_name, cr, workdir, oe_builddir,
                        bootimg_dir, kernel_dir, native_sysroot):
        """
        Called after all partitions have been prepared and assembled into a
        disk image.  This provides a hook to allow finalization of a
        disk image e.g. to write an MBR to it.
        """
        msger.debug("SourcePlugin: do_install_disk: disk: %s" % disk_name)

    @classmethod
    def do_stage_partition(self, part, cr, workdir, oe_builddir, bootimg_dir,
                           kernel_dir, native_sysroot):
        """
        Special content staging hook called before do_prepare_partition(),
        normally empty.

        Typically, a partition will just use the passed-in parame e.g
        straight bootimg_dir, etc, but in some cases, things need to
        be more tailored e.g. to use a deploy dir + /boot, etc.  This
        hook allows those files to be staged in a customized fashion.
        Not that get_bitbake_var() allows you to acces non-standard
        variables that you might want to use for this.
        """
        msger.debug("SourcePlugin: do_stage_partition: part: %s" % part)

    @classmethod
    def do_configure_partition(self, part, cr, cr_workdir, oe_builddir,
                               bootimg_dir, kernel_dir, native_sysroot):
        """
        Called before do_prepare_partition(), typically used to create
        custom configuration files for a partition, for example
        syslinux or grub config files.
        """
        msger.debug("SourcePlugin: do_configure_partition: part: %s" % part)

    @classmethod
    def do_prepare_partition(self, part, cr, cr_workdir, oe_builddir, bootimg_dir,
                             kernel_dir, native_sysroot):
        """
        Called to do the actual content population for a partition i.e. it
        'prepares' the partition to be incorporated into the image.
        """
        msger.debug("SourcePlugin: do_prepare_partition: part: %s" % part)

class BackendPlugin(_Plugin):
    mic_plugin_type="backend"

    def addRepository(self):
        pass

def get_plugins(typen):
    ps = ImagerPlugin.get_plugins()
    if typen in ps:
        return ps[typen]
    else:
        return None

__all__ = ['ImagerPlugin', 'BackendPlugin', 'SourcePlugin', 'get_plugins']
