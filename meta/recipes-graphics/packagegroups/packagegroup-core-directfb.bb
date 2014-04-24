SUMMARY = "DirectFB without X11"
LICENSE = "MIT"

inherit packagegroup

TOUCH = ' ${@bb.utils.contains("MACHINE_FEATURES", "touchscreen", "tslib tslib-calibrate tslib-tests", "",d)}'

RDEPENDS_${PN} = " \
		directfb \
		directfb-examples \
		pango \
		pango-modules \
		fontconfig \
		${TOUCH} \
"
