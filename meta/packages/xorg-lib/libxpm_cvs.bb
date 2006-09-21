PV = "0.0+cvs${SRCDATE}"
LICENSE = "X-BSD"
SECTION = "x11/libs"
PRIORITY = "optional"
DEPENDS = "xproto virtual/libx11"
PROVIDES = "xpm"
DESCRIPTION = "X Pixmap library."
PR = "r1"

SRC_URI = "${FREEDESKTOP_CVS}/xlibs;module=Xpm"
S = "${WORKDIR}/Xpm"

inherit autotools pkgconfig 

do_stage () {
	install -m 0644 ${S}/lib/xpm.h ${STAGING_INCDIR}/X11/xpm.h
	oe_libinstall -a -so -C lib libXpm ${STAGING_LIBDIR}
}
