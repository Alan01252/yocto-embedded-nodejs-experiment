require dates.inc

DEFAULT_PREFERENCE = "-1"

PV = "0.4.4+svn${SRCDATE}"
S = "${WORKDIR}/trunk"
PR = "r1"

SRC_URI = "svn://svn.o-hand.com/repos/${PN};module=trunk;proto=http \
	   file://dates-owl-window-menu.patch;patch=1 \
	  "
