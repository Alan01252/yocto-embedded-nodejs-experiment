require xserver-xf86-lite.inc

PR = "r4"

SRC_URI += "file://drmfix.patch;patch=1 \
            file://libdri-xinerama-symbol.patch;patch=1 "

