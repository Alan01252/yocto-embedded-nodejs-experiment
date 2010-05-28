require qemu.inc

PV = "0.12.0+git${SRCREV}"
PR = "r6"

FILESPATH = "${FILE_DIRNAME}/qemu-${PV}/:${FILE_DIRNAME}/qemu-git/:${FILE_DIRNAME}/qemu-0.12/"

SRC_URI = "\
    git://git.sv.gnu.org/qemu.git;protocol=git \
    file://workaround_bad_futex_headers.patch;patch=1 \
    file://qemu-git-qemugl-host.patch;patch=1 \
    file://no-strip.patch;patch=1 \
    file://fix-dirent.patch;patch=1 \
    file://fix-nogl.patch;patch=1 \
    file://qemugl-allow-glxcontext-release.patch;patch=1"

S = "${WORKDIR}/git"

