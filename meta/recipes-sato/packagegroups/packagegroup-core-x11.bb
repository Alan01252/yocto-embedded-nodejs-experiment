#
# Copyright (C) 2011 Intel Corporation
#

DESCRIPTION = "Tasks for core X11 applications"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=3f40d7994397109285ec7b81fdeb3b58 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
PR = "r36"

inherit packagegroup

PACKAGES = "\
    packagegroup-core-apps-x11-core \
    packagegroup-core-apps-x11-games \
    packagegroup-core-x11-base \
    "

PACKAGE_ARCH = "${MACHINE_ARCH}"

XSERVER ?= "xserver-xorg xf86-video-fbdev xf86-input-evdev"

# pcmanfm doesn't work on mips/powerpc
FILEMANAGER ?= "pcmanfm"
FILEMANAGER_mips ?= ""

# xserver-common, x11-common
VIRTUAL-RUNTIME_xserver_common ?= "x11-common"

# elsa, xserver-nodm-init
VIRTUAL-RUNTIME_graphical_init_manager ?= "xserver-nodm-init"


RDEPENDS_packagegroup-core-x11-base = "\
    dbus \
    pointercal \
    matchbox-wm \
    matchbox-keyboard \
    matchbox-keyboard-applet \
    matchbox-keyboard-im \
    matchbox-panel-2 \
    matchbox-desktop \
    matchbox-session \
    ${XSERVER} \
    ${VIRTUAL-RUNTIME_xserver_common} \
    ${VIRTUAL-RUNTIME_graphical_init_manager} \
    liberation-fonts \
    xauth \
    xhost \
    xset \
    xrandr"


RDEPENDS_packagegroup-core-apps-x11-core = "\
    leafpad \
    ${FILEMANAGER} \
    matchbox-terminal \
    sato-screenshot"


RDEPENDS_packagegroup-core-apps-x11-games = "\
    oh-puzzles"
