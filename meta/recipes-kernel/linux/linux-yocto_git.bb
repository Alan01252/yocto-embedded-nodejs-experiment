inherit kernel
require linux-yocto.inc

KMACHINE_qemux86  = "yocto/standard/common-pc/base"
KMACHINE_qemux86-64  = "yocto/standard/common-pc-64"
KMACHINE_qemuppc  = "yocto/standard/qemu-ppc32"
KMACHINE_qemumips = "yocto/standard/mti-malta32-be"
KMACHINE_qemuarm  = "yocto/standard/arm-versatile-926ejs"
KMACHINE_atom-pc  = "yocto/standard/common-pc/atom-pc"
KMACHINE_routerstationpro = "yocto/standard/routerstationpro"
KMACHINE_mpc8315e-rdb = "yocto/standard/fsl-mpc8315e-rdb"
KMACHINE_beagleboard = "yocto/standard/beagleboard"

LINUX_VERSION ?= "2.6.37"
LINUX_VERSION_EXTENSION ?= "-yocto-${LINUX_KERNEL_TYPE}"
PR = "r15"
PV = "${LINUX_VERSION}+git${SRCPV}"
SRCREV_FORMAT = "meta_machine"

SRC_URI = "git://git.pokylinux.org/linux-yocto-2.6.37;protocol=git;fullclone=1;branch=${KBRANCH};name=machine \
           git://git.pokylinux.org/linux-yocto-2.6.37;protocol=git;noclone=1;branch=meta;name=meta \
           file://tools-perf-no-scripting.patch"

COMPATIBLE_MACHINE = "(qemuarm|qemux86|qemuppc|qemumips|qemux86-64|mpc8315e-rdb|routerstationpro)"

# Functionality flags
KERNEL_REVISION_CHECKING ?= "t"
KERNEL_FEATURES=features/netfilter

# extra tasks
addtask kernel_link_vmlinux after do_compile before do_install
addtask validate_branches before do_patch after do_kernel_checkout

require linux-tools.inc
