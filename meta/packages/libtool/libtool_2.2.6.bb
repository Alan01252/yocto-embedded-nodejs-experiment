require libtool.inc

PR = "r15"

SRC_URI = "${GNU_MIRROR}/libtool/libtool-${PV}a.tar.gz \
           file://dolt.m4"

PACKAGES =+ "libltdl libltdl-dev libltdl-dbg"
FILES_${PN} += "${datadir}/aclocal*"
FILES_libltdl = "${libdir}/libltdl.so.*"
FILES_libltdl-dev = "${libdir}/libltdl.* ${includedir}/ltdl.h"
FILES_libltdl-dbg = "${libdir}/.debug/"

inherit autotools_stage

EXTRA_AUTORECONF = "--exclude=libtoolize"



#
# We want the results of libtool-cross preserved - don't stage anything ourselves.
#
SYSROOT_PREPROCESS_FUNCS += "libtool_sysroot_preprocess"

libtool_sysroot_preprocess () {
	rm -rf ${SYSROOT_DESTDIR}/*
}