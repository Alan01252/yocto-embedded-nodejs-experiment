require gst-plugins.inc

LICENSE = "GPLv2+ & LGPLv2.1+ & LGPLv2+"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://gst/synaesthesia/synaescope.h;beginline=1;endline=20;md5=99f301df7b80490c6ff8305fcc712838 \
                    file://tests/check/elements/xingmux.c;beginline=1;endline=21;md5=4c771b8af188724855cb99cadd390068 \
                    file://gst/mpegstream/gstmpegparse.h;beginline=1;endline=18;md5=ff65467b0c53cdfa98d0684c1bc240a9"

DEPENDS += "gst-plugins-base libid3tag libmad mpeg2dec liba52 lame"
PR = "r0"

inherit gettext

EXTRA_OECONF += "--with-plugins=a52dec,lame,id3tag,mad,mpeg2dec,mpegstream,mpegaudioparse,asfdemux,realmedia"

do_configure_prepend() {
	# This m4 file contains nastiness which conflicts with libtool 2.2.2
	rm ${S}/m4/lib-link.m4 || true
}

SRC_URI[md5sum] = "04a7009a4efea2844075949c111f5e4d"
SRC_URI[sha256sum] = "f9c16748cd9269fae86422d8254a579fa6db073797a5a19a9dc5c72cd66c8e14"
