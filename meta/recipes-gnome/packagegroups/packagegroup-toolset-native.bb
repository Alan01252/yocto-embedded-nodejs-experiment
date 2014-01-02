#
# Copyright (C) 2012 Wind River Ltd
#

SUMMARY = "A toolset that contains most of the native packages."
DESCRIPTION = "The packagegroup is required to build most native \
tools, it is useful for testing and collecting the native tools."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit native packagegroup

DEPENDS = "\
    acl-native \
    alsa-lib-native \
    apr-native \
    apr-util-native \
    apt-native \
    atk-native \
    attr-native \
    autoconf-native \
    autogen-native \
    automake-native \
    bdwgc-native \
    beecrypt-native \
    bigreqsproto-native \
    binutils-native \
    bison-native \
    boost-native \
    btrfs-tools-native \
    byacc-native \
    bzip2-native \
    cairo-native \
    ccache-native \
    cdrtools-native \
    chrpath-native \
    cmake-native \
    compositeproto-native \
    coreutils-native \
    createrepo-native \
    cross-localedef-native \
    curl-native \
    cwautomacros-native \
    damageproto-native \
    db-native \
    dbus-glib-native \
    dbus-native \
    desktop-file-utils-native \
    docbook-dsssl-stylesheets-native \
    docbook-sgml-dtd-3.1-native \
    docbook-sgml-dtd-4.1-native \
    docbook-sgml-dtd-4.5-native \
    docbook-utils-native \
    dosfstools-native \
    dpkg-native \
    dtc-native \
    e2fsprogs-native \
    elfutils-native \
    expat-native \
    file-native \
    findutils-native \
    fixesproto-native \
    flex-native \
    fontconfig-native \
    fontsproto-native \
    font-util-native \
    freetype-native \
    gconf-native \
    gdbm-native \
    gdk-pixbuf-native \
    genext2fs-native \
    gettext-minimal-native \
    gettext-native \
    ghostscript-native \
    git-native \
    glib-2.0-native \
    gmp-native \
    gnome-common-native \
    gnome-doc-utils-native \
    gnu-config-native \
    gnutls-native \
    gperf-native \
    groff-native \
    gtk-doc-stub-native \
    gtk-update-icon-cache-native \
    guile-native \
    guilt-native \
    gzip-native \
    help2man-native \
    icecc-create-env-native \
    icon-naming-utils-native \
    icu-native \
    inputproto-native \
    insserv-native \
    intltool-native \
    jpeg-native \
    kbd-native \
    kbproto-native \
    kconfig-frontends-native \
    kern-tools-native \
    kmod-native \
    ldconfig-native \
    libcap-native \
    libclass-isa-perl-native \
    libconvert-asn1-perl-native \
    libffi-native \
    libfontenc-native \
    libgcrypt-native \
    libgpg-error-native \
    libice-native \
    libmpc-native \
    libpcre-native \
    libpng-native \
    libpod-plainer-perl-native \
    libpthread-stubs-native \
    librsvg-native \
    libsm-native \
    libtasn1-native \
    libtimedate-perl-native \
    libtool-native \
    libunistring-native \
    libusb1-native \
    libusb-compat-native \
    libx11-native \
    libxau-native \
    libxcb-native \
    libxcomposite-native \
    libxcursor-native \
    libxdamage-native \
    libxdmcp-native \
    libxext-native \
    libxfixes-native \
    libxfont-native \
    libxft-native \
    libxkbfile-native \
    libxml2-native \
    libxml-namespacesupport-perl-native \
    libxml-parser-perl-native \
    libxml-sax-perl-native \
    libxml-simple-perl-native \
    libxmu-native \
    libxpm-native \
    libxrandr-native \
    libxrender-native \
    libxslt-native \
    libxt-native \
    linuxdoc-tools-native \
    lzo-native \
    lzop-native \
    m4-native \
    makedepend-native \
    makedevs-native \
    make-native \
    mkelfimage-native \
    mkfontdir-native \
    mkfontscale-native \
    mklibs-native \
    mpfr-native \
    mtd-utils-native \
    mtools-native \
    nasm-native \
    ncurses-native \
    neon-native \
    ocf-linux-native \
    openjade-native \
    opensp-native \
    openssl-native \
    opkg-native \
    opkg-utils-native \
    ossp-uuid-native \
    pango-native \
    parted-native \
    pax-utils-native \
    perl-native \
    pigz-native \
    pixman-native \
    pkgconfig-native \
    popt-native \
    prelink-native \
    pseudo-native \
    python-argparse-native \
    python-native \
    python-pycurl-native \
    python-pygobject-native \
    python-pyrex-native \
    python-scons-native \
    python-setuptools-native \
    python-smartpm-native \
    qemu-helper-native \
    qemu-native \
    qt4-native \
    quilt-native \
    randrproto-native \
    readline-native \
    renderproto-native \
    rpm-native \
    rpmresolve-native \
    sed-native \
    sgml-common-native \
    sgmlspl-native \
    shared-mime-info-native \
    sqlite3-native \
    squashfs-tools-native \
    strace-native \
    subversion-native \
    swabber-native \
    syslinux-native \
    systemtap-native \
    tar-replacement-native \
    tcl-native \
    texinfo-native \
    tiff-native \
    tzcode-native \
    u-boot-mkimage-native \
    unfs-server-native \
    unifdef-native \
    unzip-native \
    update-rc.d-native \
    util-linux-native \
    util-macros-native \
    xcb-proto-native \
    xcmiscproto-native \
    xextproto-native \
    xkbcomp-native \
    xproto-native \
    xtrans-native \
    xz-native \
    zip-native \
    zlib-native \
"
