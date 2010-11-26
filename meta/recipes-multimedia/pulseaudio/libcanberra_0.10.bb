DESCRIPTION = "Libcanberra is an implementation of the XDG Sound Theme and Name Specifications, for generating event sounds on free desktops."
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/canberra.h;endline=24;md5=8dd99ba690687f5816f711d9313c8967"

DEPENDS = "gtk+ pulseaudio alsa-lib libtool"
PR = "r3"

inherit gconf autotools

SRC_URI = "http://0pointer.de/lennart/projects/libcanberra/libcanberra-${PV}.tar.gz \
           file://nofallbackfix.patch;patch=1 \
           file://autoconf_version.patch;patch=1"

EXTRA_OECONF = " --disable-oss --disable-ltdl-install" 

do_configure_prepend () {
	rm -f ${S}/libltdl/configure*
}

FILES_${PN} += "${libdir}/gtk-2.0/modules/ ${datadir}/gnome"
FILES_${PN}-dbg += "${libdir}/gtk-2.0/modules/.debug"

AUTOTOOLS_STAGE_PKGCONFIG = "1"

