SECTION = "devel"
DESCRIPTION = "The Linux trace toolkit is a suite of tools designed to \
extract program execution details from the Linux operating system and  \
interpret them."
LICENSE = "GPL"
MAINTAINER = "Richard Purdie <richard@openedhand.com>"

SRC_URI = "http://ltt.polymtl.ca/lttng/ltt-control-${PV}-12062006.tar.gz \
           file://lttctl_sti-r0.patch;patch=1 "

S = "${WORKDIR}/ltt-control-${PV}-12062006"

inherit autotools

export KERNELDIR="${STAGING_KERNEL_DIR}"

FILES_${PN} += "${datadir}/ltt-control/facilities/*"	    