include mesa-common.inc

LIC_FILES_CHKSUM = "file://docs/license.html;md5=7a3373c039b6b925c427755a4f779c1d"

PROTO_DEPS = "xf86driproto glproto dri2proto"
LIB_DEPS = "libdrm virtual/libx11 libxext libxxf86vm libxdamage libxfixes expat"

DEPENDS = "${PROTO_DEPS}  ${LIB_DEPS} mesa-dri-glsl-native"

PR = "r2"

SRC_URI += "file://crossfix.patch"

# most of our targets do not have DRI so will use mesa-xlib
DEFAULT_PREFERENCE = "-1"

# Atom PCs have DRI support so use mesa-dri by default
DEFAULT_PREFERENCE_atom-pc = "1"

LEAD_SONAME = "libGL.so.1"

EXTRA_OECONF += "--with-driver=dri --disable-egl --disable-gallium"

# We need glsl-compile built for buildhost arch instead of target (is provided by mesa-dri-glsl-native)"
do_configure_prepend() {
	sed -i "s#^GLSL_CL = .*\$#GLSL_CL = ${STAGING_BINDIR_NATIVE}/glsl-compile#g" ${S}/src/mesa/shader/slang/library/Makefile
}

do_install_append () {
	install -d ${D}/usr/bin
	install -m 0755 ${S}/progs/xdemos/{glxdemo,glxgears,glxheads,glxinfo} ${D}/usr/bin/
}

python populate_packages_prepend() {
	import os.path

	dri_drivers_root = os.path.join(bb.data.getVar('libdir', d, 1), "dri")

	do_split_packages(d, dri_drivers_root, '^(.*)_dri\.so$', 'mesa-dri-driver-%s', 'Mesa %s DRI driver', extra_depends='')
}

COMPATIBLE_HOST = '(i.86.*-linux|x86_64.*-linux)'

PACKAGES =+ "${PN}-xprogs"
PACKAGES_DYNAMIC = "mesa-dri-driver-*"

FILES_${PN}-dbg += "${libdir}/dri/.debug/*"
FILES_${PN}-xprogs = "${bindir}/glxdemo ${bindir}/glxgears ${bindir}/glxheads ${bindir}/glxinfo"

#
# Header generated by i586-poky-linux-gcc gen_matypes.c -o gen_matypes -I ../../../include/GL -I ../../../include -I .. -I ../main/ -I ../math -I ../glapi/ -I ../tnl
# then run gen_matypes > matypes.h on device
#
