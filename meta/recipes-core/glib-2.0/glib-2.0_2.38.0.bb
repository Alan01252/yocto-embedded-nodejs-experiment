require glib.inc

PE = "1"

DEPENDS += "libffi zlib"
DEPENDS_class-native += "libffi-native"
DEPENDS_class-nativesdk += "nativesdk-libffi nativesdk-zlib ${BPN}-native"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/glib/${SHRT_VER}/glib-${PV}.tar.xz \
           file://configure-libtool.patch \
           file://fix-conflicting-rand.patch \
           file://add-march-i486-into-CFLAGS-automatically.patch \
           file://glib-2.0-configure-readlink.patch \
           file://run-ptest \
           file://0001-gio-Fix-Werror-format-string-errors-from-mismatched-.patch \
          "

SRC_URI_append_class-native = " file://glib-gettextize-dir.patch"

SRC_URI[md5sum] = "c50d2805a76763e9b4cc4385d4ea215d"
SRC_URI[sha256sum] = "7513a7de5e814ccb48206340a8773ea523d6a7bf04dc74565de69b899bc2ff32"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN}-ptest += "\
            gnome-desktop-testing \
            tzdata \
            tzdata-americas \
            tzdata-asia \
            tzdata-europe \
            tzdata-posix \
            python-pygobject \
            python-dbus \
           "

RDEPENDS_${PN}-ptest_append_libc-glibc = "\
            eglibc-gconv-utf-16 \
            eglibc-charmap-utf-8 \
            eglibc-gconv-cp1255 \
            eglibc-charmap-cp1255 \
            eglibc-gconv-utf-32 \
            eglibc-gconv-utf-7 \
            eglibc-charmap-invariant \
            eglibc-localedata-translit-cjk-variants \
           "
EXTRA_OECONF_append_class-target_libc-uclibc = " --with-libiconv=gnu"

do_configure_prepend() {
	sed -i -e '1s,#!.*,#!${USRBINPATH}/env python,' ${S}/gio/gdbus-2.0/codegen/gdbus-codegen.in
}

do_install_append() {
  # remove some unpackaged files
  rm -f ${D}${datadir}/glib-2.0/codegen/*.pyc
  rm -f ${D}${datadir}/glib-2.0/codegen/*.pyo
  # and empty dirs
  rm -rf ${D}${libdir}/gio

  # Some distros have both /bin/perl and /usr/bin/perl, but we set perl location
  # for target as /usr/bin/perl, so fix it to /usr/bin/perl.
  if [ -f ${D}${bindir}/glib-mkenums ]; then
    sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/glib-mkenums
  fi
}
