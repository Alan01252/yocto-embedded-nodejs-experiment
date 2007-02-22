LICENSE = "GPL"
SECTION = "x11"
DEPENDS = "glib-2.0 gtk+ eds-dbus"
DESCRIPTION = "Tasks is a task list application."

PV = "0.0+svn${SRCDATE}"
PR = "r1"

SRC_URI = "svn://svn.o-hand.com/repos/${PN};module=trunk;proto=http"

inherit autotools pkgconfig

S = "${WORKDIR}/trunk"

FILES_${PN} += "${datadir}/icons"
