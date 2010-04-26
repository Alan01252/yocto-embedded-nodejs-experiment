
SRC_URI = "git://git.moblin.org/${PN}.git;protocol=git"
PV = "0.6.1+git${SRCPV}"
PR = "r0"

DEPENDS = "libsoup-2.4"

S = "${WORKDIR}/git"

inherit autotools

do_configure_prepend () {
	echo "EXTRA_DIST=" > ${S}/gtk-doc.make
}
