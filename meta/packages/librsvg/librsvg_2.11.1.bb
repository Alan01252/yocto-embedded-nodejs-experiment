DESCRIPTION = "Library for rendering SVG files"
SECTION = "x11/utils"
DEPENDS = "gtk+ libcroco libart-lgpl libxml2 popt"
LICENSE = "LGPL"
PR = "r3"

inherit autotools pkgconfig gnome

PACKAGES =+ "librsvg-gtk librsvg-gtk-dbg librsvg-gtk-dev rsvg"
FILES_${PN} = "${libdir}/*.so.*"
FILES_rsvg = "${bindir}/rsvg ${bindir}/rsvg-view ${datadir}/pixmaps/svg-viewer.svg"
FILES_librsvg-gtk = "${libdir}/gtk-2.0/*/*/*.so"
FILES_librsvg-gtk-dev += "${libdir}/gtk-2.0/*.*a \
			  ${libdir}/gtk-2.0/*/loaders/*.*a \
			  ${libdir}/gtk-2.0/*/engines/*.*a"
FILES_librsvg-gtk-dbg += "${libdir}/gtk-2.0/.debug \
                          ${libdir}/gtk-2.0/*/*/.debug"

EXTRA_OECONF = "--disable-mozilla-plugin --disable-gnome-vfs"

do_stage() {
       autotools_stage_all
}
