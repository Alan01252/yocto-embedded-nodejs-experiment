#
# Copyright (C) 2008 OpenedHand Ltd.
#

DESCRIPTION = "Tools tasks for Poky"
LICENSE = "MIT"
PR = "r3"

PACKAGES = "\
    task-poky-tools-debug \
    task-poky-tools-debug-dbg \
    task-poky-tools-debug-dev \
    task-poky-tools-profile \
    task-poky-tools-profile-dbg \
    task-poky-tools-profile-dev \
    task-poky-tools-testapps \
    task-poky-tools-testapps-dbg \
    task-poky-tools-testapps-dev \
    "

PACKAGE_ARCH = "${MACHINE_ARCH}"

ALLOW_EMPTY = "1"

# kexec-tools doesn't work on Mips
KEXECTOOLS ?= "kexec-tools"
KEXECTOOLS_mips ?= ""
KEXECTOOLS_mipsel ?= ""
KEXECTOOLS_powerpc ?= ""

RDEPENDS_task-poky-tools-debug = "\
    gdb \
    gdbserver \
    strace"

RDEPENDS_task-poky-tools-profile = "\
    oprofile \
    oprofileui-server \
    powertop \
    lttng-control \
    lttng-viewer"

#    exmap-console
#    exmap-server


RDEPENDS_append_task-poky-tools-profile_qemux86 = "valgrind"

RRECOMMENDS_task-poky-tools-profile = "\
    kernel-module-oprofile"

RDEPENDS_task-poky-tools-testapps = "\
    blktool \
    tslib-calibrate \
    tslib-tests \
    lrzsz \
    ${KEXECTOOLS} \
    alsa-utils-amixer \
    alsa-utils-aplay \
    owl-video \
    gst-meta-video \
    gst-meta-audio \
    xrestop \
    xwininfo \
    xprop \
    xvideo-tests"
