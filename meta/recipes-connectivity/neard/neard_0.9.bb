SUMMARY = "Linux NFC daemon"
DESCRIPTION = "A daemon for the Linux Near Field Communication stack"
HOMEPAGE = "http://01.org/linux-nfc"
LICENSE = "GPLv2"

DEPENDS = "dbus glib-2.0 libnl"

SRC_URI = "git://git.kernel.org/pub/scm/network/nfc/neard.git;protocol=git \
           file://neard.in \
           file://neard.service.in \
          "

LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
 file://src/near.h;beginline=1;endline=20;md5=358e4deefef251a4761e1ffacc965d13 \
 "

S = "${WORKDIR}/git"
SRCREV = "eb486bf35e24d7d1db61350f5ab393a0c880523d"
PV = "0.10+git${SRCPV}"
PR = "r0"

inherit autotools pkgconfig systemd update-rc.d

EXTRA_OECONF += "--enable-tools"

do_install() {
	oe_runmake DESTDIR=${D} libexecdir=${libexecdir} install
}

# This would copy neard start-stop shell and test scripts
do_install_append() {
	if ${@base_contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/init.d/
		sed "s:@installpath@:${libexecdir}:" ${WORKDIR}/neard.in \
		  > ${D}${sysconfdir}/init.d/neard
		chmod 0755 ${D}${sysconfdir}/init.d/neard
	fi

	if ${@base_contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_unitdir}/system
		sed "s:@installpath@:${libexecdir}:" ${WORKDIR}/neard.service.in \
		  > ${D}${systemd_unitdir}/system/neard.service
	fi

	# Install the tests for neard-tests
	install -d ${D}${libdir}/neard
	install -m 0755 ${S}/test/* ${D}${libdir}/${BPN}/
	install -m 0755 ${S}/tools/nfctool/nfctool ${D}${libdir}/${BPN}/
}

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests = "${libdir}/${BPN}/*-test"
FILES_${PN}-dbg += "${libdir}/${BPN}/*/.debug"

RDEPENDS_${PN} = "dbus python python-dbus python-pygobject"

# Bluez & Wifi are not mandatory except for handover
RRECOMMENDS_${PN} = "\
                     ${@base_contains('DISTRO_FEATURES', 'bluetooth', 'bluez4', '', d)} \
                     ${@base_contains('DISTRO_FEATURES', 'wifi','wpa-supplicant', '', d)} \
                    "

RDEPENDS_${PN}-tests = "python python-dbus python-pygobject"

INITSCRIPT_NAME = "neard"
INITSCRIPT_PARAMS = "defaults 64"

SYSTEMD_SERVICE_${PN} = "neard.service"
