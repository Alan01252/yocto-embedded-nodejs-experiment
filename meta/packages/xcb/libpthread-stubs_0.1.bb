DESCRIPTION = "This library provides weak aliases for pthread functions \
not provided in libc or otherwise available by default."
SECTION = "x11/libs"
LICENSE = "MIT-X"
HOMEPAGE = "http://xcb.freedesktop.org"
PR = "r1"

PARALLEL_MAKE = ""
#DEPENDS = "xcb-proto xproto libxau libxslt-native"
# DEPENDS += "xsltproc-native gperf-native"

SRC_URI = "http://xcb.freedesktop.org/dist/libpthread-stubs-${PV}.tar.bz2"

inherit autotools_stage pkgconfig

RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${DEBPV})"
