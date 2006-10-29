LICENSE = "LGPL"
SECTION = "x11/libs"
# Xt needed to keep autoconf's check for X11 happy
DEPENDS = "glib-2.0 fontconfig freetype zlib libx11 libxft libxt gtk-doc cairo"
DESCRIPTION = "The goal of the Pango project is to provide an \
Open Source framework for the layout and rendering of \
internationalized text."
PR = "r0"

# seems to go wrong with default cflags
FULL_OPTIMIZATION_arm = "-O2"

SRC_URI = "http://download.gnome.org/sources/pango/1.14/pango-${PV}.tar.bz2 \
	   file://no-tests.patch;patch=1"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-glibtest \
		--enable-explicit-deps=no \
	        --disable-debug"

LEAD_SONAME = "libpango-1.0*"
LIBV = "1.5.0"

FILES_${PN} = "/etc ${bindir}/* ${libdir}/libpango*.so.*"
FILES_${PN}-dbg += "${libdir}/pango/${LIBV}/modules/.debug"
FILES_${PN}-dev += "${libdir}/pango/${LIBV}/modules/*.la"

do_stage () {
	for lib in pango pangox pangoft2 pangoxft pangocairo; do
		oe_libinstall -so -C pango lib$lib-1.0 ${STAGING_LIBDIR}/
	done
	install -d ${STAGING_INCDIR}/pango
	install -m 0644 ${S}/pango/pango*.h ${STAGING_INCDIR}/pango/
}

postinst_prologue() {
if [ "x$D" != "x" ]; then
  exit 1
fi

}

PACKAGES_DYNAMIC = "pango-module-*"

python populate_packages_prepend () {
	prologue = bb.data.getVar("postinst_prologue", d, 1)

	modules_root = bb.data.expand('${libdir}/pango/${LIBV}/modules', d)

	do_split_packages(d, modules_root, '^pango-(.*)\.so$', 'pango-module-%s', 'Pango module %s', prologue + 'pango-querymodules > /etc/pango/pango.modules')
}
