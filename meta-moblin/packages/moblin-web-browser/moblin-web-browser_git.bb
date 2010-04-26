DESCRIPTION = "Moblin web browser (based on clutter + mozilla-headless/mozilla-offscreen)"
SRC_URI = "git://git.moblin.org/${PN}.git;protocol=git;branch=${MOBLINBROWSERBRANCH} \
           file://xpidl-a3ea71159bef93dc03762195bd5404a51db5e4a6.patch;patch=1;rev=a3ea71159bef93dc03762195bd5404a51db5e4a6;notrev=78ddd155cc297811720e4c4835d468ac6c4d9666 \
           file://xpidl-78ddd155cc297811720e4c4835d468ac6c4d9666.patch;patch=1;rev=78ddd155cc297811720e4c4835d468ac6c4d9666;notrev=a3ea71159bef93dc03762195bd5404a51db5e4a6 \
           file://xpidl.patch;patch=1;notrev=a3ea71159bef93dc03762195bd5404a51db5e4a6;notrev=78ddd155cc297811720e4c4835d468ac6c4d9666"
LICENSE = "LGPLv2.1"
PV = "0.0+git${SRCPV}"
PR = "r11"

DEPENDS = "clutter-1.0 clutter-mozembed clutter-gtk-0.10 libunique mozilla-headless-services libccss nbtk mozilla-headless mutter-moblin"

S = "${WORKDIR}/git"

MOBLINBROWSERBRANCH ?= "master"

EXTRA_OECONF = "--with-idl-prefix=${STAGING_DIR_TARGET}/"

FILES_${PN} += "${datadir}/moblin-web-browser/chrome/* ${libdir}/xulrunner-*/chrome/* ${datadir}/dbus-1/services/*"
FILES_${PN} += "${datadir}/moblin-web-browser/components/* ${libdir}/xulrunner-*/components/*"
FILES_${PN}-dbg += "${datadir}/moblin-web-browser/components/.debug/* ${libdir}/xulrunner-*/components/.debug/*"



inherit autotools
