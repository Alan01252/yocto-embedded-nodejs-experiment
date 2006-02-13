SECTION = "libs"
DEPENDS = "gtk+ glib-2.0 libxml2"
DESCRIPTION = "A GTK+ HTML rendering library."
LICENSE = "GPL"
MAINTAINER = "Chris Lord <chris@openedhand.com>"
PV = "2.11.0+cvs${SRCDATE}"
PR = "r1"

SRC_URI = "cvs://anonymous@anoncvs.gnome.org/cvs/gnome;module=gtkhtml2 \
	file://at-import_box-pos.patch;patch=1;pnum=0 \
	file://css-stylesheet-user.patch;patch=1;pnum=0 \
	file://css-media.patch;patch=1;pnum=0 \
	file://fix-infinite-loop.patch;patch=1;pnum=0 \
	file://fix-margin-inherit.patch;patch=1;pnum=0 \
	file://fix-recreation2.patch;patch=1;pnum=0 \
	file://fix-style-change.patch;patch=1;pnum=0 \
	file://add-end-element-signal.patch;patch=1;pnum=0"

S = "${WORKDIR}/${PN}"

inherit pkgconfig autotools

EXTRA_OECONF = " --disable-accessibility"

do_stage() {
        oe_libinstall -so -C libgtkhtml libgtkhtml-2 ${STAGING_LIBDIR}
        install -d ${STAGING_INCDIR}/gtkhtml-2.0/libgtkhtml
	( for i in css document dom dom/core dom/events dom/html dom/traversal dom/views graphics layout layout/html util view; do install -d ${STAGING_INCDIR}/gtkhtml-2.0/libgtkhtml/$i; install -m 0644 ${S}/libgtkhtml/$i/*.h ${STAGING_INCDIR}/gtkhtml-2.0/libgtkhtml/$i; done )
	install -m 0644 ${S}/libgtkhtml/*.h ${STAGING_INCDIR}/gtkhtml-2.0/libgtkhtml
}

