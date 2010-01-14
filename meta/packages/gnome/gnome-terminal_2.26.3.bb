DESCRIPTION = "GNOME Terminal"
LICENSE = "GPL"
DEPENDS = "gtk+ glib-2.0 startup-notification dbus-glib vte"
PR = "r1"

inherit gnome

SRC_URI += "file://30f29e7d8e1b67c40cd18a7155ba30c4382692d5.patch;patch=1"

# Remove an autogenerated file that needs to be rebuilt
do_configure_prepend () {
	rm -f ${S}/src/terminal-type-builtins.c
}

