DESCRIPTION = "Simple GTK+ Text Editor"
HOMEPAGE = "http://tarot.freeshell.org/leafpad/"
BUGTRACKER = ""

LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/leafpad.h;endline=20;md5=d3d6a89f5e61e8b13bdea537511ba1fa \
                    file://src/utils.c;endline=20;md5=0d2cc6584ba3202448bb274f62739571"

DEPENDS = "gtk+ intltool-native"
DEPENDS_append_poky = " libowl"
SRC_URI = "http://savannah.nongnu.org/download/${PN}/${PN}-${PV}.tar.gz \
	   file://leafpad.desktop"

SRC_URI[md5sum] = "d39acdf4d31de309d484511bdc9f5924"
SRC_URI[sha256sum] = "8df8de7aaea26148225b6120631b4fe6b89d36d2b52962e7c9cc0ce07bfdbd4c"
PR = "r0"

SRC_URI_append_poky += " file://owl-menu.patch;apply=yes "

inherit autotools pkgconfig

EXTRA_OECONF = " --enable-chooser --disable-gtktest --disable-print"

do_install_append () {
        install -d ${D}/${datadir}
        install -d ${D}/${datadir}/applications
        install -m 0644 ${WORKDIR}/leafpad.desktop ${D}/${datadir}/applications
}

FILES_${PN} += "${datadir}/applications/leafpad.desktop"
