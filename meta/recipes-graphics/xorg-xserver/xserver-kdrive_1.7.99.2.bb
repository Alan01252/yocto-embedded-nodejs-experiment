require xserver-kdrive.inc

DEPENDS += "libxkbfile libxcalibrate font-util pixman"

RDEPENDS_${PN} += "xkeyboard-config"

EXTRA_OECONF += "--disable-glx"

PE = "1"
PR = "r30"

SRC_URI = "${XORG_MIRROR}/individual/xserver/xorg-server-${PV}.tar.bz2 \
	file://extra-kmodes.patch \
	file://disable-apm.patch \
	file://no-serial-probing.patch \
	file://keyboard-resume-workaround.patch \
	file://enable-xcalibrate.patch \
	file://hide-cursor-and-ppm-root.patch \
	file://fbdev_xrandr_ioctl.patch \
	file://fix-newer-xorg-headers.patch \
	file://crosscompile.patch \
	file://error-address-work-around.patch \
        file://fix-bogus-stack-variables.patch \
	file://nodolt.patch"
#	file://kdrive-evdev.patch
#	file://kdrive-use-evdev.patch
#	file://enable-builtin-fonts.patch
#	file://optional-xkb.patch

SRC_URI[md5sum] = "cafc4e2d4ef6cf6e47f3e7dffeb3346a"
SRC_URI[sha256sum] = "a89f13b166b412930fe418ff50032dd2cde8bb181d8b47b5ca6f848d218fdcf2"


S = "${WORKDIR}/xorg-server-${PV}"
