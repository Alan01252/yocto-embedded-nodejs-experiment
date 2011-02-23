SUMMARY = "DSSSL stylesheets used to transform SGML and XML DocBook files"
DESCRIPTION = "DSSSL stylesheets used to transform SGML and XML DocBook files"
HOMEPAGE= "http://docbook.sourceforge.net"
# Simple persmissive
LICENSE = "DSSSL"
LIC_FILES_CHKSUM = "file://README;beginline=41;endline=74;md5=875385159b2ee76ecf56136ae7f542d6"

DEPENDS = "sgml-common-native"

PR = "r2"

SRC_URI = "${SOURCEFORGE_MIRROR}/docbook/docbook-dsssl-${PV}.tar.bz2"

SRC_URI[md5sum] = "bc192d23266b9a664ca0aba4a7794c7c"
SRC_URI[sha256sum] = "2f329e120bee9ef42fbdd74ddd60e05e49786c5a7953a0ff4c680ae6bdf0e2bc"

S = "${WORKDIR}/docbook-dsssl-${PV}"

inherit native

SSTATEPOSTINSTFUNCS += "docbook_dsssl_stylesheets_sstate_postinst"

do_install () {
	# Refer to http://www.linuxfromscratch.org/blfs/view/stable/pst/docbook-dsssl.html
	# for details.
	install -d ${D}${bindir}
	install -m 0755 bin/collateindex.pl ${D}${bindir}

	install -d ${D}${datadir}/sgml/docbook/dsssl-stylesheets-${PV}
	install -m 0644 catalog ${D}${datadir}/sgml/docbook/dsssl-stylesheets-${PV}
	cp -v -R * ${D}${datadir}/sgml/docbook/dsssl-stylesheets-${PV}

	install -d ${D}${sysconfdir}/sgml
	echo "CATALOG ${datadir}/sgml/docbook/dsssl-stylesheets-${PV}/catalog" > \
		 ${D}${sysconfdir}/sgml/dsssl-docbook-stylesheets.cat
	echo "CATALOG ${datadir}/sgml/docbook/dsssl-stylesheets-${PV}/common/catalog" >> \
		${D}${sysconfdir}/sgml/dsssl-docbook-stylesheets.cat
}

docbook_dsssl_stylesheets_sstate_postinst () {
	# Ensure that the catalog file sgml-docbook.cat is properly
	# updated when the package is installed from sstate cache.
	install-catalog \
		--add ${sysconfdir}/sgml/sgml-docbook.cat \
		${sysconfdir}/sgml/dsssl-docbook-stylesheets.cat
}
