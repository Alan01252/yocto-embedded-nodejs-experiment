SUMMARY = "GNU grep utility"
DESCRIPTION = "GNU grep utility"
HOMEPAGE = "http://savannah.gnu.org/projects/grep/"
BUGTRACKER = "http://savannah.gnu.org/bugs/?group=grep"
SECTION = "console/utils"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=8006d9c814277c1bfc4ca22af94b59ee"

PR = "r2"

SRC_URI = "${GNU_MIRROR}/grep/grep-${PV}.tar.gz"

SRC_URI[md5sum] = "03e3451a38b0d615cb113cbeaf252dc0"
SRC_URI[sha256sum] = "e9118eac72ecc71191725a7566361ab7643edfd3364869a47b78dc934a357970"

inherit autotools gettext

EXTRA_OECONF = "--disable-perl-regexp"

do_configure_prepend () {
	rm -f ${S}/m4/init.m4
}

do_install () {
	autotools_do_install
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/grep ${D}${base_bindir}/grep
	mv ${D}${bindir}/egrep ${D}${base_bindir}/egrep
	mv ${D}${bindir}/fgrep ${D}${base_bindir}/fgrep
	rmdir ${D}${bindir}/
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN} = "grep egrep fgrep"
ALTERNATIVE_LINK_NAME[grep] = "${base_bindir}/grep"
ALTERNATIVE_LINK_NAME[egrep] = "${base_bindir}/egrep"
ALTERNATIVE_LINK_NAME[fgrep] = "${base_bindir}/fgrep"

