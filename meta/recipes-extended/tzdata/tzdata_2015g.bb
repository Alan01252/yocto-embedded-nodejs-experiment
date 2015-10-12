SUMMARY = "Timezone data"
HOMEPAGE = "http://www.iana.org/time-zones"
SECTION = "base"
LICENSE = "PD & BSD"
LIC_FILES_CHKSUM = "file://asia;beginline=2;endline=3;md5=996a9811747aa48db91ed239e5b355a1 \
                    file://README;md5=d0ff93a73dd5bc3c6e724bb4343760f6"
DEPENDS = "tzcode-native"

SRC_URI = "ftp://ftp.iana.org/tz/releases/tzdata${PV}.tar.gz;name=tzdata"

SRC_URI[tzdata.md5sum] = "8d46e8b225b9a04c75f5c39636435ad6"
SRC_URI[tzdata.sha256sum] = "b923cdbf078491696b17bc8d069c74bce73fabc5774629da2f410c9b31576161"

inherit allarch

RCONFLICTS_${PN} = "timezones timezone-africa timezone-america timezone-antarctica \
             timezone-arctic timezone-asia timezone-atlantic \
             timezone-australia timezone-europe timezone-indian \
             timezone-iso3166.tab timezone-pacific timezone-zone.tab"

S = "${WORKDIR}"

DEFAULT_TIMEZONE ?= "Universal"

TZONES= "africa antarctica asia australasia europe northamerica southamerica  \
         factory etcetera backward systemv \
        "
# pacificnew 

do_compile () {
        for zone in ${TZONES}; do \
            ${STAGING_BINDIR_NATIVE}/zic -d ${WORKDIR}${datadir}/zoneinfo -L /dev/null \
                -y ${S}/yearistype.sh ${S}/${zone} ; \
            ${STAGING_BINDIR_NATIVE}/zic -d ${WORKDIR}${datadir}/zoneinfo/posix -L /dev/null \
                -y ${S}/yearistype.sh ${S}/${zone} ; \
            ${STAGING_BINDIR_NATIVE}/zic -d ${WORKDIR}${datadir}/zoneinfo/right -L ${S}/leapseconds \
                -y ${S}/yearistype.sh ${S}/${zone} ; \
        done
}

do_install () {
        install -d ${D}/$exec_prefix ${D}${datadir}/zoneinfo
        cp -pPR ${S}/$exec_prefix ${D}/
        # libc is removing zoneinfo files from package
        cp -pP "${S}/zone.tab" ${D}${datadir}/zoneinfo
        cp -pP "${S}/iso3166.tab" ${D}${datadir}/zoneinfo

        # Install default timezone
        if [ -e ${D}${datadir}/zoneinfo/${DEFAULT_TIMEZONE} ]; then
            install -d ${D}${sysconfdir}
            echo ${DEFAULT_TIMEZONE} > ${D}${sysconfdir}/timezone
            ln -s ${datadir}/zoneinfo/${DEFAULT_TIMEZONE} ${D}${sysconfdir}/localtime
        else
            bberror "DEFAULT_TIMEZONE is set to an invalid value."
            exit 1
        fi

        chown -R root:root ${D}
}

pkg_postinst_${PN} () {
	etc_lt="$D${sysconfdir}/localtime"
	src="$D${sysconfdir}/timezone"

	if [ -e ${src} ] ; then
		tz=$(sed -e 's:#.*::' -e 's:[[:space:]]*::g' -e '/^$/d' "${src}")
	fi
	
	if [ -z "${tz}" ] ; then
		exit 0
	fi
	
	if [ ! -e "$D${datadir}/zoneinfo/${tz}" ] ; then
		echo "You have an invalid TIMEZONE setting in ${src}"
		echo "Your ${etc_lt} has been reset to Universal; enjoy!"
		tz="Universal"
		echo "Updating ${etc_lt} with $D${datadir}/zoneinfo/${tz}"
		if [ -L ${etc_lt} ] ; then
			rm -f "${etc_lt}"
		fi
		ln -s "${datadir}/zoneinfo/${tz}" "${etc_lt}"
	fi
}

# Packages primarily organized by directory with a major city
# in most time zones in the base package

PACKAGES = "tzdata tzdata-misc tzdata-posix tzdata-right tzdata-africa \
    tzdata-americas tzdata-antarctica tzdata-arctic tzdata-asia \
    tzdata-atlantic tzdata-australia tzdata-europe tzdata-pacific"

FILES_tzdata-africa += "${datadir}/zoneinfo/Africa/*"
RPROVIDES_tzdata-africa = "tzdata-africa"

FILES_tzdata-americas += "${datadir}/zoneinfo/America/*  \
                ${datadir}/zoneinfo/US/*                \
                ${datadir}/zoneinfo/Brazil/*            \
                ${datadir}/zoneinfo/Canada/*            \
                ${datadir}/zoneinfo/Mexico/*            \
                ${datadir}/zoneinfo/Chile/*"
RPROVIDES_tzdata-americas = "tzdata-americas"

FILES_tzdata-antarctica += "${datadir}/zoneinfo/Antarctica/*"
RPROVIDES_tzdata-antarctica = "tzdata-antarctica"

FILES_tzdata-arctic += "${datadir}/zoneinfo/Arctic/*"
RPROVIDES_tzdata-arctic = "tzdata-arctic"

FILES_tzdata-asia += "${datadir}/zoneinfo/Asia/*        \
                ${datadir}/zoneinfo/Indian/*            \
                ${datadir}/zoneinfo/Mideast/*"
RPROVIDES_tzdata-asia = "tzdata-asia"

FILES_tzdata-atlantic += "${datadir}/zoneinfo/Atlantic/*"
RPROVIDES_tzdata-atlantic = "tzdata-atlantic"

FILES_tzdata-australia += "${datadir}/zoneinfo/Australia/*"
RPROVIDES_tzdata-australia = "tzdata-australia"

FILES_tzdata-europe += "${datadir}/zoneinfo/Europe/*"
RPROVIDES_tzdata-europe = "tzdata-europe"

FILES_tzdata-pacific += "${datadir}/zoneinfo/Pacific/*"
RPROVIDES_tzdata-pacific = "tzdata-pacific"

FILES_tzdata-posix += "${datadir}/zoneinfo/posix/*"
RPROVIDES_tzdata-posix = "tzdata-posix"

FILES_tzdata-right += "${datadir}/zoneinfo/right/*"
RPROVIDES_tzdata-right = "tzdata-right"


FILES_tzdata-misc += "${datadir}/zoneinfo/Cuba           \
                ${datadir}/zoneinfo/Egypt                \
                ${datadir}/zoneinfo/Eire                 \
                ${datadir}/zoneinfo/Factory              \
                ${datadir}/zoneinfo/GB-Eire              \
                ${datadir}/zoneinfo/Hongkong             \
                ${datadir}/zoneinfo/Iceland              \
                ${datadir}/zoneinfo/Iran                 \
                ${datadir}/zoneinfo/Israel               \
                ${datadir}/zoneinfo/Jamaica              \
                ${datadir}/zoneinfo/Japan                \
                ${datadir}/zoneinfo/Kwajalein            \
                ${datadir}/zoneinfo/Libya                \
                ${datadir}/zoneinfo/Navajo               \
                ${datadir}/zoneinfo/Poland               \
                ${datadir}/zoneinfo/Portugal             \
                ${datadir}/zoneinfo/Singapore            \
                ${datadir}/zoneinfo/Turkey"
RPROVIDES_tzdata-misc = "tzdata-misc"


FILES_${PN} += "${datadir}/zoneinfo/Pacific/Honolulu     \
                ${datadir}/zoneinfo/America/Anchorage    \
                ${datadir}/zoneinfo/America/Los_Angeles  \
                ${datadir}/zoneinfo/America/Denver       \
                ${datadir}/zoneinfo/America/Chicago      \
                ${datadir}/zoneinfo/America/New_York     \
                ${datadir}/zoneinfo/America/Caracas      \
                ${datadir}/zoneinfo/America/Sao_Paulo    \
                ${datadir}/zoneinfo/Europe/London        \
                ${datadir}/zoneinfo/Europe/Paris         \
                ${datadir}/zoneinfo/Africa/Cairo         \
                ${datadir}/zoneinfo/Europe/Moscow        \
                ${datadir}/zoneinfo/Asia/Dubai           \
                ${datadir}/zoneinfo/Asia/Karachi         \
                ${datadir}/zoneinfo/Asia/Dhaka           \
                ${datadir}/zoneinfo/Asia/Bankok          \
                ${datadir}/zoneinfo/Asia/Hong_Kong       \
                ${datadir}/zoneinfo/Asia/Tokyo           \
                ${datadir}/zoneinfo/Australia/Darwin     \
                ${datadir}/zoneinfo/Australia/Adelaide   \
                ${datadir}/zoneinfo/Australia/Brisbane   \
                ${datadir}/zoneinfo/Australia/Sydney     \
                ${datadir}/zoneinfo/Pacific/Noumea       \
                ${datadir}/zoneinfo/CET                  \
                ${datadir}/zoneinfo/CST6CDT              \
                ${datadir}/zoneinfo/EET                  \
                ${datadir}/zoneinfo/EST                  \
                ${datadir}/zoneinfo/EST5EDT              \
                ${datadir}/zoneinfo/GB                   \
                ${datadir}/zoneinfo/GMT                  \
                ${datadir}/zoneinfo/GMT+0                \
                ${datadir}/zoneinfo/GMT-0                \
                ${datadir}/zoneinfo/GMT0                 \
                ${datadir}/zoneinfo/Greenwich            \
                ${datadir}/zoneinfo/HST                  \
                ${datadir}/zoneinfo/MET                  \
                ${datadir}/zoneinfo/MST                  \
                ${datadir}/zoneinfo/MST7MDT              \
                ${datadir}/zoneinfo/NZ                   \
                ${datadir}/zoneinfo/NZ-CHAT              \
                ${datadir}/zoneinfo/PRC                  \
                ${datadir}/zoneinfo/PST8PDT              \
                ${datadir}/zoneinfo/ROC                  \
                ${datadir}/zoneinfo/ROK                  \
                ${datadir}/zoneinfo/UCT                  \
                ${datadir}/zoneinfo/UTC                  \
                ${datadir}/zoneinfo/Universal            \
                ${datadir}/zoneinfo/W-SU                 \
                ${datadir}/zoneinfo/WET                  \
                ${datadir}/zoneinfo/Zulu                 \
                ${datadir}/zoneinfo/zone.tab             \
                ${datadir}/zoneinfo/iso3166.tab          \
                ${datadir}/zoneinfo/Etc/*"

CONFFILES_${PN} += "${sysconfdir}/timezone ${sysconfdir}/localtime"
