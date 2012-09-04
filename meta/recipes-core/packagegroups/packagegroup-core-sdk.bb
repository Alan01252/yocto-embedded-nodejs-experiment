#
# Copyright (C) 2007 OpenedHand Ltd.
#

SUMMARY = "Software development tools"
LICENSE = "MIT"
PR = "r9"

inherit packagegroup

#PACKAGEFUNCS =+ 'generate_sdk_pkgs'

# For backwards compatibility after rename
RPROVIDES_packagegroup-core-sdk = "task-core-sdk"
RREPLACES_packagegroup-core-sdk = "task-core-sdk"
RCONFLICTS_packagegroup-core-sdk = "task-core-sdk"

RDEPENDS_packagegroup-core-sdk = "\
    autoconf \
    automake \
    binutils \
    binutils-symlinks \
    coreutils \
    cpp \
    cpp-symlinks \
    ccache \
    diffutils \
    gcc \
    gcc-symlinks \
    g++ \
    g++-symlinks \
    gettext \
    make \
    intltool \
    libstdc++ \
    libstdc++-dev \
    libtool \
    perl-module-re \
    perl-module-text-wrap \
    pkgconfig \
    findutils \
    quilt \
    less \
    distcc \
    ldd \
    file \
    tcl"

RRECOMMENDS_packagegroup-core-sdk = "\
    libgomp \
    libgomp-dev"

#python generate_sdk_pkgs () {
#    poky_pkgs = read_pkgdata('packagegroup-core', d)['PACKAGES']
#    pkgs = d.getVar('PACKAGES', True).split()
#    for pkg in poky_pkgs.split():
#        newpkg = pkg.replace('packagegroup-core', 'packagegroup-core-sdk')
#
#        # for each of the task packages, add a corresponding sdk task
#        pkgs.append(newpkg)
#
#        # for each sdk task, take the rdepends of the non-sdk task, and turn
#        # that into rrecommends upon the -dev versions of those, not unlike
#        # the package depchain code
#        spkgdata = read_subpkgdata(pkg, d)
#
#        rdepends = explode_deps(spkgdata.get('RDEPENDS_%s' % pkg) or '')
#        rreclist = []
#
#        for depend in rdepends:
#            split_depend = depend.split(' (')
#            name = split_depend[0].strip()
#            if packaged('%s-dev' % name, d):
#                rreclist.append('%s-dev' % name)
#            else:
#                deppkgdata = read_subpkgdata(name, d)
#                rdepends2 = explode_deps(deppkgdata.get('RDEPENDS_%s' % name) or '')
#                for depend in rdepends2:
#                    split_depend = depend.split(' (')
#                    name = split_depend[0].strip()
#                    if packaged('%s-dev' % name, d):
#                        rreclist.append('%s-dev' % name)
#
#            oldrrec = d.getVar('RRECOMMENDS_%s' % newpkg) or ''
#            d.setVar('RRECOMMENDS_%s' % newpkg, oldrrec + ' ' + ' '.join(rreclist))
#            # bb.note('RRECOMMENDS_%s = "%s"' % (newpkg, d.getVar('RRECOMMENDS_%s' % newpkg)))
#
#    # bb.note('pkgs is %s' % pkgs)
#    d.setVar('PACKAGES', ' '.join(pkgs))
#}
#
#PACKAGES_DYNAMIC = "packagegroup-core-sdk-*"
