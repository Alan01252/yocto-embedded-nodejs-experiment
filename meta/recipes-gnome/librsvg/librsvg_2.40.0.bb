DESCRIPTION = "Library for rendering SVG files"
HOMEPAGE = "http://ftp.gnome.org/pub/GNOME/sources/librsvg/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://rsvg.h;beginline=3;endline=24;md5=20b4113c4909bbf0d67e006778302bc6"

SECTION = "x11/utils"
DEPENDS = "cairo gdk-pixbuf glib-2.0 libcroco libxml2 pango"
BBCLASSEXTEND = "native"

inherit autotools pkgconfig gnomebase gtk-doc pixbufcache

GNOME_COMPRESS_TYPE = "xz"

SRC_URI += "file://gtk-option.patch \
            file://local-m4.patch \
            file://vapigen.m4"

SRC_URI[archive.md5sum] = "e16a84e9a86a18e5ca6ba95c512db6c6"
SRC_URI[archive.sha256sum] = "8f7db31df235813dbd035888035cf862d682e7cc5706c4e7ec05750d3f64a2f9"

EXTRA_OECONF = "--disable-introspection --disable-vala"

PACKAGECONFIG ??= "gdkpixbuf"
# The gdk-pixbuf loader
PACKAGECONFIG[gdkpixbuf] = "--enable-pixbuf-loader,--disable-pixbuf-loader,gdk-pixbuf-native"
# GTK+ test application (rsvg-view)
PACKAGECONFIG[gtk] = "--with-gtk3,--without-gtk3,gtk+3"

# 2.40.1 should ship the tarball with local m4 macros, but until then drop a
# vapigen in there so we don't need to build vala to configure.
do_configure_prepend() {
	if test ! -e ${S}/m4/vapigen.m4; then
		mkdir --parents ${S}/m4
		mv ${WORKDIR}/vapigen.m4 ${S}/m4/
	fi
}

do_install_append() {
	# Loadable modules don't need .a or .la on Linux
	rm -f ${D}${libdir}/gdk-pixbuf-2.0/*/loaders/*.a ${D}${libdir}/gdk-pixbuf-2.0/*/loaders/*.la
}

PACKAGES =+ "librsvg-gtk rsvg"
FILES_${PN} = "${libdir}/*.so.*"
FILES_${PN}-dbg += "${libdir}/gdk-pixbuf-2.0/*/loaders/.debug"
FILES_rsvg = "${bindir}/rsvg* \
	      ${datadir}/pixmaps/svg-viewer.svg \
	      ${datadir}/themes"
FILES_librsvg-gtk = "${libdir}/gdk-pixbuf-2.0/*/*/*.so"

PIXBUF_PACKAGES = "librsvg-gtk"

PIXBUFCACHE_SYSROOT_DEPS_append_class-native = " harfbuzz-native:do_populate_sysroot_setscene pango-native:do_populate_sysroot_setscene icu-native:do_populate_sysroot_setscene"
