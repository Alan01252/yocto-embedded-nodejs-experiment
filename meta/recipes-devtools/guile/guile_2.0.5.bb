SUMMARY = "Guile is the GNU Ubiquitous Intelligent Language for Extensions."
DESCRIPTION = "Guile is the GNU Ubiquitous Intelligent Language for Extensions,\
 the official extension language for the GNU operating system.\
 Guile is a library designed to help programmers create flexible applications.\
 Using Guile in an application allows the application's functionality to be\
 extended by users or other programmers with plug-ins, modules, or scripts.\
 Guile provides what might be described as 'practical software freedom,'\
 making it possible for users to customize an application to meet their\
 needs without digging into the application's internals."

HOMEPAGE = "http://www.gnu.org/software/guile/"
SECTION = "devel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504" 

SRC_URI = "${GNU_MIRROR}/guile/guile-${PV}.tar.gz \
           file://debian/0002-Mark-Unused-modules-are-removed-gc-test-as-unresolve.patch \
           file://debian/0003-Mark-mutex-with-owner-not-retained-threads-test-as-u.patch \
           file://opensuse/guile-64bit.patch \
           file://guile_2.0.5_fix_sed_error.patch \
           file://guile_2.0.5_disable_goops_optimizations.patch \
           file://guile_2.0.5_fix_cross_compilation.patch \
           file://remove-gets.patch \
           file://arm_endianness.patch \
           "

#           file://debian/0001-Change-guile-to-guile-X.Y-for-info-pages.patch
#           file://opensuse/guile-turn-off-gc-test.patch 

SRC_URI[md5sum] = "bcf70d54b44c99cb9acd3f63c5486b4b"
SRC_URI[sha256sum] = "2a026ea6cdbc51ca71bcd9787839debfa45ac5db1e26dc00b30ca9b128b10956"

PR = "r4"

inherit autotools gettext
BBCLASSEXTEND = "native"

DEPENDS = "libunistring bdwgc gmp libtool libffi"
# add guile-native only to the target recipe's DEPENDS
DEPENDS += "${@['guile-native libatomics-ops', ''][d.getVar('PN', True) != 'guile']}"

EXTRA_OECONF += "${@['--without-libltdl-prefix --without-libgmp-prefix', ''][bb.data.inherits_class('native',d)]}"

do_configure_prepend() {
	mkdir -p po
}

export GUILE_FOR_BUILD="${BUILD_SYS}-guile"

do_compile_append() {
	# just for target recipe
	if [ "${PN}" == "guile" ]
	then
		sed -i -e s:${STAGING_DIR_TARGET}::g \
	               -e s:/${TARGET_SYS}::g \
	               -e s:-L/usr/lib::g \
        	       -e s:-isystem/usr/include::g \
	               -e s:,/usr/lib:,\$\{libdir\}:g \
	                  meta/guile-2.0.pc
	fi
}

do_install_append_virtclass-native() {
	install -m 0755  ${D}${bindir}/guile ${D}${bindir}/${HOST_SYS}-guile

	create_wrapper ${D}/${bindir}/guile \
		GUILE_LOAD_PATH=${STAGING_DATADIR_NATIVE}/guile/2.0 \
		GUILE_LOAD_COMPILED_PATH=${STAGING_LIBDIR_NATIVE}/guile/2.0/ccache
	create_wrapper ${D}${bindir}/${HOST_SYS}-guile \
		GUILE_LOAD_PATH=${STAGING_DATADIR_NATIVE}/guile/2.0 \
		GUILE_LOAD_COMPILED_PATH=${STAGING_LIBDIR_NATIVE}/guile/2.0/ccache
}

SYSROOT_PREPROCESS_FUNCS = "guile_cross_config"

guile_cross_config() {
	# this is only for target recipe
	if [ "${PN}" == "guile" ]
	then
	        # Create guile-config returning target values instead of native values
	        install -d ${SYSROOT_DESTDIR}${STAGING_BINDIR_CROSS}
        	echo '#!'`which ${BUILD_SYS}-guile`$' \\\n--no-auto-compile -e main -s\n!#\n(define %guile-build-info '\'\( \
			> guile-config.cross
	        sed -n -e 's:^[ \t]*{[ \t]*":  (:' \
			-e 's:",[ \t]*": . ":' \
			-e 's:" *}, *\\:"):' \
			-e 's:^.*cachedir.*$::' \
			-e '/^  (/p' \
			< libguile/libpath.h >> guile-config.cross
	        echo '))' >> guile-config.cross
	        cat meta/guile-config >> guile-config.cross
	        install guile-config.cross ${STAGING_BINDIR_CROSS}/guile-config
	fi
}
