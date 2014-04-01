SUMMARY = "Advanced tools for certain ALSA sound card drivers"
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "https://bugtrack.alsa-project.org/alsa-bug/login_page.php"
SECTION = "console/utils"
LICENSE = "GPLv2 & LGPLv2+"
DEPENDS = "alsa-lib ncurses"

LIC_FILES_CHKSUM = "file://hdsploader/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://ld10k1/COPYING.LIB;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "ftp://ftp.alsa-project.org/pub/tools/alsa-tools-${PV}.tar.bz2 \
           file://mips_has_no_io_h.patch \
           file://autotools.patch \
           ${@base_contains('DISTRO_FEATURES', 'x11', '', 'file://makefile_no_gtk.patch', d)}"

SRC_URI[md5sum] = "1ea381d00a6069a98613aa7effa4cb51"
SRC_URI[sha256sum] = "6562611b5a6560712f109e09740a9d4fa47296b07ed9590cb44139c5f154ada2"

inherit autotools-brokensep

EXTRA_OEMAKE += "GITCOMPILE_ARGS='--host=${HOST_SYS} --build=${BUILD_SYS} --target=${TARGET_SYS} --with-libtool-sysroot=${STAGING_DIR_HOST} --prefix=${prefix}'"

PACKAGECONFIG ??= "${@base_contains('DISTRO_FEATURES', 'x11', 'gtk+', '', d)}"
PACKAGECONFIG[gtk+] = ",,gtk+ gtk+3,"

# configure.ac/.in doesn't exist so force copy
AUTOTOOLS_COPYACLOCAL = "1"

do_compile_prepend () {
    #Automake dir is not correctly detected in cross compilation case
    export AUTOMAKE_DIR="$(automake --print-libdir)"
    export ACLOCAL_FLAGS="--system-acdir=${ACLOCALDIR}/"
}

FILES_${PN} += "${datadir}/ld10k1"
