SUMMARY = "Ultra-simple screen capture utility, aimed at handheld devices"
HOMEPAGE = "http://www.o-hand.com"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://main.c;endline=9;md5=023e14d6404d0a961eb97cbd011fc141 \
                    file://screenshot-ui.h;endline=9;md5=638d9ffa83e9325a36df224166ed6ad0"

DEPENDS = "matchbox-panel-2"
SRCREV = "3a9688e8a01b63a78f402b4e7c0b8b005fcdfa29"
PV = "0.1+git${SRCPV}"
PR = "r2"

SRC_URI = "git://git.yoctoproject.org/screenshot"

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check

FILES_${PN} += "${libdir}/matchbox-panel/*.so"

do_install_append () {
	rm ${D}${libdir}/matchbox-panel/*.la
}

# The matchbox-panel-2 requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"
