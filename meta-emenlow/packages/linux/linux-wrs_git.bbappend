FILESEXTRAPATHS := "${THISDIR}/${PN}"
SRC_URI_append_emenlow = " file://defconfig"
SRC_URI_append_emenlow = " file://tools-profile.cfg"
COMPATIBLE_MACHINE_emenlow = "emenlow"
WRMACHINE_emenlow  = "emenlow"
