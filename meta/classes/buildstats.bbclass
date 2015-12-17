BUILDSTATS_BASE = "${TMPDIR}/buildstats/"

################################################################################
# Build statistics gathering.
#
# The CPU and Time gathering/tracking functions and bbevent inspiration
# were written by Christopher Larson and can be seen here:
# http://kergoth.pastey.net/142813
#
################################################################################

def get_process_cputime(pid):
    with open("/proc/%d/stat" % pid, "r") as f:
        fields = f.readline().rstrip().split()
    # 13: utime, 14: stime, 15: cutime, 16: cstime
    return sum(int(field) for field in fields[13:16])

def get_cputime():
    with open("/proc/stat", "r") as f:
        fields = f.readline().rstrip().split()[1:]
    return sum(int(field) for field in fields)

def set_timedata(var, d, server_time):
    cputime = get_cputime()
    proctime = get_process_cputime(os.getpid())
    d.setVar(var, (server_time, cputime, proctime))

def get_timedata(var, d, end_time):
    timedata = d.getVar(var, False)
    if timedata is None:
        return
    oldtime, oldcpu, oldproc = timedata
    procdiff = get_process_cputime(os.getpid()) - oldproc
    cpudiff = get_cputime() - oldcpu
    timediff = end_time - oldtime
    if cpudiff > 0:
        cpuperc = float(procdiff) * 100 / cpudiff
    else:
        cpuperc = None
    return timediff, cpuperc

def set_buildtimedata(var, d):
    import time
    time = time.time()
    cputime = get_cputime()
    proctime = get_process_cputime(os.getpid())
    d.setVar(var, (time, cputime, proctime))

def get_buildtimedata(var, d):
    import time
    timedata = d.getVar(var, False)
    if timedata is None:
        return
    oldtime, oldcpu, oldproc = timedata
    procdiff = get_process_cputime(os.getpid()) - oldproc
    cpudiff = get_cputime() - oldcpu
    end_time = time.time()
    timediff = end_time - oldtime
    if cpudiff > 0:
        cpuperc = float(procdiff) * 100 / cpudiff
    else:
        cpuperc = None
    return timediff, cpuperc

def write_task_data(status, logfile, e, d):
    bn = d.getVar('BUILDNAME', True)
    bsdir = os.path.join(d.getVar('BUILDSTATS_BASE', True), bn)
    with open(os.path.join(logfile), "a") as f:
        timedata = get_timedata("__timedata_task", d, e.time)
        if timedata:
            elapsedtime, cpu = timedata
            f.write(d.expand("${PF}: %s: Elapsed time: %0.2f seconds \n" %
                                    (e.task, elapsedtime)))
            if cpu:
                f.write("CPU usage: %0.1f%% \n" % cpu)
        if status is "passed":
            f.write("Status: PASSED \n")
        else:
            f.write("Status: FAILED \n")
        f.write("Ended: %0.2f \n" % e.time)

python run_buildstats () {
    import bb.build
    import bb.event
    import time, subprocess, platform

    bn = d.getVar('BUILDNAME', True)
    bsdir = os.path.join(d.getVar('BUILDSTATS_BASE', True), bn)
    taskdir = os.path.join(bsdir, d.getVar('PF', True))

    if isinstance(e, bb.event.BuildStarted):
        ########################################################################
        # at first pass make the buildstats heriarchy and then
        # set the buildname
        ########################################################################
        bb.utils.mkdirhier(bsdir)
        set_buildtimedata("__timedata_build", d)
        build_time = os.path.join(bsdir, "build_stats")
        # write start of build into build_time
        with open(build_time, "a") as f:
            host_info = platform.uname()
            f.write("Host Info: ")
            for x in host_info:
                if x:
                    f.write(x + " ")
            f.write("\n")
            f.write("Build Started: %0.2f \n" % time.time())

    elif isinstance(e, bb.event.BuildCompleted):
        build_time = os.path.join(bsdir, "build_stats")
        with open(build_time, "a") as f:
            ########################################################################
            # Write build statistics for the build
            ########################################################################
            timedata = get_buildtimedata("__timedata_build", d)
            if timedata:
                time, cpu = timedata
                # write end of build and cpu used into build_time
                f.write("Elapsed time: %0.2f seconds \n" % (time))
                if cpu:
                    f.write("CPU usage: %0.1f%% \n" % cpu)

    if isinstance(e, bb.build.TaskStarted):
        set_timedata("__timedata_task", d, e.time)
        bb.utils.mkdirhier(taskdir)
        # write into the task event file the name and start time
        with open(os.path.join(taskdir, e.task), "a") as f:
            f.write("Event: %s \n" % bb.event.getName(e))
            f.write("Started: %0.2f \n" % e.time)

    elif isinstance(e, bb.build.TaskSucceeded):
        write_task_data("passed", os.path.join(taskdir, e.task), e, d)
        if e.task == "do_rootfs":
            bs = os.path.join(bsdir, "build_stats")
            with open(bs, "a") as f:
                rootfs = d.getVar('IMAGE_ROOTFS', True)
                rootfs_size = subprocess.Popen(["du", "-sh", rootfs], stdout=subprocess.PIPE).stdout.read()
                f.write("Uncompressed Rootfs size: %s" % rootfs_size)

    elif isinstance(e, bb.build.TaskFailed):
        write_task_data("failed", os.path.join(taskdir, e.task), e, d)
        ########################################################################
        # Lets make things easier and tell people where the build failed in
        # build_status. We do this here because BuildCompleted triggers no
        # matter what the status of the build actually is
        ########################################################################
        build_status = os.path.join(bsdir, "build_stats")
        with open(build_status, "a") as f:
            f.write(d.expand("Failed at: ${PF} at task: %s \n" % e.task))
}

addhandler run_buildstats
run_buildstats[eventmask] = "bb.event.BuildStarted bb.event.BuildCompleted bb.build.TaskStarted bb.build.TaskSucceeded bb.build.TaskFailed"

