HOMEPAGE = "http://live.gnome.org/JsonGlib"
SRC_URI = "git://github.com/ebassi/${PN}.git;protocol=git"
PV = "0.6.2+${SRCREV}"
PR = "r0"

S = "${WORKDIR}/git"

DEPENDS = "glib-2.0"

inherit autotools_stage

do_configure_prepend () {
	touch ${S}/gtk-doc.make
}