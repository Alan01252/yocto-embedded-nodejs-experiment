# This file is for getting archiving packages with patched
# sources(archive ${S} before do_patch stage), logs(archive 'temp' after
# package_write_rpm), dump data and creating diff file(get all
# environment variables and functions in building and mapping all
# content in ${S} including patches to xxx.diff.gz. All archived
# packages will be deployed in ${DEPLOY_DIR}/sources

inherit archiver

# Get archiving package with patched sources including patches
addtask do_archive_patched_sources after do_patch

# Get archiving package with logs(temp) and scripts(.bb and .inc files)
addtask do_archive_scripts_logs

# Get dump date and create diff file 
addtask do_dumpdata_create_diff_gz before do_build

python () {
    pn = d.getVar('PN', True)
    packaging = d.getVar('IMAGE_PKGTYPE', True)
    d.appendVarFlag('do_dumpdata_create_diff_gz', 'depends', ' %s:do_package_write_' %pn + packaging)

    if d.getVar('SOURCE_ARCHIVE_PACKAGE_TYPE', True) != 'srpm':
        """
        If package type is not 'srpm' then add tasks to move archive packages of
        patched sources and scripts/logs in ${DEPLOY_DIR}/sources.
        """
        d.appendVarFlag('do_configure', 'depends', ' %s:do_archive_patched_sources' %pn)
        build_deps = ' %s:do_archive_patched_sources' %pn
        if d.getVar('SOURCE_ARCHIVE_LOG_WITH_SCRIPTS', True) == 'logs_with_scripts':
            build_deps += ' %s:do_archive_scripts_logs' %pn
            d.appendVarFlag('do_archive_scripts_logs', 'depends', ' %s:do_package_write_' %pn + packaging)
        d.appendVarFlag('do_build', 'depends', build_deps)

    else:
        d.prependVarFlag('do_patch', 'postfuncs', "do_archive_patched_sources")
        d.prependVarFlag('do_package_write_rpm', 'prefuncs', "do_archive_scripts_logs")
}

ARCHIVE_SSTATE_OUTDIR = "${DEPLOY_DIR}/sources/"
ARCHIVE_SSTATE_SCRIPTS_LOGS_INDIR = "${WORKDIR}/script-logs/"
ARCHIVE_SSTATE_DIFFGZ_ENVDATA_INDIR = "${WORKDIR}/diffgz-envdata/"

SSTATETASKS += "do_archive_scripts_logs"
do_archive_scripts_logs[sstate-name] = "archive_scripts_logs"
do_archive_scripts_logs[sstate-inputdirs] = "${ARCHIVE_SSTATE_SCRIPTS_LOGS_INDIR}"
do_archive_scripts_logs[sstate-outputdirs] = "${ARCHIVE_SSTATE_OUTDIR}"

python do_archive_scripts_logs_setscene () {
    sstate_setscene(d)
}

addtask do_archive_scripts_logs_setscene

SSTATETASKS += "do_dumpdata_create_diff_gz"
do_dumpdata_create_diff_gz[sstate-name] = "dumpdata_create_diff_gz"
do_dumpdata_create_diff_gz[sstate-inputdirs] = "${ARCHIVE_SSTATE_DIFFGZ_ENVDATA_INDIR}"
do_dumpdata_create_diff_gz[sstate-outputdirs] = "${ARCHIVE_SSTATE_OUTDIR}"

python do_dumpdata_create_diff_gz_setscene () {
    sstate_setscene(d)
}

addtask do_dumpdata_create_diff_gz_setscene
