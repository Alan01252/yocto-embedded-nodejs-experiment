DESCRIPTION = "Custom MB session files for poky"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2.0+"
LIC_FILES_CHKSUM = "file://session;endline=3;md5=f8a5c5b9c279e52dc094d10e11c2be63"

SECTION = "x11"
DEPENDS = "gconf-native"
RDEPENDS_${PN} = "formfactor gtk-sato-engine matchbox-theme-sato gtk-theme-sato matchbox-panel-2 matchbox-desktop-sato matchbox-session gconf"
PR = "r30"

# This package is architecture specific because the session script is modified
# based on the machine architecture.
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://session"
S = "${WORKDIR}"

do_install() {
	# This is the set of machine features that the script has markers for
	FEATURES="phone"
	SCRIPT="${S}/sedder"
	rm -f $SCRIPT
	touch $SCRIPT
	for FEAT in $FEATURES; do
		if echo ${MACHINE_FEATURES} | awk "/$FEAT/ {exit 1}"; then
			echo "/feature-$FEAT/d" >> $SCRIPT
		fi
	done

	install -d ${D}/${sysconfdir}/matchbox
	sed -f "$SCRIPT" ${S}/session > ${D}/${sysconfdir}/matchbox/session
        chmod +x ${D}/${sysconfdir}/matchbox/session
}

pkg_postinst_${PN} () {
	set_value() {
		#type, name, value
		gconftool-2 --config-source=xml::$D${sysconfdir}/gconf/gconf.xml.defaults --direct --type $1 --set /desktop/poky/interface/$2 "$3"
	}
	set_value string theme Sato
	set_value string icon_theme Sato
	set_value bool touchscreen true
	set_value string font_name "Sans 9"
}
