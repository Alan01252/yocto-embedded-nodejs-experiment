DESCRIPTION = "X11 keyboard file manipulation library"

require xorg-lib-common.inc

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=aa32e1b87f1da02948ab7b55e129bf5f"

DEPENDS += "virtual/libx11 kbproto"

PR = "r0"
PE = "1"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "59b4fe0bdf8d9b05e45b59e8fe9e7516"
SRC_URI[sha256sum] = "667e370a733b96b647a40211430cfc41dd2160c9a2aa701d0c839c626d0f2ae5"
