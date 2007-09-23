DESCRIPTION = "NetworkManager"
SECTION = "net/misc"
LICENSE = "GPL"
HOMEPAGE = "http://www.gnome.org"
PRIORITY = "optional"
DEPENDS = "libnl dbus dbus-glib hal gconf-dbus wireless-tools"
RDEPENDS = "hal wpa-supplicant iproute2 dhcdbd"
PV = "0.6.4+svn${SRCDATE}"

PR = "r6"

SRC_URI="svn://svn.gnome.org/svn/NetworkManager/branches;module=NETWORKMANAGER_0_6_0_RELEASE;proto=http \
	file://25NetworkManager \
	file://99_networkmanager"

EXTRA_OECONF = " \
		--without-gnome \
		--with-distro=debian \
		--without-gcrypt \
 		--with-wpa_supplicant=/usr/sbin/wpa_supplicant \
		--with-dhcdbd=/sbin/dhcdbd \
		--with-ip=/sbin/ip"

S = "${WORKDIR}/NETWORKMANAGER_0_6_0_RELEASE"

inherit autotools pkgconfig

do_staging () {
	autotools_stage_includes
	oe_libinstall -C libnm-util libnm-util ${STAGING_LIBDIR}
	oe_libinstall gnome/libnm_glib libnm_glib ${STAGING_LIBDIR}
}

do_install_append () {
	install -d ${D}/etc/default/volatiles
	install -m 0644 ${WORKDIR}/99_networkmanager ${D}/etc/default/volatiles
	install -d ${D}/etc/dbus-1/event.d
	install -m 0755 ${WORKDIR}/25NetworkManager ${D}/etc/dbus-1/event.d
}

pkg_postinst () {
	/etc/init.d/populate-volatile.sh update
}

FILES_${PN} += "${libdir}/*.so."

FILES_${PN}-dev = "${includedir}/* \
        ${libdir}/*.so \
        ${libdir}/*.a \
        ${libdir}/pkgconfig/*.pc \
        ${datadir}/NetworkManager/gdb-cmd \
        "
