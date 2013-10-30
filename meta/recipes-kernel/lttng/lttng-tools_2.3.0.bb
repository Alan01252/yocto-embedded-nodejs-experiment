SECTION = "devel"
SUMMARY = "Linux Trace Toolkit Control"
DESCRIPTION = "The Linux trace toolkit is a suite of tools designed \
to extract program execution details from the Linux operating system \
and interpret them."

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01d7fc4496aacf37d90df90b90b0cac1 \
                    file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://lgpl-2.1.txt;md5=0f0d71500e6a57fd24d825f33242b9ca"

DEPENDS = "liburcu popt lttng-ust"
RDEPENDS_${PN}-ptest += "make"

SRCREV = "c9dc1289e040c542f96fbfd558267786816d5703"
PV = "v2.3.0"

SRC_URI = "git://git.lttng.org/lttng-tools.git \
           file://runtest.patch \
           file://run-ptest \
	  "

S = "${WORKDIR}/git"

inherit autotools ptest

export KERNELDIR="${STAGING_KERNEL_DIR}"

FILES_${PN} += "${libdir}/lttng/libexec/*"
FILES_${PN}-dbg += "${libdir}/lttng/libexec/.debug"

# Since files are installed into ${libdir}/lttng/libexec we match 
# the libexec insane test so skip it.
INSANE_SKIP_${PN} = "libexec"
INSANE_SKIP_${PN}-dbg = "libexec"


do_install_ptest () {
	chmod +x ${D}/${libdir}/${PN}/ptest/tests/utils/utils.sh
	for i in `find ${D}/${libdir}/${PN}/ptest -perm /u+x -type f`; do
		sed -e "s:\$TESTDIR.*/src/bin/lttng/\$LTTNG_BIN:\$LTTNG_BIN:" \
		  -e "s:\$TESTDIR/../src/bin/lttng-sessiond/\$SESSIOND_BIN:\$SESSIOND_BIN:" \
		  -e "s:\$DIR/../src/bin/lttng-sessiond/\$SESSIOND_BIN:\$SESSIOND_BIN:" \
		  -e "s:\$TESTDIR/../src/bin/lttng-consumerd/:${libedir}/lttng/libexec/:" \
		  -e "s:\$DIR/../src/bin/lttng-consumerd/:${libdir}/lttng/libexec/:" \
		  -e "s:\$TESTDIR/../src/bin/lttng-relayd/\$RELAYD_BIN:\$RELAYD_BIN:" \
		  -i $i
	done

	sed -e "s:src/bin/lttng-sessiond:$bindir:" \
	    -e "s:src/bin/lttng-consumerd:${libexecdir}/libexec/:" \
	-i ${D}/${libdir}/${PN}/ptest/tests/regression/run-report.py

}
