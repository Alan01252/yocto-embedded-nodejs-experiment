DESCRIPTION = "Simple DirectMedia Layer - native Edition"
HOMEPAGE = "http://www.libsdl.org"
SECTION = "libs"
LICENSE = "LGPL"
DEPENDS = "libx11-sdk libxext-sdk libxrandr-sdk libxrender-sdk"
RDEPENDS = "libx11-sdk libxrandr-sdk libxrender-sdk libxext-sdk"
PR = "r3"

SRC_URI = "http://www.libsdl.org/release/SDL-${PV}.tar.gz \
	   file://acinclude.m4 \
	   file://configure_tweak.patch;patch=1 \
	   file://kernel-asm-page.patch;patch=1 "
S = "${WORKDIR}/SDL-${PV}"

inherit autotools binconfig pkgconfig sdk

EXTRA_OECONF = "--disable-static --disable-debug --disable-cdrom --enable-threads --enable-timers --enable-endian \
                --enable-file --disable-oss --disable-alsa --disable-esd --disable-arts \
                --disable-diskaudio --disable-nas --disable-esd-shared --disable-esdtest \
                --disable-mintaudio --disable-nasm --enable-video-x11 --disable-video-dga \
                --disable-video-fbcon --disable-video-directfb --disable-video-ps2gs \
                --disable-video-xbios --disable-video-gem --disable-video-dummy \
                --disable-video-opengl --enable-input-events --enable-pthreads \
                --disable-video-picogui --disable-video-qtopia --enable-dlopen"

PARALLEL_MAKE = ""

do_stage() {
	autotools_stage_all
	install -m 0644 ${S}/build/libSDLmain.a ${STAGING_LIBDIR}
}
