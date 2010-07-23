DESCRIPTION = "Wind River Kernel"
SECTION = "kernel"
LICENSE = "GPL"

# Set this to 'preempt_rt' in the local.conf if you want a real time kernel
LINUX_KERNEL_TYPE ?= standard
DEPENDS = "kern-tools-native"
PV = "2.6.34+git${SRCPV}"

# To use a staged, on-disk bare clone of a Wind River Kernel, use a 
# variant of the below
# SRC_URI = "git://///path/to/kernel/default_kernel.git;fullclone=1"
SRC_URI = "git://git.pokylinux.org/linux-2.6-windriver.git;protocol=git;fullclone=1;branch=${WRMACHINE}-${LINUX_KERNEL_TYPE}"


WRMACHINE = "${MACHINE}"
WRMACHINE_qemux86  = "common_pc"
WRMACHINE_qemuppc  = "qemu_ppc32"
WRMACHINE_qemumips = "mti_malta32_be"
WRMACHINE_qemuarm  = "arm_versatile_926ejs"

COMPATIBLE_MACHINE = "(qemuarm|qemux86|qemuppc|qemumips)"

LINUX_VERSION = "v2.6.34"
LINUX_VERSION_EXTENSION = "-wr-${LINUX_KERNEL_TYPE}"
PR = "r2"

S = "${WORKDIR}/linux"
B = "${WORKDIR}/linux-${WRMACHINE}-${LINUX_KERNEL_TYPE}-build"

do_patch() {
    echo "[INFO] Patching is currently not supported"
}

do_wrlinux_checkout() {
	if [ -d ${WORKDIR}/.git/refs/remotes/origin ]; then
		echo "Fixing up git directory for ${WRMACHINE}-${LINUX_KERNEL_TYPE}"
		rm -rf ${S}
		mkdir ${S}
		mv ${WORKDIR}/.git ${S}
		mv ${S}/.git/refs/remotes/origin/* ${S}/.git/refs/heads
		rmdir ${S}/.git/refs/remotes/origin
	fi
	cd ${S}
	git checkout -f ${WRMACHINE}-${LINUX_KERNEL_TYPE}
}

addtask wrlinux_checkout before do_patch after do_unpack

do_wrlinux_configme() {
	echo "Doing wrlinux configme"
	rm -rf ${B}
	cd ${S}
	configme
	echo "# CONFIG_WRNOTE is not set" >> ${B}/.config
	echo "# Global settings from linux recipe" >> ${B}/.config
	echo "CONFIG_LOCALVERSION="\"${LINUX_VERSION_EXTENSION}\" >> ${B}/.config
}

do_wrlinux_configcheck() {
	echo "[INFO] validating kernel configuration"
	cd ${B}/..
	kconf_check ${B}/.config ${B} ${S} ${B} ${LINUX_VERSION} ${WRMACHINE}-${LINUX_KERNEL_TYPE}
}

do_wrlinux_link_vmlinux() {
	cd ${B}/arch/${ARCH}/boot
	ln -sf ../../../vmlinux
}

do_wrlinux_configme[depends] = "kern-tools-native:do_populate_sysroot"
addtask wrlinux_configme before do_configure after do_patch
addtask wrlinux_link_vmlinux after do_compile before do_install

# XXX 
#addtask wrlinux_configcheck after do_configure before do_compile

inherit kernel

# object files are in B, not S, so we need to override this
do_deploy[dirs] = "${B}"
