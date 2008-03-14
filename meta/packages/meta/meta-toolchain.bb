DESCRIPTION = "Meta package for building a installable toolchain"
LICENSE = "MIT"
DEPENDS = "ipkg-native ipkg-utils-native fakeroot-native sed-native"

inherit sdk meta

SDK_DIR = "${WORKDIR}/sdk"
SDK_OUTPUT = "${SDK_DIR}/image"
SDK_OUTPUT2 = "${SDK_DIR}/image-extras"
SDK_DEPLOY = "${TMPDIR}/deploy/sdk"

IPKG_HOST = "ipkg-cl -f ${IPKGCONF_SDK} -o ${SDK_OUTPUT}"
IPKG_TARGET = "ipkg-cl -f ${IPKGCONF_TARGET} -o ${SDK_OUTPUT}/${SDK_PREFIX}/${TARGET_SYS}"

TOOLCHAIN_HOST_TASK ?= "task-sdk-host"
TOOLCHAIN_TARGET_TASK ?= "task-poky-standalone-sdk-target task-poky-standalone-sdk-target-dbg"
TOOLCHAIN_OUTPUTNAME ?= "${SDK_NAME}-toolchain-${DISTRO_VERSION}"

RDEPENDS = "${TOOLCHAIN_TARGET_TASK} ${TOOLCHAIN_HOST_TASK}"

do_populate_sdk() {
	rm -rf ${SDK_OUTPUT}
	mkdir -p ${SDK_OUTPUT}

	package_update_index_ipk
	package_generate_ipkg_conf

	for arch in ${PACKAGE_ARCHS}; do
		revipkgarchs="$arch $revipkgarchs"
	done

	${IPKG_HOST} update
	${IPKG_HOST} install ${TOOLCHAIN_HOST_TASK} ipkg-sdk

	${IPKG_TARGET} update
	${IPKG_TARGET} install ${TOOLCHAIN_TARGET_TASK}

	install -d ${SDK_OUTPUT}/${prefix}/usr/lib/ipkg
	mv ${SDK_OUTPUT}/usr/lib/ipkg/* ${SDK_OUTPUT}/${prefix}/usr/lib/ipkg/
	#rm -Rf ${SDK_OUTPUT}/usr/lib

	install -d ${SDK_OUTPUT}/${sysconfdir}
	install -m 0644 ${IPKGCONF_TARGET} ${IPKGCONF_SDK} ${SDK_OUTPUT}/${sysconfdir}

	# extract and store ipks, pkgdata and shlibs data
	target_pkgs=`cat ${SDK_OUTPUT}/${prefix}/package-status | grep Package: | cut -f 2 -d ' '`
	mkdir -p ${SDK_OUTPUT2}/${prefix}/ipk/
	mkdir -p ${SDK_OUTPUT2}/${prefix}/pkgdata/runtime/
	mkdir -p ${SDK_OUTPUT2}/${prefix}/${TARGET_SYS}/shlibs/
	for pkg in $target_pkgs ; do
		for arch in $revipkgarchs; do
			pkgnames=${DEPLOY_DIR_IPK}/$arch/${pkg}_*_$arch.ipk
			if [ -e $pkgnames ]; then
				echo "Found $pkgnames"
				cp $pkgnames ${SDK_OUTPUT2}/${prefix}/ipk/
				orig_pkg=`ipkg-list-fields $pkgnames | grep OE: | cut -d ' ' -f2`
				pkg_subdir=$arch${TARGET_VENDOR}${@['-' + bb.data.getVar('TARGET_OS', d, 1), ''][bb.data.getVar('TARGET_OS', d, 1) == ('' or 'custom')]}
				mkdir -p ${SDK_OUTPUT2}/${prefix}/pkgdata/$pkg_subdir/runtime
				cp ${STAGING_DIR}/pkgdata/$pkg_subdir/$orig_pkg ${SDK_OUTPUT2}/${prefix}/pkgdata/$pkg_subdir/
				subpkgs=`cat ${STAGING_DIR}/pkgdata/$pkg_subdir/$orig_pkg | grep PACKAGES: | cut -b 10-`
				for subpkg in $subpkgs; do
					cp ${STAGING_DIR}/pkgdata/$pkg_subdir/runtime/$subpkg ${SDK_OUTPUT2}/${prefix}/pkgdata/$pkg_subdir/runtime/
					if [ -e ${STAGING_DIR}/pkgdata/$pkg_subdir/runtime/$subpkg.packaged ];then
						cp ${STAGING_DIR}/pkgdata/$pkg_subdir/runtime/$subpkg.packaged ${SDK_OUTPUT2}/${prefix}/pkgdata/$pkg_subdir/runtime/
					fi
					if [ -e ${STAGING_DIR_TARGET}/shlibs/$subpkg.list ]; then
						cp ${STAGING_DIR_TARGET}/shlibs/$subpkg.* ${SDK_OUTPUT2}/${prefix}/${TARGET_SYS}/shlibs/
					fi
				done
				break
			fi
		done
	done

	# Remove broken .la files
	rm -f ${SDK_OUTPUT}/${prefix}/${TARGET_SYS}/lib/*.la

	# Setup site file for external use
	siteconfig=${SDK_OUTPUT}/${prefix}/site-config
	touch $siteconfig
	for sitefile in ${CONFIG_SITE} ; do
		cat $sitefile >> $siteconfig
	done

	# Create environment setup script
	script=${SDK_OUTPUT}/${prefix}/environment-setup
	touch $script
	echo 'export PATH=${prefix}/bin:$PATH' >> $script
	echo 'export PKG_CONFIG_SYSROOT_DIR=${prefix}/${TARGET_SYS}' >> $script
	echo 'export PKG_CONFIG_PATH=${prefix}/${TARGET_SYS}/lib/pkgconfig' >> $script
	echo 'export CONFIG_SITE=${prefix}/site-config' >> $script
	echo "alias ipkg='LD_LIBRARY_PATH=${prefix}/lib ${prefix}/bin/ipkg-cl -f ${sysconfdir}/ipkg-sdk.conf -o ${prefix}'" >> $script
	echo "alias ipkg-target='LD_LIBRARY_PATH=${prefix}/lib ${prefix}/bin/ipkg-cl -f ${sysconfdir}/ipkg.conf -o ${prefix}/${TARGET_SYS}'" >> $script

	# Add version information
	versionfile=${SDK_OUTPUT}/${prefix}/version
	touch $versionfile
	echo 'Distro: ${DISTRO}' >> $versionfile
	echo 'Distro Version: ${DISTRO_VERSION}' >> $versionfile
	echo 'Metadata Revision: ${METADATA_REVISION}' >> $versionfile
	echo 'Timestamp: ${DATETIME}' >> $versionfile

	# Package it up
	mkdir -p ${SDK_DEPLOY}
	cd ${SDK_OUTPUT}
	fakeroot tar cfj ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.tar.bz2 .
	cd ${SDK_OUTPUT2}
	fakeroot tar cfj ${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}-extras.tar.bz2 .
}

do_populate_sdk[nostamp] = "1"
do_populate_sdk[recrdeptask] = "do_package_write"
addtask populate_sdk before do_build after do_install
