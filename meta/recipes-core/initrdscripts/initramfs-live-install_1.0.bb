DESCRIPTION = "A live image init script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SRC_URI = "file://init-install.sh"

PR = "r4"

RDEPENDS="grub parted e2fsprogs-mke2fs"

do_install() {
        install -m 0755 ${WORKDIR}/init-install.sh ${D}/install.sh
}

inherit allarch

FILES_${PN} = " /install.sh "

# Alternatives to grub need adding for other arch support
COMPATIBLE_HOST = "(i.86|x86_64).*-linux"
