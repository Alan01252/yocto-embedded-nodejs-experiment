DESCRIPTION = "System-V like init."
DESCRIPTION = "This package is required to boot in most configurations.  It provides the /sbin/init program.  This is the first process started on boot, and the last process terminated before the system halts."
HOMEPAGE = "http://savannah.nongnu.org/projects/sysvinit/"
SECTION = "base"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYRIGHT;endline=15;md5=349c872e0066155e1818b786938876a4"
PR = "r11"

RDEPENDS_${PN} = "${PN}-inittab"

SRC_URI = "http://download.savannah.gnu.org/releases-noredirect/sysvinit/sysvinit-${PV}.tar.bz2 \
           file://install.patch \
           file://crypt-lib.patch \
           file://pidof-add-m-option.patch \
           file://rcS-default \
           file://rc \
           file://rcS \
           file://bootlogd.init"

SRC_URI[md5sum] = "6eda8a97b86e0a6f59dabbf25202aa6f"
SRC_URI[sha256sum] = "60bbc8c1e1792056e23761d22960b30bb13eccc2cabff8c7310a01f4d5df1519"

S = "${WORKDIR}/sysvinit-${PV}"
B = "${S}/src"

inherit update-alternatives
DEPENDS_append = " update-rc.d-native base-passwd"

ALTERNATIVE_${PN} = "init mountpoint halt reboot runlevel shutdown poweroff last mesg utmpdump wall"

ALTERNATIVE_PRIORITY = "200"

ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] = "50"

ALTERNATIVE_LINK_NAME[mountpoint] = "${base_bindir}/mountpoint"
ALTERNATIVE_LINK_NAME[halt] = "${base_sbindir}/halt"
ALTERNATIVE_LINK_NAME[reboot] = "${base_sbindir}/reboot"
ALTERNATIVE_LINK_NAME[runlevel] = "${base_sbindir}/runlevel"
ALTERNATIVE_LINK_NAME[shutdown] = "${base_sbindir}/shutdown"
ALTERNATIVE_LINK_NAME[poweroff] = "${base_sbindir}/poweroff"

ALTERNATIVE_${PN}-pidof = "pidof"
ALTERNATIVE_LINK_NAME[pidof] = "${base_bindir}/pidof"

ALTERNATIVE_${PN}-sulogin = "sulogin"
ALTERNATIVE_LINK_NAME[sulogin] = "${base_sbindir}/sulogin"

ALTERNATIVE_${PN}-doc = "mountpoint.1 last.1 mesg.1 wall.1 sulogin.8 utmpdump.1"

ALTERNATIVE_LINK_NAME[last.1] = "${mandir}/man1/last.1"
ALTERNATIVE_LINK_NAME[mesg.1] = "${mandir}/man1/mesg.1"
ALTERNATIVE_LINK_NAME[mountpoint.1] = "${mandir}/man1/mountpoint.1"
ALTERNATIVE_LINK_NAME[sulogin.8] = "${mandir}/man8/sulogin.8"
ALTERNATIVE_LINK_NAME[utmpdump.1] = "${mandir}/man1/utmpdump.1"
ALTERNATIVE_LINK_NAME[wall.1] = "${mandir}/man1/wall.1"

PACKAGES =+ "sysvinit-pidof sysvinit-sulogin"
FILES_${PN} += "${base_sbindir}/* ${base_bindir}/*"
FILES_sysvinit-pidof = "${base_bindir}/pidof.sysvinit ${base_sbindir}/killall5"
FILES_sysvinit-sulogin = "${base_sbindir}/sulogin.sysvinit"

RDEPENDS_${PN} += "sysvinit-pidof"

CFLAGS_prepend = "-D_GNU_SOURCE "
export LCRYPT = "-lcrypt"
EXTRA_OEMAKE += "'base_bindir=${base_bindir}' \
		 'base_sbindir=${base_sbindir}' \
		 'bindir=${bindir}' \
		 'sbindir=${sbindir}' \
		 'sysconfdir=${sysconfdir}' \
		 'includedir=${includedir}' \
		 'mandir=${mandir}'"

do_install () {
	oe_runmake 'ROOT=${D}' install

	install -d ${D}${sysconfdir} \
		   ${D}${sysconfdir}/default \
		   ${D}${sysconfdir}/init.d
	for level in S 0 1 2 3 4 5 6; do
		install -d ${D}${sysconfdir}/rc$level.d
	done

	install -m 0644    ${WORKDIR}/rcS-default	${D}${sysconfdir}/default/rcS
	install -m 0755    ${WORKDIR}/rc		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/rcS		${D}${sysconfdir}/init.d
	install -m 0755    ${WORKDIR}/bootlogd.init     ${D}${sysconfdir}/init.d/bootlogd
	ln -sf bootlogd ${D}${sysconfdir}/init.d/stop-bootlogd

	update-rc.d -r ${D} bootlogd start 07 S .
	update-rc.d -r ${D} stop-bootlogd start 99 2 3 4 5 .

	chown root.shutdown ${D}${base_sbindir}/halt ${D}${base_sbindir}/shutdown
	chmod o-x,u+s ${D}${base_sbindir}/halt ${D}${base_sbindir}/shutdown
}
