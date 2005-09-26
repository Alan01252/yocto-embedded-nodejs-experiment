SECTION = "x11/wm"
DESCRIPTION = "Matchbox window manager"
LICENSE = "GPL"
DEPENDS = "libmatchbox x11 xext libxrender startup-notification expat matchbox-common gconf"
RDEPENDS = "matchbox-common"
PV = "0.9.5cvs${CVSDATE}"
PR = "r3"
DEFAULT_PREFERENCE = "-1"

SRC_URI = "svn://svn.o-hand.com/repos/matchbox/trunk;module=matchbox-window-manager;proto=http \
	   file://kbdconfig"

S = "${WORKDIR}/matchbox-window-manager"

inherit autotools pkgconfig update-alternatives

ALTERNATIVE_NAME = "x-window-manager"
ALTERNATIVE_LINK = "${bindir}/x-window-manager"
ALTERNATIVE_PATH = "${bindir}/matchbox-session"
ALTERNATIVE_PRIORITY = "10"

FILES_${PN} = "${bindir} \
               ${datadir}/matchbox \
               ${sysconfdir}/matchbox \
               ${datadir}/themes/blondie/matchbox \
               ${datadir}/themes/Default/matchbox \
               ${datadir}/themes/MBOpus/matchbox"

EXTRA_OECONF = "--enable-startup-notification --disable-xrm --enable-expat"

do_install_prepend() {
	install ${WORKDIR}/kbdconfig ${S}/data/kbdconfig
}

