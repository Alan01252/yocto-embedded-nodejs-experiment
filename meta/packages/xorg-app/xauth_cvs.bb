PV = "0.0+cvs${SRCDATE}"
LICENSE = "MIT"
DEPENDS = "virtual/libx11 libxau libxmu libxext"
DESCRIPTION = "X authority file utility"
SECTION = "x11/base"
PR = "r2"

SRC_URI = "${FREEDESKTOP_CVS}/xorg;module=xc/programs/xauth \
           file://autofoo.patch;patch=1"
S = "${WORKDIR}/xauth"

inherit autotools pkgconfig 
