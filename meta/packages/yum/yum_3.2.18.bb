HOMEPAGE = "http://linux.duke.edu/projects/yum/"
PR = "r3"

SRC_URI = "http://linux.duke.edu/projects/yum/download/3.2/yum-${PV}.tar.gz \
           file://paths.patch;patch=1 \
           file://paths2.patch;patch=1 \
	   file://yum-install-recommends.py \
	   file://extract-postinst.awk"

DEPENDS = "python"
RDEPENDS = "rpm python-core python-iniparse python-urlgrabber yum-metadata-parser"

S = "${WORKDIR}/yum-${PV}"

inherit autotools

do_compile_append () {
	sed -e 's#!/usr/bin/python#!${bindir}/python#' -e 's#/usr/share#${datadir}#' -i ${S}/bin/yum.py
	sed -e 's#!/usr/bin/python#!${bindir}/python#' -e 's#/usr/share#${datadir}#' -i ${S}/bin/yum-updatesd.py
}

do_install_append () {
	install -d ${D}${bindir}/
	install ${WORKDIR}/extract-postinst.awk ${D}${bindir}/
	install ${WORKDIR}/yum-install-recommends.py ${D}${bindir}/
}

FILES_${PN} += "${libdir}/python* ${datadir}/yum-cli"
