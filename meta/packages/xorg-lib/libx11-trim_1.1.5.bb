require xorg-lib-common.inc

DESCRIPTION = "X11 protocol and utility library"
DEPENDS += "libxcb xproto xextproto xtrans libxau"

#FILESDIR = "${@os.path.dirname(bb.data.getVar('FILE',d,1))}/libx11"
#SRC_URI += "file://X18NCMSstubs.diff;patch=1 "

PROVIDES = "virtual/libx11"

PE = "1"
PR = "r0"

XORG_PN = "libX11"
LEAD_SONAME = "libX11.so"

#EXTRA_OECONF += "--disable-xlocale --disable-xcms --with-xcb"
EXTRA_OECONF += "--disable-xcms --with-xcb"

do_compile() {
        (
         unset CC LD CXX CCLD CFLAGS CPPFLAGS LDFLAGS CXXFLAGS
         cd src/util; touch makekeys-makekeys.o ; ${BUILD_CC} ${BUILD_CFLAGS} makekeys.c -o makekeys
         cd ../../
        ) || exit 1
        oe_runmake
}

FILES_${PN} += "${datadir}/X11/XKeysymDB ${datadir}/X11/XErrorDB "

