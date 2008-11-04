SECTION = "x11/base"
LICENSE = "MIT"
SRC_URI = "http://dri.freedesktop.org/libdrm/libdrm-${PV}.tar.bz2"
PROVIDES = "drm"
DEPENDS = "pthread-stubs"

inherit autotools pkgconfig

do_stage() {
	autotools_stage_all
}
