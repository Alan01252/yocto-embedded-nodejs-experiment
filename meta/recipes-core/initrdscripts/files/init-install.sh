#!/bin/sh -e
#
# Copyright (C) 2008-2011 Intel
#
# install.sh [device_name] [rootfs_name] [video_mode] [vga_mode]
#

PATH=/sbin:/bin:/usr/sbin:/usr/bin

# We need 20 Mb for the boot partition
boot_size=20

# 5% for the swap
swap_ratio=5

# Get a list of hard drives
hdnamelist=""
live_dev_name=`cat /proc/mounts | grep ${1%/} | awk '{print $1}'`
live_dev_name=${live_dev_name#\/dev/}
live_dev_name=${live_dev_name%%[0-9]*}

echo "Searching for hard drives ..."

for device in `ls /sys/block/`; do
    case $device in
	loop*)
            # skip loop device
	    ;;
	sr*)
            # skip CDROM device
	    ;;
	ram*)
            # skip ram device
	    ;;
	*)
	    # skip the device LiveOS is on
	    # Add valid hard drive name to the list
	    if [ $device != $live_dev_name -a -e /dev/$device ]; then
		hdnamelist="$hdnamelist $device"
	    fi
	    ;;
    esac
done

TARGET_DEVICE_NAME=""
for hdname in $hdnamelist; do
    # Display found hard drives and their basic info
    echo "-------------------------------"
    echo /dev/$hdname
    if [ -r /sys/block/$hdname/device/vendor ]; then
	echo -n "VENDOR="
	cat /sys/block/$hdname/device/vendor
    fi
    if [ -r /sys/block/$hdname/device/model ]; then
        echo -n "MODEL="
        cat /sys/block/$hdname/device/model
    fi
    if [ -r /sys/block/$hdname/device/uevent ]; then
        echo -n "UEVENT="
        cat /sys/block/$hdname/device/uevent
    fi
    echo
    # Get user choice
    while true; do
	echo -n "Do you want to install this image there? [y/n] "
	read answer
	if [ "$answer" = "y" -o "$answer" = "n" ]; then
	    break
	fi
	echo "Please answer y or n"
    done
    if [ "$answer" = "y" ]; then
	TARGET_DEVICE_NAME=$hdname
	break
    fi
done

if [ -n "$TARGET_DEVICE_NAME" ]; then
    echo "Installing image on /dev/$TARGET_DEVICE_NAME ..."
else
    echo "No hard drive selected. Installation aborted."
    exit 1
fi

device=$TARGET_DEVICE_NAME

#
# The udev automounter can cause pain here, kill it
#
rm -f /etc/udev/rules.d/automount.rules
rm -f /etc/udev/scripts/mount*

#
# Unmount anything the automounter had mounted
#
umount /dev/${device}* 2> /dev/null || /bin/true

if [ ! -b /dev/loop0 ] ; then
    mknod /dev/loop0 b 7 0
fi

mkdir -p /tmp
if [ ! -L /etc/mtab ]; then
	cat /proc/mounts > /etc/mtab
fi

disk_size=$(parted /dev/${device} unit mb print | grep Disk | cut -d" " -f 3 | sed -e "s/MB//")

grub_version=$(grub-install -v|sed 's/.* \([0-9]\).*/\1/')

if [ $grub_version -eq 0 ] ; then
    bios_boot_size=0
else
    # For GRUB 2 we need separate parition to store stage2 grub image
    # 2Mb value is chosen to align partition for best performance.
    bios_boot_size=2
fi

swap_size=$((disk_size*swap_ratio/100))
rootfs_size=$((disk_size-bios_boot_size-boot_size-swap_size))

boot_start=$((bios_boot_size))
rootfs_start=$((bios_boot_size+boot_size))
rootfs_end=$((rootfs_start+rootfs_size))
swap_start=$((rootfs_end))

# MMC devices are special in a couple of ways
# 1) they use a partition prefix character 'p'
# 2) they are detected asynchronously (need rootwait)
rootwait=""
part_prefix=""
if [ ! "${device#mmcblk}" = "${device}" ]; then
	part_prefix="p"
	rootwait="rootwait"
fi

if [ $grub_version -eq 0 ] ; then
    bios_boot=''
    bootfs=/dev/${device}${part_prefix}1
    rootfs=/dev/${device}${part_prefix}2
    swap=/dev/${device}${part_prefix}3
else
    bios_boot=/dev/${device}${part_prefix}1
    bootfs=/dev/${device}${part_prefix}2
    rootfs=/dev/${device}${part_prefix}3
    swap=/dev/${device}${part_prefix}4
fi

echo "*****************"
[ $grub_version -ne 0 ] && echo "BIOS boot partition size: $bios_boot_size MB ($bios_boot)"
echo "Boot partition size:   $boot_size MB ($bootfs)"
echo "Rootfs partition size: $rootfs_size MB ($rootfs)"
echo "Swap partition size:   $swap_size MB ($swap)"
echo "*****************"
echo "Deleting partition table on /dev/${device} ..."
dd if=/dev/zero of=/dev/${device} bs=512 count=2

echo "Creating new partition table on /dev/${device} ..."
if [ $grub_version -eq 0 ] ; then
    parted /dev/${device} mktable msdos
    echo "Creating boot partition on $bootfs"
    parted /dev/${device} mkpart primary ext3 0% $boot_size
else
    parted /dev/${device} mktable gpt
    echo "Creating BIOS boot partition on $bios_boot"
    parted /dev/${device} mkpart bios_boot 0% $bios_boot_size
    parted /dev/${device} set 1 bios_grub on
    echo "Creating boot partition on $bootfs"
    parted /dev/${device} mkpart boot ext3 $boot_start $boot_size
fi

echo "Creating rootfs partition on $rootfs"
[ $grub_version -eq 0 ] && pname='primary' || pname='root'
parted /dev/${device} mkpart $pname ext3 $rootfs_start $rootfs_end

echo "Creating swap partition on $swap"
[ $grub_version -eq 0 ] && pname='primary' || pname='swap'
parted /dev/${device} mkpart $pname linux-swap $swap_start 100%

parted /dev/${device} print

echo "Formatting $bootfs to ext3..."
mkfs.ext3 $bootfs

echo "Formatting $rootfs to ext3..."
mkfs.ext3 $rootfs

echo "Formatting swap partition...($swap)"
mkswap $swap

mkdir /tgt_root
mkdir /src_root
mkdir -p /boot

# Handling of the target root partition
mount $rootfs /tgt_root
mount -o rw,loop,noatime,nodiratime /run/media/$1/$2 /src_root
echo "Copying rootfs files..."
cp -a /src_root/* /tgt_root
if [ -d /tgt_root/etc/ ] ; then
    if [ $grub_version -ne 0 ] ; then
        boot_uuid=$(blkid -o value -s UUID ${device}2)
        swap_part_uuid=$(blkid -o value -s PARTUUID ${device}4)
        bootdev="UUID=$boot_uuid"
        swapdev=/dev/disk/by-partuuid/$swap_part_uuid
    else
        bootdev=${device}2
        swapdev=${device}4
    fi
    echo "$swapdev                swap             swap       defaults              0  0" >> /tgt_root/etc/fstab
    echo "$bootdev              /boot            ext3       defaults              1  2" >> /tgt_root/etc/fstab
    # We dont want udev to mount our root device while we're booting...
    if [ -d /tgt_root/etc/udev/ ] ; then
	echo "/dev/${device}" >> /tgt_root/etc/udev/mount.blacklist
    fi
fi
umount /tgt_root
umount /src_root

# Handling of the target boot partition
mount $bootfs /boot
echo "Preparing boot partition..."
if [ -f /etc/grub.d/00_header -a $grub_version -ne 0 ] ; then
    echo "Preparing custom grub2 menu..."
    root_part_uuid=$(blkid -o value -s PARTUUID /dev/${device}3)
    boot_uuid=$(blkid -o value -s UUID /dev/${device}2)
    GRUBCFG="/boot/grub/grub.cfg"
    mkdir -p $(dirname $GRUBCFG)
    cat >$GRUBCFG <<_EOF
menuentry "Linux" {
    search --no-floppy --fs-uuid $boot_uuid --set root
    linux /vmlinuz root=PARTUUID=$root_part_uuid $rootwait rw $5 $3 $4 quiet
}
_EOF
    chmod 0444 $GRUBCFG
fi
grub-install /dev/${device}

if [ $grub_version -eq 0 ] ; then
    echo "(hd0) /dev/${device}" > /boot/grub/device.map
    echo "Preparing custom grub menu..."
    echo "default 0" > /boot/grub/menu.lst
    echo "timeout 30" >> /boot/grub/menu.lst
    echo "title Live Boot/Install-Image" >> /boot/grub/menu.lst
    echo "root  (hd0,0)" >> /boot/grub/menu.lst
    echo "kernel /vmlinuz root=$rootfs rw $3 $4 quiet" >> /boot/grub/menu.lst
fi

cp /run/media/$1/vmlinuz /boot/

umount /boot

sync

echo "Remove your installation media, and press ENTER"

read enter

echo "Rebooting..."
reboot -f
