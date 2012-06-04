HOMEPAGE = "http://en.opensuse.org/Portal:Libzypp"
DESCRIPTION  = "The ZYpp Linux Software management framework"

LICENSE  = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=11fccc94d26293d78cb4996cb17e5fa7"

inherit cmake gettext

DEPENDS  = "rpm boost curl libxml2 zlib sat-solver expat openssl udev libproxy"

S = "${WORKDIR}/git"
SRCREV = "15b6c52260bbc52b3d8e585e271b67e10cc7c433"
PV = "0.0-git${SRCPV}"
PR = "r23"

SRC_URI = "git://github.com/openSUSE/libzypp.git;protocol=git \
           file://no-doc.patch \
           file://rpm5.patch \
           file://rpm5-no-rpmdbinit.patch \
	   file://config-release.patch \
	   file://libzypp-oearch.patch \
	   file://libzypp-compatargs.patch \
	   file://fix_for_compile_wth_gcc-4.6.0.patch \
	   file://hardcode-lib-fix.patch \
	   file://close.patch \
	   file://libzypp-rpm549.patch \
	   file://cstdio.patch \
          "

SRC_URI_append_mips = " file://mips-workaround-gcc-tribool-error.patch"

# ARM specific global constructor workaround
SRC_URI_append_arm  = " file://arm-workaround-global-constructor.patch"

# rpmdb2solv from sat-solver is run from libzypp
RDEPENDS_${PN} = "sat-solver rpm-libs gzip ${RDEPGNUPG}"

RDEPGNUPG = "gnupg"
RDEPGNUPG_libc-uclibc = ""

PACKAGES =+ "${PN}-pkgmgt"

FILES_${PN} += "${libdir}/zypp ${datadir}/zypp ${datadir}/icons"
FILES_${PN}-dev += "${datadir}/cmake"

FILES_${PN}-pkgmgt = "${bindir}/package-manager \
                      ${bindir}/package-manager-su \
                      ${datadir}/applications/package-manager.desktop \
                     "

EXTRA_OECMAKE += " -DLIB=${@os.path.basename('${libdir}')}"
OECMAKE_CXX_LINK_FLAGS_libc-uclibc += "-pthread"

LDFLAGS += "-lpthread"

PACKAGE_ARCH = "${MACHINE_ARCH}"

AVOID_CONSTRUCTOR = ""

# Due to an ARM specific compiler issue
AVOID_CONSTRUCTOR_arm = "true"

# Due to a potential conflict with '_mips' being a define
AVOID_CONSTRUCTOR_mips = "true"

do_archgen () {
	# We need to dynamically generate our arch file based on the machine
	# configuration
	echo "/* Automatically generated by the libzypp recipes */" 		 > zypp/oe-arch.h
	echo "/* Avoid Constructor: ${AVOID_CONSTRUCTOR} */" 			 >> zypp/oe-arch.h
	echo ""									>> zypp/oe-arch.h
	echo "#ifndef OE_ARCH_H"						>> zypp/oe-arch.h
	echo "#define OE_ARCH_H 1"						>> zypp/oe-arch.h
	echo "#define Arch_machine Arch_${MACHINE_ARCH}" | tr - _		>> zypp/oe-arch.h
	echo "#endif /* OE_ARCH_H */"						>> zypp/oe-arch.h
	echo ""									>> zypp/oe-arch.h
	if [ "${AVOID_CONSTRUCTOR}" != "true" ]; then
	  echo "#ifdef DEF_BUILTIN"						>> zypp/oe-arch.h
	  echo "/* Specify builtin types */"					>> zypp/oe-arch.h
	  for each_arch in ${PACKAGE_ARCHS} ; do
		case "$each_arch" in
			all | any | noarch)
				continue;;
		esac
		echo "    DEF_BUILTIN( ${each_arch} );"	 | tr - _		>> zypp/oe-arch.h
	  done
	  echo "#endif /* DEF_BUILTIN */"						>> zypp/oe-arch.h
	  echo ""									>> zypp/oe-arch.h
	fi
	echo "#ifdef OE_EXTERN_PROTO"						>> zypp/oe-arch.h
	echo "/* Specify extern prototypes */"					>> zypp/oe-arch.h
	for each_arch in ${PACKAGE_ARCHS} ; do
		case "$each_arch" in
			all | any | noarch)
				continue;;
		esac
		echo "  extern const Arch Arch_${each_arch};" | tr - _		>> zypp/oe-arch.h
	done
	echo "#endif /* OE_EXTERN_PROTO */"					>> zypp/oe-arch.h
	echo ""									>> zypp/oe-arch.h
	echo "#ifdef OE_PROTO"						>> zypp/oe-arch.h
	echo "/* Specify prototypes */"						>> zypp/oe-arch.h
	for each_arch in ${PACKAGE_ARCHS} ; do
		case "$each_arch" in
			all | any | noarch)
				continue;;
		esac
		if [ "${AVOID_CONSTRUCTOR}" != "true" ]; then
		  echo -n "  const Arch Arch_${each_arch} " | tr - _		>> zypp/oe-arch.h
		  echo "(_${each_arch});" | tr - _				>> zypp/oe-arch.h
		else
		  echo -n "  const Arch Arch_${each_arch} " | tr - _		>> zypp/oe-arch.h
		  echo "( IdString ( \"${each_arch}\" ) );" | tr - _		>> zypp/oe-arch.h
		fi
	done
	echo "#endif /* OE_PROTO */"						>> zypp/oe-arch.h
	echo ""									>> zypp/oe-arch.h
	echo "#ifdef OE_DEF_COMPAT"						>> zypp/oe-arch.h
	echo "/* Specify compatibility information */"				>> zypp/oe-arch.h
	INSTALL_PLATFORM_ARCHS=""
	for each_arch in ${PACKAGE_ARCHS} ; do
		INSTALL_PLATFORM_ARCHS="$each_arch $INSTALL_PLATFORM_ARCHS"
	done

	COMPAT_WITH=""
	set -- ${INSTALL_PLATFORM_ARCHS}
	while [ $# -gt 0 ]; do
		case "$1" in
			all | any | noarch)
				shift ; continue;;
		esac
		if [ "${AVOID_CONSTRUCTOR}" != "true" ]; then
		  CARCH="_$1"
		else
		  CARCH="IdString(\"$1\")"
		fi
		shift
		COMPAT=""
		for each_arch in "$@"; do
			if [ -z "${AVOID_CONSTRUCTOR}" ]; then
			  arch_val="_${each_arch}"
			else
			  arch_val="IdString(\"${each_arch}\")"
			fi
			if [ -z "$COMPAT" ]; then
				COMPAT=${arch_val}
			else
				COMPAT="${arch_val},$COMPAT"
			fi
		done
		COMPAT_WITH="${CARCH},${COMPAT} $COMPAT_WITH"
	done
	for each_compat in ${COMPAT_WITH} ; do
		echo "        defCompatibleWith( ${each_compat} );" | tr - _	>> zypp/oe-arch.h
	done
	echo "#endif /* DEF_COMPAT */"						>> zypp/oe-arch.h
	echo ""									>> zypp/oe-arch.h
}

addtask archgen before do_configure after do_patch
