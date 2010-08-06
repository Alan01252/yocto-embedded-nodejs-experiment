require module-init-tools.inc
PR = "r1"

# autotools set prefix to /usr, however we want them in /bin and /sbin
bindir = "/bin"
sbindir = "/sbin"

do_install() {
	autotools_do_install
	for f in bin/lsmod sbin/insmod sbin/rmmod sbin/modprobe sbin/modinfo sbin/depmod; do
		mv ${D}/$f ${D}/$f.26
	done
}

pkg_postinst_module-init-tools() {
	for f in sbin/insmod sbin/modprobe sbin/rmmod sbin/modinfo; do
		bn=`basename $f`
		update-alternatives --install /$f $bn /$f.26 60
	done
	update-alternatives --install /bin/lsmod bin-lsmod /bin/lsmod.26 60
	update-alternatives --install /sbin/lsmod lsmod /bin/lsmod.26 60
}

pkg_prerm_module-init-tools() {
	for f in sbin/insmod sbin/modprobe sbin/rmmod sbin/modinfo; do
		bn=`basename $f`
		update-alternatives --remove $bn /$f.26
	done
	update-alternatives --remove bin-lsmod /bin/lsmod.26
	update-alternatives --remove lsmod /bin/lsmod.26
}

pkg_postinst_module-init-tools-depmod() {
	update-alternatives --install /sbin/depmod depmod /sbin/depmod.26 60
}

pkg_prerm_module-init-tools-depmod() {
	update-alternatives --remove depmod /sbin/depmod.26
}
