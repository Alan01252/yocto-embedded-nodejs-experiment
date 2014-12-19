SUMMARY = "Constructs the main configuration file for opkg"
SECTION = "base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
PR = "r2"

SRC_URI = "file://opkg.conf"

S = "${WORKDIR}"

OPKGLIBDIR = "${localstatedir}/lib"
do_compile () {
	echo "option lists_dir ${OPKGLIBDIR}/opkg" >>${WORKDIR}/opkg.conf
}

do_install () {
	install -d ${D}${sysconfdir}/opkg
	install -m 0644 ${WORKDIR}/opkg.conf ${D}${sysconfdir}/opkg/opkg.conf
}

CONFFILES_${PN} = "${sysconfdir}/opkg/opkg.conf"
