DESCRIPTION = "Library for ASN.1 and DER manipulation"
HOMEPAGE = "http://www.gnu.org/software/libtasn1/"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN}-bin = "GPLv3+"
LICENSE_${PN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://README;endline=8;md5=c3803a3e8ca5ab5eb1e5912faa405351"

#RREPLACES_${PN}-bin = "libtasn1 (<< 2.7)"

PR = "r1"

SRC_URI = "ftp://ftp.gnutls.org/pub/gnutls/libtasn1/libtasn1-${PV}.tar.gz"

inherit autotools binconfig lib_package

AUTOTOOLS_STAGE_PKGCONFIG = "1"

BBCLASSEXTEND = "native"
