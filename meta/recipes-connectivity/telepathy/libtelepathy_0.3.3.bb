SUMMARY = "Telepathy framework"
DESCRIPTION = "Telepathy is a D-Bus framework for unifying real time \
communication, including instant messaging, voice calls and video calls.  It \
abstracts differences between protocols to provide a unified interface for \
applications."
HOMEPAGE = "http://telepathy.freedesktop.org/wiki/"
DEPENDS = "glib-2.0 dbus dbus-glib telepathy-glib"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://src/tp-conn.c;beginline=1;endline=19;md5=4c58069f77d601cc59200bce5396c7cb"
PR = "r3"

SRC_URI = "http://telepathy.freedesktop.org/releases/libtelepathy/libtelepathy-${PV}.tar.gz \
           file://prefer_python_2.5.patch;patch=1 \
           file://doublefix.patch;patch=1"

inherit autotools pkgconfig

FILES_${PN} += "${datadir}/telepathy \
		${datadir}/dbus-1"
