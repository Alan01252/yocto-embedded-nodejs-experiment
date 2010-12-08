DESCRIPTION = "X11 miscellaneous utility library"

require xorg-lib-common.inc

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=bb8d1df693806ad17c7268086f1d5394"

DEPENDS += "libxt libxext"
PROVIDES = "xmu"

PR = "r0"
PE = "1"

XORG_PN = "libXmu"

LEAD_SONAME = "libXmu"

PACKAGES =+ "libxmuu libxmuu-dev"

FILES_libxmuu = "${libdir}/libXmuu.so.*"
FILES_libxmuu-dev = "${libdir}/libXmuu.so"

SRC_URI[md5sum] = "6836883a0120e8346cf7f58dc42e465a"
SRC_URI[sha256sum] = "0fa91f303b70decc1ef6201c88c8a5f0b4ecd68c6c88bdcc891ecd1a689d36ad"
