DESCRIPTION = "People widgets for Moblin User Experience"
SRC_URI = "git://git.moblin.org/${PN}.git;protocol=git"
LICENSE = "LGPLv2.1"
PV = "0.0+git${SRCPV}"
PR = "r2"

S = "${WORKDIR}/git"

DEPENDS = "telepathy-glib glib-2.0 telepathy-mission-control nbtk eds-dbus"

inherit autotools_stage
