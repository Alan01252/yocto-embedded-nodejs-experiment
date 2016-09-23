DEPENDS += "python-scons-native"

EXTRA_OESCONS ?= ""

do_configure[noexec] = "1"

scons_do_compile() {
        ${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} PREFIX=${prefix} prefix=${prefix} ${EXTRA_OESCONS} || \
        die "scons build execution failed."
}

scons_do_install() {
        ${STAGING_BINDIR_NATIVE}/scons install_root=${D}${prefix} PREFIX=${prefix} prefix=${prefix} ${EXTRA_OESCONS} install || \
        die "scons install execution failed."
}

EXPORT_FUNCTIONS do_compile do_install
