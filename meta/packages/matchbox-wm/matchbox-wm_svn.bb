SECTION = "x11/wm"
DESCRIPTION = "Matchbox window manager"
LICENSE = "GPL"
DEPENDS = "libmatchbox virtual/libx11 libxext libxrender startup-notification expat gconf"
PV = "1.2+svn${SRCDATE}"
PR = "r6"

SRC_URI = "svn://svn.o-hand.com/repos/matchbox/trunk;module=matchbox-window-manager;proto=http \
	   file://kbdconfig"

S = "${WORKDIR}/matchbox-window-manager"

inherit autotools pkgconfig update-alternatives

ALTERNATIVE_NAME = "x-window-manager"
ALTERNATIVE_LINK = "${bindir}/x-window-manager"
ALTERNATIVE_PATH = "${bindir}/matchbox-session"
ALTERNATIVE_PRIORITY = "10"

FILES_${PN} = "${bindir}/* \
               ${datadir}/matchbox \
               ${sysconfdir}/matchbox \
               ${datadir}/themes/blondie/matchbox \
               ${datadir}/themes/Default/matchbox \
               ${datadir}/themes/MBOpus/matchbox"

EXTRA_OECONF = "--enable-startup-notification --disable-xrm --enable-expat"

do_install_prepend() {
	install ${WORKDIR}/kbdconfig ${S}/data/kbdconfig
}
