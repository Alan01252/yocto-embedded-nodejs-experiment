SUMMARY = "GRUB2 is the next-generation GRand Unified Bootloader"

DESCRIPTION = "GRUB2 is the next generaion of a GPLed bootloader \
intended to unify bootloading across x86 operating systems. In \
addition to loading the Linux kernel, it implements the Multiboot \
standard, which allows for flexible loading of multiple boot images."

HOMEPAGE = "http://www.gnu.org/software/grub/"
SECTION = "bootloaders"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "autogen-native flex-native"
RDEPENDS_${PN} = "diffutils freetype"
PR = "r0"

SRC_URI = "ftp://ftp.gnu.org/gnu/grub/grub-${PV}.tar.gz \
          file://grub-install.in.patch \
          file://grub-2.00-fpmath-sse-387-fix.patch \
          file://remove-gets.patch \
          file://40_custom"

SRC_URI[md5sum] = "e927540b6eda8b024fb0391eeaa4091c"
SRC_URI[sha256sum] = "65b39a0558f8c802209c574f4d02ca263a804e8a564bc6caf1cd0fd3b3cc11e3"

COMPATIBLE_HOST = '(x86_64.*|i.86.*)-(linux|freebsd.*)'

FILES_${PN}-dbg += "${libdir}/${BPN}/i386-pc/.debug"


inherit autotools
inherit gettext

EXTRA_OECONF = "--with-platform=pc --disable-grub-mkfont --program-prefix="""

do_install_append () {
    install -d ${D}${sysconfdir}/grub.d
    install -m 0755 ${WORKDIR}/40_custom ${D}${sysconfdir}/grub.d/40_custom
}

INSANE_SKIP_${PN} = "arch"
INSANE_SKIP_${PN}-dbg = "arch"
