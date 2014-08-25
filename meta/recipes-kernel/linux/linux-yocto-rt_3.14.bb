require recipes-kernel/linux/linux-yocto.inc

KBRANCH ?= "standard/preempt-rt/base"
KBRANCH_qemuppc ?= "standard/preempt-rt/qemuppc"

SRCREV_machine ?= "21561a4c2865f79bd3489087eed19b26839e33f1"
SRCREV_machine_qemuppc ?= "61cd09f76a3632ccc02a9e471220f7b5fcb61b93"
SRCREV_meta ?= "ccad961c4cb6be245ed198bd2c17c27ab33cfcd7"

SRC_URI = "git://git.yoctoproject.org/linux-yocto-3.14.git;bareclone=1;branch=${KBRANCH},meta;name=machine,meta"

LINUX_VERSION ?= "3.14.13"

PV = "${LINUX_VERSION}+git${SRCPV}"

KMETA = "meta"

LINUX_KERNEL_TYPE = "preempt-rt"

COMPATIBLE_MACHINE = "(qemux86|qemux86-64|qemuarm|qemuppc|qemumips)"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc features/taskstats/taskstats.scc"
KERNEL_FEATURES_append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES_append_qemux86-64=" cfg/sound.scc"
