
require conf/distro/include/distro_tracking_fields.inc

addhandler distro_eventhandler
python distro_eventhandler() {
    from bb.event import Handled, NotHandled
    # if bb.event.getName(e) == "TaskStarted":

    if bb.event.getName(e) == "BuildStarted":
	"""initialize log files."""
	logpath = bb.data.getVar('LOG_DIR', e.data, 1)
	bb.utils.mkdirhier(logpath)
	logfile = os.path.join(logpath, "distrodata.%s.csv" % bb.data.getVar('DATETIME', e.data, 1))
	if not os.path.exists(logfile):
		slogfile = os.path.join(logpath, "distrodata.csv")
		if os.path.exists(slogfile):
			os.remove(slogfile)
		os.system("touch %s" % logfile)
		os.symlink(logfile, slogfile)
		bb.data.setVar('LOG_FILE', logfile, e.data)

	lf = bb.utils.lockfile(logfile + ".lock")
	f = open(logfile, "a")
	f.write("Package,Description,Maintainer,License,ChkSum,Status,VerMatch,Version,Upsteam,Non-Update,Reason,Recipe Status\n")
        f.close()
        bb.utils.unlockfile(lf)

    return NotHandled
}

addtask distrodata_np
do_distrodata_np[nostamp] = "1"
python do_distrodata_np() {
	localdata = bb.data.createCopy(d)
        pn = bb.data.getVar("PN", d, True)
        bb.note("Package Name: %s" % pn)

        import oe.distro_check as dist_check
        tmpdir = bb.data.getVar('TMPDIR', d, 1)
        distro_check_dir = os.path.join(tmpdir, "distro_check")
        datetime = bb.data.getVar('DATETIME', localdata, 1)
        dist_check.update_distro_data(distro_check_dir, datetime)

	if pn.find("-native") != -1:
	    pnstripped = pn.split("-native")
	    bb.note("Native Split: %s" % pnstripped)
	    bb.data.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + bb.data.getVar('OVERRIDES', d, True), localdata)
	    bb.data.update_data(localdata)

	if pn.find("-cross") != -1:
	    pnstripped = pn.split("-cross")
	    bb.note("cross Split: %s" % pnstripped)
	    bb.data.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + bb.data.getVar('OVERRIDES', d, True), localdata)
	    bb.data.update_data(localdata)

	if pn.find("-initial") != -1:
	    pnstripped = pn.split("-initial")
	    bb.note("initial Split: %s" % pnstripped)
	    bb.data.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + bb.data.getVar('OVERRIDES', d, True), localdata)
	    bb.data.update_data(localdata)

	"""generate package information from .bb file"""
	pname = bb.data.getVar('PN', localdata, True)
	pcurver = bb.data.getVar('PV', localdata, True)
	pdesc = bb.data.getVar('DESCRIPTION', localdata, True)
        if pdesc is not None:
                pdesc = pdesc.replace(',','')
                pdesc = pdesc.replace('\n','')

	pgrp = bb.data.getVar('SECTION', localdata, True)
	plicense = bb.data.getVar('LICENSE', localdata, True).replace(',','_')
	if bb.data.getVar('LIC_FILES_CHKSUM', localdata, True):
		pchksum="1"
	else:
		pchksum="0"

	if bb.data.getVar('RECIPE_STATUS', localdata, True):
		hasrstatus="1"
	else:
		hasrstatus="0"

	rstatus = bb.data.getVar('RECIPE_STATUS', localdata, True)
        if rstatus is not None:
                rstatus = rstatus.replace(',','')
		
	pupver = bb.data.getVar('RECIPE_LATEST_VERSION', localdata, True)
	if pcurver == pupver:
		vermatch="1"
	else:
		vermatch="0"
	noupdate_reason = bb.data.getVar('RECIPE_NO_UPDATE_REASON', localdata, True)
	if noupdate_reason is None:
		noupdate="0"
	else:
		noupdate="1"
                noupdate_reason = noupdate_reason.replace(',','')

	ris = bb.data.getVar('RECIPE_INTEL_SECTION', localdata, True)
	maintainer = bb.data.getVar('RECIPE_MAINTAINER', localdata, True)
	rttr = bb.data.getVar('RECIPE_TIME_BETWEEN_LAST_TWO_RELEASES', localdata, True)
	rlrd = bb.data.getVar('RECIPE_LATEST_RELEASE_DATE', localdata, True)
	dc = bb.data.getVar('DEPENDENCY_CHECK', localdata, True)
	rc = bb.data.getVar('RECIPE_COMMENTS', localdata, True)
        result = dist_check.compare_in_distro_packages_list(distro_check_dir, localdata)

	bb.note("DISTRO: %s,%s,%s,%s,%s,%s,%s,%s,%s, %s, %s, %s\n" % \
		  (pname, pdesc, maintainer, plicense, pchksum, hasrstatus, vermatch, pcurver, pupver, noupdate, noupdate_reason, rstatus))
        line = pn
        for i in result:
            line = line + "," + i
        bb.note("%s\n" % line)
}

addtask distrodata
do_distrodata[nostamp] = "1"
python do_distrodata() {
	logpath = bb.data.getVar('LOG_DIR', d, 1)
	bb.utils.mkdirhier(logpath)
	logfile = os.path.join(logpath, "distrodata.csv")

        import oe.distro_check as dist_check
	localdata = bb.data.createCopy(d)
        tmpdir = bb.data.getVar('TMPDIR', d, 1)
        distro_check_dir = os.path.join(tmpdir, "distro_check")
        datetime = bb.data.getVar('DATETIME', localdata, 1)
        dist_check.update_distro_data(distro_check_dir, datetime)

        pn = bb.data.getVar("PN", d, True)
        bb.note("Package Name: %s" % pn)

	if pn.find("-native") != -1:
	    pnstripped = pn.split("-native")
	    bb.note("Native Split: %s" % pnstripped)
	    bb.data.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + bb.data.getVar('OVERRIDES', d, True), localdata)
	    bb.data.update_data(localdata)

	if pn.find("-cross") != -1:
	    pnstripped = pn.split("-cross")
	    bb.note("cross Split: %s" % pnstripped)
	    bb.data.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + bb.data.getVar('OVERRIDES', d, True), localdata)
	    bb.data.update_data(localdata)

	if pn.find("-initial") != -1:
	    pnstripped = pn.split("-initial")
	    bb.note("initial Split: %s" % pnstripped)
	    bb.data.setVar('OVERRIDES', "pn-" + pnstripped[0] + ":" + bb.data.getVar('OVERRIDES', d, True), localdata)
	    bb.data.update_data(localdata)

	"""generate package information from .bb file"""
	pname = bb.data.getVar('PN', localdata, True)
	pcurver = bb.data.getVar('PV', localdata, True)
	pdesc = bb.data.getVar('DESCRIPTION', localdata, True)
        if pdesc is not None:
                pdesc = pdesc.replace(',','')
                pdesc = pdesc.replace('\n','')

	pgrp = bb.data.getVar('SECTION', localdata, True)
	plicense = bb.data.getVar('LICENSE', localdata, True).replace(',','_')
	if bb.data.getVar('LIC_FILES_CHKSUM', localdata, True):
		pchksum="1"
	else:
		pchksum="0"

	if bb.data.getVar('RECIPE_STATUS', localdata, True):
		hasrstatus="1"
	else:
		hasrstatus="0"

	rstatus = bb.data.getVar('RECIPE_STATUS', localdata, True)
        if rstatus is not None:
                rstatus = rstatus.replace(',','')
		
	pupver = bb.data.getVar('RECIPE_LATEST_VERSION', localdata, True)
	if pcurver == pupver:
		vermatch="1"
	else:
		vermatch="0"

	noupdate_reason = bb.data.getVar('RECIPE_NO_UPDATE_REASON', localdata, True)
	if noupdate_reason is None:
		noupdate="0"
	else:
		noupdate="1"
                noupdate_reason = noupdate_reason.replace(',','')

	ris = bb.data.getVar('RECIPE_INTEL_SECTION', localdata, True)
	maintainer = bb.data.getVar('RECIPE_MAINTAINER', localdata, True)
	rttr = bb.data.getVar('RECIPE_TIME_BETWEEN_LAST_TWO_RELEASES', localdata, True)
	rlrd = bb.data.getVar('RECIPE_LATEST_RELEASE_DATE', localdata, True)
	dc = bb.data.getVar('DEPENDENCY_CHECK', localdata, True)
	rc = bb.data.getVar('RECIPE_COMMENTS', localdata, True)
        # do the comparison
        result = dist_check.compare_in_distro_packages_list(distro_check_dir, localdata)

	lf = bb.utils.lockfile(logfile + ".lock")
	f = open(logfile, "a")
	f.write("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s," % \
		  (pname, pdesc, maintainer, plicense, pchksum, hasrstatus, vermatch, pcurver, pupver, noupdate, noupdate_reason, rstatus))
        line = ""
        for i in result:
            line = line + "," + i
        f.write(line + "\n")
        f.close()
        bb.utils.unlockfile(lf)
}

addtask distrodataall after do_distrodata
do_distrodataall[recrdeptask] = "do_distrodata"
do_distrodataall[nostamp] = "1"
do_distrodataall() {
	:
}

