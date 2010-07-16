DESCRIPTION = "A tool to make device nodes"
LICENSE = "GPLv2"
SECTION = "base"
PRIORITY = "required"
SRC_URI = "file://makedevs.c"
PR = "r6"

do_configure() {
	install -m 0644 ${WORKDIR}/makedevs.c ${S}/
}

do_compile() {
	${CC} ${CFLAGS} -o ${S}/makedevs ${S}/makedevs.c
}

do_install() {
	install -d ${D}${base_sbindir}
	install -m 0755 ${S}/makedevs ${D}${base_sbindir}/makedevs
}

BBCLASSEXTEND = "native"
NATIVE_INSTALL_WORKS = "1"
