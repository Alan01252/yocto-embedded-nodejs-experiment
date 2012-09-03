SUMMARY = "Provides a small set of tools for development on the device"
LICENSE = "MIT"

inherit packagegroup

RPROVIDES_${PN} = "qemu-config"
RREPLACES_${PN} = "qemu-config"

RDEPENDS_${PN} = "\
    distcc-config \
    oprofileui-server \
    nfs-export-root \
    bash \
    "

