require qemu_svn.bb
require qemu-sdk.inc

EXTRA_OECONF +="--target-list=arm-linux-user,arm-softmmu,i386-linux-user,i386-softmmu --disable-vnc-tls"

DEPENDS += "gcc3-native"

require qemu-gcc3-check.inc
