require qemu_git.bb
require qemu-sdk.inc

PR = "r2"

EXTRA_OECONF +="--target-list=arm-linux-user,arm-softmmu,i386-softmmu --disable-vnc-tls"
