SUMMARY = "Advanced tools for certain ALSA sound card drivers"
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "https://bugtrack.alsa-project.org/alsa-bug/login_page.php"
SECTION = "console/utils"
LICENSE = "GPLv2 & LGPLv2+"
DEPENDS = "alsa-lib ncurses glib-2.0"

LIC_FILES_CHKSUM = "file://hdsploader/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://ld10k1/COPYING.LIB;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "ftp://ftp.alsa-project.org/pub/tools/${BP}.tar.bz2 \
           file://autotools.patch \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', \
                                'file://makefile_no_gtk.patch', d)} \
           file://gitcompile_hdajacksensetest \
           file://0001-as10k1-Make-output_tram_line-static-inline.patch \
          "

SRC_URI[md5sum] = "f339a3cd24f748c9d007bdff0e98775b"
SRC_URI[sha256sum] = "94abf0ab5a73f0710c70d4fb3dc1003af5bae2d2ed721d59d245b41ad0f2fbd1"

inherit autotools-brokensep pkgconfig

CLEANBROKEN = "1"

EXTRA_OEMAKE += "GITCOMPILE_ARGS='--host=${HOST_SYS} --build=${BUILD_SYS} --target=${TARGET_SYS} --with-libtool-sysroot=${STAGING_DIR_HOST} --prefix=${prefix}'"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk+', '', d)}"
PACKAGECONFIG[gtk+] = ",,gtk+ gtk+3,"

# configure.ac/.in doesn't exist so force copy
AUTOTOOLS_COPYACLOCAL = "1"

do_compile_prepend () {
    #Automake dir is not correctly detected in cross compilation case
    export AUTOMAKE_DIR="$(automake --print-libdir)"
    export ACLOCAL_FLAGS="--system-acdir=${ACLOCALDIR}/"

    cp ${WORKDIR}/gitcompile_hdajacksensetest ${S}/hdajacksensetest/gitcompile
}

FILES_${PN} += "${datadir}/ld10k1"
