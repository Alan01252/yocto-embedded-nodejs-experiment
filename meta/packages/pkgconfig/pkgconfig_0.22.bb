SECTION = "console/utils"
DESCRIPTION = "pkg-config is a system for managing library \
compile/link flags that works with automake and autoconf. \
It replaces the ubiquitous *-config scripts you may have \
seen with a single tool."
HOMEPAGE = "http://pkg-config.freedesktop.org/wiki/"
LICENSE = "GPL"

SRC_URI = "http://pkgconfig.freedesktop.org/releases/pkg-config-${PV}.tar.gz \
           file://glibconfig-sysdefs.h"

S = "${WORKDIR}/pkg-config-${PV}/"

inherit autotools

acpaths = "-I ."
do_configure () {
	install -m 0644 ${WORKDIR}/glibconfig-sysdefs.h glib-1.2.8/
	gnu-configize
	libtoolize --force
        oe_runconf
}

do_stage_prepend() {
	install -d -m 0755 ${STAGING_DATADIR}/pkgconfig

}
