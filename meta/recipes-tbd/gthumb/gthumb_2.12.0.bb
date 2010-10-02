DESCRIPTION = "gThumb is an image viewer and browser for the GNOME Desktop"
SECTION = "x11/gnome"
LICENSE = "GPLv2"
DEPENDS = "glib-2.0 gtk+ libxml2 gnome-doc-utils libunique gconf-dbus libpng gstreamer jpeg tiff"
PR = "r1"

EXTRA_OECONF = "--disable-gnome-keyring --disable-libsoup --disable-exiv2 --disable-clutter"

inherit gnome pkgconfig

FILES_${PN} += "${datadir}/icons"
FILES_${PN}-dbg += "${libdir}/gthumb/modules/.debug"
