require automake.inc

DEPENDS_virtclass-native = "autoconf-native"

RDEPENDS_automake += "\
    autoconf \
    perl \
    perl-module-bytes \
    perl-module-constant \
    perl-module-cwd \
    perl-module-data-dumper \
    perl-module-dynaloader \
    perl-module-errno \
    perl-module-exporter-heavy \
    perl-module-file-basename \
    perl-module-file-compare \
    perl-module-file-copy \
    perl-module-file-glob \
    perl-module-file-spec-unix \
    perl-module-file-stat \
    perl-module-getopt-long \
    perl-module-io \
    perl-module-io-file \
    perl-module-posix \
    perl-module-strict \
    perl-module-text-parsewords \
    perl-module-vars "

RDEPENDS_automake-native = "autoconf-native perl-native-runtime"

PATHFIXPATCH = "file://path_prog_fixes.patch;patch=1"
PATHFIXPATCH_virtclass-native = ""

SRC_URI += "${PATHFIXPATCH}"

NATIVE_INSTALL_WORKS = "1"
do_install () {
	oe_runmake 'DESTDIR=${D}' install
	install -d ${D}${datadir}
	if [ ! -e ${D}${datadir}/aclocal ]; then
		ln -sf aclocal-1.10 ${D}${datadir}/aclocal
	fi
	if [ ! -e ${D}${datadir}/automake ]; then
		ln -sf automake-1.10 ${D}${datadir}/automake
	fi
}

BBCLASSEXTEND = "native"

DEFAULT_PREFERENCE = "-1"
