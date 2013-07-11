SECTION = "x11/utils"
DESCRIPTION = "rxvt-unicode is a clone of the well known \
terminal emulator rxvt, modified to store text in Unicode \
(either UCS-2 or UCS-4) and to use locale-correct input and \
output. It also supports mixing multiple fonts at the \
same time, including Xft fonts."
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/main.C;beginline=1;endline=31;md5=775485398a09fa7aee6f90464af88432"

DEPENDS = "virtual/libx11 libxt libxft gdk-pixbuf"

PR = "r0"

SRC_URI = "http://dist.schmorp.de/rxvt-unicode/Attic/rxvt-unicode-${PV}.tar.bz2 \
	   file://xwc.patch \
	   file://rxvt.desktop \
	   file://rxvt.png"

SRC_URI[md5sum] = "963ccc748fe5bca925e7b92c0404d68b"
SRC_URI[sha256sum] = "87b72a43cac7c9362a984094c661a632ce1a829ad90f149142ab1953854b433f"

inherit autotools update-alternatives

PROVIDES = "virtual/x-terminal-emulator"
ALTERNATIVE_${PN} = "x-terminal-emulator"
ALTERNATIVE_TARGET[x-terminal-emulator] = "${bindir}/rxvt"

CFLAGS_append = " -fpermissive"

EXTRA_OECONF = "--enable-menubar --enable-xim \
		--enable-utmp --enable-wtmp --enable-lastlog \
		--disable-strings --with-term=rxvt --enable-keepscrolling \
		--enable-xft --with-name=rxvt --enable-frills \
		--enable-swapscreen --enable-transparency \
		--with-codesets=eu \
		--enable-cursor-blink --enable-pointer-blank \
		--enable-text-blink --enable-rxvt-scroll \
		--enable-combining --enable-shared \
		--enable-xgetdefault \
                --disable-perl \
		--with-x=${STAGING_DIR_HOST}${prefix}"

do_configure_prepend () {
	if [ ! -e ${S}/acinclude.m4 ]; then
		cp ${S}/aclocal.m4 ${S}/acinclude.m4
	fi
}

do_compile_prepend () {
	echo '#define UTMP_FILE "${localstatedir}/run/utmp"' >> config.h
	echo '#define WTMP_FILE "${localstatedir}/log/wtmp"' >> config.h
	echo '#define LASTLOG_FILE "${localstatedir}/log/lastlog"' >> config.h
	echo '#define HAVE_XLOCALE 1' >> config.h
}

do_install_append () {
	install -d ${D}/${datadir}
	install -d ${D}/${datadir}/applications
	install -d ${D}/${datadir}/pixmaps/

	install -m 0644 ${WORKDIR}/rxvt.png ${D}/${datadir}/pixmaps
	install -m 0644 ${WORKDIR}/rxvt.desktop ${D}/${datadir}/applications
}

FILES_${PN} += "${datadir}/applications/rxvt.desktop ${datadir}/pixmaps/rxvt.png"
