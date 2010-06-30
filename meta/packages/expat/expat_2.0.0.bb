require expat.inc
PR = "r3"

SRC_URI += "file://autotools.patch;"

inherit lib_package

do_configure_prepend () {
	rm -f ${S}/conftools/libtool.m4
	touch ${S}/conftools/libtool.m4
}

BBCLASSEXTEND = "native nativesdk"
