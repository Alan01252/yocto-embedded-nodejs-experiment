#
# This class will generate the proper postinst/postrm scriptlets for font
# packages.
#

DEPENDS += "qemu-native"
inherit qemu

FONT_PACKAGES ??= "${PN}"

fontcache_common() {
if [ "x$D" != "x" ] ; then
	if [ ! -f $INTERCEPT_DIR/update_font_cache ]; then
		cat << "EOF" > $INTERCEPT_DIR/update_font_cache
#!/bin/sh

${@qemu_run_binary(d, '$D', '/usr/bin/fc-cache')} --sysroot=$D >/dev/null 2>&1

if [ $? -ne 0 ]; then
    exit 1
fi

EOF
	fi
	exit 0
fi

fc-cache
}

python populate_packages_append() {
    font_pkgs = d.getVar('FONT_PACKAGES', True).split()

    for pkg in font_pkgs:
        bb.note("adding fonts postinst and postrm scripts to %s" % pkg)
        postinst = d.getVar('pkg_postinst_%s' % pkg, True) or d.getVar('pkg_postinst', True)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('fontcache_common', True)
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm_%s' % pkg, True) or d.getVar('pkg_postrm', True)
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('fontcache_common', True)
        d.setVar('pkg_postrm_%s' % pkg, postrm)
}
