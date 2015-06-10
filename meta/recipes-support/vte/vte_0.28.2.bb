require vte.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

PR = "r6"

SRC_URI += "file://obsolete_automake_macros.patch"

CFLAGS += "-D_GNU_SOURCE"

SRC_URI[archive.md5sum] = "497f26e457308649e6ece32b3bb142ff"
SRC_URI[archive.sha256sum] = "86cf0b81aa023fa93ed415653d51c96767f20b2d7334c893caba71e42654b0ae"
