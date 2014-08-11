SUMMARY = "Command line tool and library for client-side URL transfers"
HOMEPAGE = "http://curl.haxx.se/"
BUGTRACKER = "http://curl.haxx.se/mail/list.cgi?list=curl-tracker"
SECTION = "console/network"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;beginline=7;md5=3a34942f4ae3fbf1a303160714e664ac"

DEPENDS = "zlib gnutls"
DEPENDS_class-native = "zlib-native openssl-native"
DEPENDS_class-nativesdk = "nativesdk-zlib nativesdk-openssl"

SRC_URI = "http://curl.haxx.se/download/curl-${PV}.tar.bz2 \
           file://pkgconfig_fix.patch \
"

# curl likes to set -g0 in CFLAGS, so we stop it
# from mucking around with debug options
#
SRC_URI += " file://configure_ac.patch"

SRC_URI[md5sum] = "95c627abcf6494f5abe55effe7cd6a57"
SRC_URI[sha256sum] = "c3ef3cd148f3778ddbefb344117d7829db60656efe1031f9e3065fc0faa25136"

inherit autotools pkgconfig binconfig multilib_header

PACKAGECONFIG ??= "gnutls ${@bb.utils.contains("DISTRO_FEATURES", "ipv6", "ipv6", "", d)}"
PACKAGECONFIG_class-native = "ipv6 ssl"
PACKAGECONFIG_class-nativesdk = "ipv6 ssl"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[ssl] =  "--with-ssl, --without-ssl, ,"
PACKAGECONFIG[gnutls] =  "--with-gnutls=${STAGING_LIBDIR}/../, --without-gnutls, gnutls,"

EXTRA_OECONF = "--with-zlib=${STAGING_LIBDIR}/../ \
                --without-libssh2 \
                --with-random=/dev/urandom \
                --without-libidn \
                --enable-crypto-auth \
                --disable-ldap \
                --disable-ldaps \
                --with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
                "

do_configure_prepend() {
	sed -i s:OPT_GNUTLS/bin:OPT_GNUTLS:g ${S}/configure.ac
}

do_install_append() {
	oe_multilib_header curl/curlbuild.h
}

PACKAGES =+ "lib${BPN} lib${BPN}-dev lib${BPN}-staticdev lib${BPN}-doc"

FILES_lib${BPN} = "${libdir}/lib*.so.*"
RRECOMMENDS_lib${BPN} += "ca-certificates"
FILES_lib${BPN}-dev = "${includedir} \
                      ${libdir}/lib*.so \
                      ${libdir}/lib*.la \
                      ${libdir}/pkgconfig \
                      ${datadir}/aclocal \
                      ${bindir}/*-config"
FILES_lib${BPN}-staticdev = "${libdir}/lib*.a"
FILES_lib${BPN}-doc = "${mandir}/man3 \
                      ${mandir}/man1/curl-config.1"

BBCLASSEXTEND = "native nativesdk"
