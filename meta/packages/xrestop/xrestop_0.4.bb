DESCRIPTION = "top-like statistics of X11 server resource usage by clients"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/xrestop"
BUGTRACKER = "https://bugs.freedesktop.org/"

LICENSE = "GPLv2 + GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://xrestop.c;endline=18;md5=730876c30f0d8a928676bcd1242a3b35"

SECTION = "x11/utils"
PR = "r2"

DEPENDS = "libxres libxext virtual/libx11"

SRC_URI = "http://projects.o-hand.com/sources/xrestop/xrestop-${PV}.tar.gz"

inherit autotools
