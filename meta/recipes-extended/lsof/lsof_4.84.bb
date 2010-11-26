DESCRIPTION = "Lsof is a Unix-specific diagnostic tool. \
Its name stands for LiSt Open Files, and it does just that."
SECTION = "devel"
LICENSE = "BSD"
PR = "r0"

SRC_URI = "ftp://lsof.itap.purdue.edu/pub/tools/unix/lsof/lsof_${PV}.tar.bz2"
LOCALSRC = "file://${WORKDIR}/lsof_${PV}/lsof_${PV}_src.tar"
S = "${WORKDIR}/lsof_${PV}_src"

LIC_FILES_CHKSUM = "file://${S}/00README;beginline=645;endline=679;md5=e0108f7811919035515a97f872eb8ee2"

python do_unpack () {
    bb.build.exec_func('base_do_unpack', d)
    src_uri = bb.data.getVar('SRC_URI', d)
    bb.data.setVar('SRC_URI', '${LOCALSRC}', d)
    bb.build.exec_func('base_do_unpack', d)
    bb.data.setVar('SRC_URI', src_uri, d)
}

export LSOF_OS = "${TARGET_OS}"
LSOF_OS_linux-uclibc = "linux"
LSOF_OS_linux-gnueabi = "linux"
export LSOF_INCLUDE = "${STAGING_INCDIR}"

do_configure () {
	yes | ./Configure ${LSOF_OS}
}

export I = "${STAGING_INCDIR}"
export L = "${STAGING_INCDIR}"
export EXTRA_OEMAKE = ""

do_compile () {
	oe_runmake 'CC=${CC}' 'CFGL=${LDFLAGS} -L./lib -llsof' 'DEBUG=' 'INCL=${CFLAGS}'
}

do_install () {
	install -d ${D}${sbindir} ${D}${mandir}/man8
	install -m 4755 lsof ${D}${sbindir}/lsof
	install -m 0644 lsof.8 ${D}${mandir}/man8/lsof.8
}
