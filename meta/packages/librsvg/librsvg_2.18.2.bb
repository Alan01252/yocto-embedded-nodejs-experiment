DESCRIPTION = "Library for rendering SVG files"
SECTION = "x11/utils"
DEPENDS = "gtk+ libcroco cairo libxml2 popt"
LICENSE = "LGPL"
PR = "r3"

EXTRA_OECONF = "--disable-mozilla-plugin --without-svgz --without-croco --disable-gnome-vfs"

inherit autotools pkgconfig gnome

PACKAGES =+ "librsvg-gtk librsvg-gtk-dbg librsvg-gtk-dev rsvg"
FILES_${PN} = "${libdir}/*.so.*"
FILES_rsvg = "${bindir}/rsvg \
	      ${bindir}/rsvg-view \
	      ${bindir}/rsvg-convert \
	      ${datadir}/pixmaps/svg-viewer.svg"
FILES_librsvg-gtk = "${libdir}/gtk-2.0/*/*/*.so"
FILES_librsvg-gtk-dev += "${libdir}/gtk-2.0/*.*a \
			  ${libdir}/gtk-2.0/*/loaders/*.*a \
			  ${libdir}/gtk-2.0/*/engines/*.*a"
FILES_librsvg-gtk-dbg += "${libdir}/gtk-2.0/.debug \
                          ${libdir}/gtk-2.0/*/*/.debug"

do_stage() {
	autotools_stage_all
}

pkg_postinst_librsvg-gtk() {
if [ "x$D" != "x" ]; then
  exit 1
fi

test -x ${bindir}/gdk-pixbuf-query-loaders && { gdk-pixbuf-query-loaders > ${sysconfdir}/gtk-2.0/gdk-pixbuf.loaders ; }
test -x ${bindir}/gtk-update-icon-cache && gtk-update-icon-cache  -q ${datadir}/icons/hicolor
}
