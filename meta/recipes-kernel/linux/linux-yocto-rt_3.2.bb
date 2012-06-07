inherit kernel
require recipes-kernel/linux/linux-yocto.inc

KBRANCH = "standard/preempt-rt/base"
KBRANCH_qemuppc = "standard/preempt-rt/qemu-ppc32"

LINUX_VERSION ?= "3.2.18"
LINUX_KERNEL_TYPE = "preempt-rt"

KMETA = "meta"

SRCREV_machine ?= "fe2630b38159ea7b9cf977b5fed40a9917002087"
SRCREV_machine_qemuppc ?= "0259e5a3ef568c6979f2cb31280a43c55c784f4f"
SRCREV_meta ?= "0a18db9fc89a0e030e8c7b8d01fe03c5ca4197e3"

PR = "r1"
PV = "${LINUX_VERSION}+git${SRCPV}"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-3.2.git;protocol=git;bareclone=1;branch=${KBRANCH},meta;name=machine,meta"

# Omit broken machines from COMPATIBLE_MACHINE
#   qemuppc hangs at boot
#   qemumips panics at boot
COMPATIBLE_MACHINE = "(qemux86|qemux86-64|qemuarm)"

# Functionality flags
KERNEL_FEATURES = "features/netfilter"
KERNEL_FEATURES_append = " features/taskstats"
KERNEL_FEATURES_append_qemux86 = " cfg/sound"
KERNEL_FEATURES_append_qemux86-64 = " cfg/sound"

require recipes-kernel/linux/linux-tools.inc
