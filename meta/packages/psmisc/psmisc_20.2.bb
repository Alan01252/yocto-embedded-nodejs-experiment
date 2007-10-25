LICENSE = "GPL"
DESCRIPTION = "procfs tools"
SECTION = "base"
PRIORITY = "required"
DEPENDS = "ncurses"
PR = "r4"

SRC_URI = "${SOURCEFORGE_MIRROR}/psmisc/psmisc-${PV}.tar.gz \
	   file://gettext.patch;patch=1"
S = "${WORKDIR}/psmisc-${PV}"

inherit autotools gettext

ALLOW_EMPTY_${PN} = "1"

PACKAGES = "${PN} \
           fuser-dbg fuser fuser-doc \
	   killall-dbg killall killall-doc \
	   pstree-dbg pstree pstree-doc"

FILES_${PN} = ""
RDEPENDS_${PN} = "fuser killall pstree"

FILES_fuser = "${bindir}/fuser"
FILES_fuser-doc = "${mandir}/man1/fuser*"
FILES_fuser-dbg = "${bindir}/.debug/fuser"

FILES_killall = "${bindir}/killall.${PN}"
FILES_killall-doc = "${mandir}/man1/killall*"
FILES_killall-dbg = "${bindir}/.debug/killall*"

FILES_pstree = "${bindir}/pstree"
FILES_pstree-doc = "${mandir}/man1/pstree*"
FILES_pstree-dbg = "${bindir}/.debug/pstree"

do_install_append() {
	mv ${D}${bindir}/killall ${D}${bindir}/killall.${PN}
}

pkg_postinst_killall() {
	update-alternatives --install ${bindir}/killall killall killall.${PN} 90
}

pkg_postrm_killall() {
	update-alternatives --remove ${bindir}/killall killall.${PN}
}
