DESCRIPTION = "Firmware for the Prism 2.x/3 cards"
SECTION = "base"
LICENSE = "closed"
RDEPENDS = "hostap-utils"
RREPLACES = "prism3-firmware prism3-support"
RCONFLICTS = "prism3-firmware prism3-support"
PACKAGE_ARCH = "all"
PR = "r1"

SRC_URI = "http://www.red-bean.com/~proski/firmware/Latest-prism.tar.bz2 \
           file://prism-fw.sh \
	   file://hostap.rules"

S = "${WORKDIR}/Latest-prism/"

do_install() {
	install -d ${D}${base_libdir}/firmware/
	install -d ${D}${base_libdir}/udev/
	install -d ${D}${sysconfdir}/pcmcia/
	install -d ${D}${sysconfdir}/udev/rules.d/

	install -m 0644 primary-RAM/*.hex ${D}${base_libdir}/firmware/
	install -m 0644 secondary-RAM/rf010804.hex ${D}${base_libdir}/firmware/

	install -m 0755 ${WORKDIR}/prism-fw.sh ${D}${base_libdir}/udev/
	install -m 0644 ${WORKDIR}/hostap.rules ${D}${sysconfdir}/udev/rules.d/
}

PACKAGES = "${PN}"
FILES_${PN} += "${base_libdir}"
