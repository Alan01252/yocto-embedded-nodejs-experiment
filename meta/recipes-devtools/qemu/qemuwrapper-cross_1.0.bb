DESCRIPTION = "Qemu wrapper script"
LICENSE = "MIT"
PR = "r0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit qemu

do_install () {
    install -d ${STAGING_BINDIR_CROSS}

    echo "#!/bin/sh" > ${STAGING_BINDIR_CROSS}/qemuwrapper
    echo exec env ${@qemu_target_binary(d)} \"\$@\" >> ${STAGING_BINDIR_CROSS}/qemuwrapper
    chmod +x ${STAGING_BINDIR_CROSS}/qemuwrapper
}
