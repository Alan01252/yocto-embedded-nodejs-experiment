DESCRIPTION  = "Sat Solver"
HOMEPAGE = "http://http://en.opensuse.org/openSUSE:Libzypp_satsolver"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=62272bd11c97396d4aaf1c41bc11f7d8"

DEPENDS = "libcheck rpm zlib expat db"

SRCREV = "0a7378d5f482f477a01cf1690d76871ab8bdcc32"
PV = "0.0-git${SRCPV}"
PR = "r12"

PARALLEL_MAKE=""

SRC_URI = "git://github.com/openSUSE/sat-solver.git;protocol=git \
           file://sat-solver_rpm5.patch \
           file://sat-solver_obsolete.patch \
           file://cmake.patch \
           file://db5.patch \
           file://sat-solver_core.patch \
           file://fix_gcc-4.6.0_compile_issue.patch \
          "

S = "${WORKDIR}/git"

EXTRA_OECMAKE += "-DRPM5=RPM5 -DOE_CORE=OE_CORE"

EXTRA_OECMAKE += " -DLIB=${@os.path.basename('${libdir}')}"

inherit cmake pkgconfig

RDEPENDS_${PN} = "rpm-libs"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_archgen () {
	# We need to dynamically generate our arch file based on the machine
	# configuration

	INSTALL_PLATFORM_ARCHS=""
	for each_arch in ${PACKAGE_ARCHS} ; do
		case "$each_arch" in
			all | any | noarch)
				continue;;
		esac
		INSTALL_PLATFORM_ARCHS="$each_arch $INSTALL_PLATFORM_ARCHS"
	done

	echo "/* Automatically generated by the sat-solver recipe */" > src/core-arch.h
	echo "const char *archpolicies[] = {" >> src/core-arch.h

        set -- $INSTALL_PLATFORM_ARCHS

        save_IFS=$IFS
        IFS=:
        while [ $# -gt 0 ]; do echo "  \"$1\",	"\""$*"\", >> src/core-arch.h ; shift; done
        IFS=$save_IFS

	echo "  0" >> src/core-arch.h
	echo "};" >> src/core-arch.h
}

addtask archgen before do_configure after do_patch
