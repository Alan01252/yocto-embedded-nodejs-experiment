SECTION = "console/utils"
inherit native
require zip_${PV}.bb
S = "${WORKDIR}/zip-${PV}"

do_stage() {
	install -d ${STAGING_BINDIR}
	install zip zipnote zipsplit zipcloak ${STAGING_BINDIR}
}
