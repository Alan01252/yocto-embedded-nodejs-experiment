#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "Debugging tools"
LICENSE = "MIT"

inherit packagegroup

PR = "r3"

MTRACE = ""
MTRACE_libc-glibc = "libc-mtrace"

RDEPENDS_${PN} = "\
    gdb \
    gdbserver \
    strace \
    ${MTRACE} \
    "
