DESCRIPTION = "Texinfo is a documentation system that can produce both online \
information and printed output from a single source file. The GNU \
Project uses the Texinfo file format for most of its documentation."

SECTION = "console/utils"
HOMEPAGE = "http://www.gnu.org/software/texinfo/"
PRIORITY = "required"
LICENSE = "GPLV3+"
PR = "r0"

LIC_FILES_CHKSUM = "file://COPYING;md5=adefda309052235aa5d1e99ce7557010"

DEPENDS  = "zlib"

SRC_URI = "http://ftp.gnu.org/gnu/texinfo/texinfo-${PV}.tar.gz \
           file://texinfo-4.12-zlib.patch;striplevel=1 \
           file://texinfo-4.13a-data_types.patch;striplevel=1 \
           file://texinfo-4.13a-mosdo-crash.patch;striplevel=1 \
           file://texinfo-4.13a-powerpc.patch;striplevel=1 \
           file://texinfo-4.13a-help-index-segfault.patch;striplevel=1"

inherit gettext autotools

S = ${WORKDIR}/texinfo-4.13
tex_texinfo = "texmf/tex/texinfo"

do_configure() {
	oe_runconf
}

do_compile_prepend(){
	if [ -d tools ];then
		make -C tools/gnulib/lib
	fi
}

do_install_append() {
	mkdir -p ${D}${datadir}/${tex_texinfo}
	install -p -m644 doc/texinfo.tex doc/txi-??.tex ${D}${datadir}/${tex_texinfo} 	
}

PACKAGES += "info info-doc info-dbg"

FILES_info = "${bindir}/info ${bindir}/infokey ${bindir}/install-info"
FILES_info-doc = "${infodir}/info.info ${infodir}/dir ${infodir}/info-*.info \
                  ${mandir}/man1/info.1* ${mandir}/man5/info.5* \
                  ${mandir}/man1/infokey.1* ${mandir}/man1/install-info.1*"
FILES_info-dbg = "${bindir}/.debug/info ${bindir}/.debug/install-info \
                  ${bindir}/.debug/infokey"

FILES_${PN} = "${bindir}/makeinfo ${bindir}/texi* ${bindir}/pdftexi2dvi"
FILES_${PN}-doc = "${datadir}/texinfo ${infodir}/texinfo* \
                   ${datadir}/${tex_texinfo} \
                   ${mandir}/man1/makeinfo.1* ${mandir}/man5/texinfo.5* \
                   ${mandir}/man1/texindex.1* ${mandir}/man1/texi2dvi.1* \
                   ${mandir}/man1/texi2pdf.1* ${mandir}/man1/pdftexi2dvi.1*"
FILES_${PN}-dbg = "${bindir}/.debug/texindex ${bindir}/.debug/makeinfo"
