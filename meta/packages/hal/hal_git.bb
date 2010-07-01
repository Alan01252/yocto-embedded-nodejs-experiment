DESCRIPTION = "Hardware Abstraction Layer"
HOMEPAGE = "http://freedesktop.org/Software/hal"
BUGTRACKER = "http://bugs.freedesktop.org/buglist.cgi?product=hal"
SECTION = "unknown"

LICENSE = "GPLv2+ | AFL"

DEPENDS = "virtual/kernel dbus-glib udev intltool-native expat libusb"
RDEPENDS_${PN} += "udev hal-info"
RRECOMMENDS_${PN} += "udev-utils"

PV = "0.5.9.1+git${SRCDATE}"
PR = "r4"

SRC_URI = "git://anongit.freedesktop.org/hal/;protocol=git \
        file://20hal \
        file://99_hal"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--with-hwdata=${datadir}/hwdata \
                --with-expat=${STAGING_DIR_HOST}${prefix} \
                --with-dbus-sys=${sysconfdir}/dbus-1/system.d \
                --with-hotplug=${sysconfdir}/hotplug.d \
                --disable-docbook-docs \
                --disable-policy-kit \
                --disable-acpi --disable-acpi-acpid --disable-acpi-proc \
                --disable-sonypic \
                --disable-pmu --disable-pci \
                --disable-pci-ids --disable-pnp-ids \
                "

do_install_append() {
	install -d ${D}/etc/default/volatiles
	install -m 0644 ${WORKDIR}/99_hal ${D}/etc/default/volatiles
	install -d ${D}/etc/dbus-1/event.d
	install -m 0755 ${WORKDIR}/20hal ${D}/etc/dbus-1/event.d
}

# At the time the postinst runs, dbus might not be setup so only restart if running
pkg_postinst_hal () {
	# can't do this offline
	if [ "x$D" != "x" ]; then
		exit 1
	fi

	/etc/init.d/populate-volatile.sh update

	grep haldaemon /etc/group || addgroup haldaemon
	grep haldaemon /etc/passwd || adduser --disabled-password --system --home /var/run/hald --no-create-home haldaemon --ingroup haldaemon -g HAL

	DBUSPID=`pidof dbus-daemon`

	if [ "x$DBUSPID" != "x" ]; then
		/etc/init.d/dbus-1 reload
	fi
}

pkg_postrm_hal () {
	deluser haldaemon || true
	delgroup haldaemon || true
}

FILES_${PN} = "${sysconfdir} \
                ${bindir}/lshal \
                ${bindir}/hal-find-by-capability \
                ${bindir}/hal-find-by-property \
                ${bindir}/hal-device  \
                ${bindir}/hal-get-property \
                ${bindir}/hal-set-property  \
                ${bindir}/hal-lock  \
                ${bindir}/hal-is-caller-locked-out  \
                ${bindir}/hal-disable-polling  \
                ${sbindir} \
                ${libdir}/libhal.so.* \
                ${libdir}/libhal-storage.so.* \
                ${libdir}/hal \
                ${libexecdir} \
                ${datadir}/hal/fdi \
                ${datadir}/hal/scripts"
