SUMMARY = "OpenGL extension loading library"
DESCRIPTION = "The OpenGL Extension Wrangler Library (GLEW) is a cross-platform open-source C/C++ extension loading library."
HOMEPAGE = "http://glew.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=67586"
SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ac251558de685c6b9478d89be3149c2"

DEPENDS = "virtual/libx11 virtual/libgl libglu libxext libxi libxmu"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/glew/glew/${PV}/glew-${PV}.tgz \
           file://no-strip.patch"

SRC_URI[md5sum] = "01246c7ecd135d99be031aa63f86dca1"
SRC_URI[sha256sum] = "af58103f4824b443e7fa4ed3af593b8edac6f3a7be3b30911edbc7344f48e4bf"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/glew/files/glew"
UPSTREAM_CHECK_REGEX = "/glew/(?P<pver>(\d+[\.\-_]*)+)/"

inherit lib_package pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

# Override SYSTEM to avoid calling config.guess, we're cross-compiling.  Pass
# our CFLAGS via POPT as that's the optimisation variable and safely
# overwritten.
EXTRA_OEMAKE = "SYSTEM='linux' \
                CC='${CC}' LD='${CC}' STRIP='' \
                LDFLAGS.EXTRA='${LDFLAGS}' \
                POPT='${CFLAGS}' \
                GLEW_PREFIX='${prefix}' BINDIR='${bindir}' \
                LIBDIR='${libdir}' INCDIR='${includedir}/GL'"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install.all
}
