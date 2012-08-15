#
# BitBake (No)TTY UI Implementation
#
# Handling output to TTYs or files (no TTY)
#
# Copyright (C) 2006-2012 Richard Purdie
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

from __future__ import division

import os
import sys
import xmlrpclib
import logging
import progressbar
import signal
import bb.msg
import fcntl
import struct
import copy
from bb.ui import uihelper

logger = logging.getLogger("BitBake")
interactive = sys.stdout.isatty()

class BBProgress(progressbar.ProgressBar):
    def __init__(self, msg, maxval):
        self.msg = msg
        widgets = [progressbar.Percentage(), ' ', progressbar.Bar(), ' ',
           progressbar.ETA()]

        try:
            self._resize_default = signal.getsignal(signal.SIGWINCH)
        except:
            self._resize_default = None
        progressbar.ProgressBar.__init__(self, maxval, [self.msg + ": "] + widgets)

    def _handle_resize(self, signum, frame):
        progressbar.ProgressBar._handle_resize(self, signum, frame)
        if self._resize_default:
            self._resize_default(signum, frame)
    def finish(self):
        progressbar.ProgressBar.finish(self)
        if self._resize_default:
            signal.signal(signal.SIGWINCH, self._resize_default)

class NonInteractiveProgress(object):
    fobj = sys.stdout

    def __init__(self, msg, maxval):
        self.msg = msg
        self.maxval = maxval

    def start(self):
        self.fobj.write("%s..." % self.msg)
        self.fobj.flush()
        return self

    def update(self, value):
        pass

    def finish(self):
        self.fobj.write("done.\n")
        self.fobj.flush()

def new_progress(msg, maxval):
    if interactive:
        return BBProgress(msg, maxval)
    else:
        return NonInteractiveProgress(msg, maxval)

def pluralise(singular, plural, qty):
    if(qty == 1):
        return singular % qty
    else:
        return plural % qty


class InteractConsoleLogFilter(logging.Filter):
    def __init__(self, tf, format):
        self.tf = tf
        self.format = format

    def filter(self, record):
        if record.levelno == self.format.NOTE and (record.msg.startswith("Running") or record.msg.startswith("package ")):
            return False
        self.tf.clearFooter()
        return True

class TerminalFilter(object):
    columns = 80

    def sigwinch_handle(self, signum, frame):
        self.columns = self.getTerminalColumns()
        if self._sigwinch_default:
            self._sigwinch_default(signum, frame)

    def getTerminalColumns(self):
        def ioctl_GWINSZ(fd):
            try:
                cr = struct.unpack('hh', fcntl.ioctl(fd, self.termios.TIOCGWINSZ, '1234'))
            except:
                return None
            return cr
        cr = ioctl_GWINSZ(sys.stdout.fileno())
        if not cr:
            try:
                fd = os.open(os.ctermid(), os.O_RDONLY)
                cr = ioctl_GWINSZ(fd)
                os.close(fd)
            except:
                pass
        if not cr:
            try:
                cr = (env['LINES'], env['COLUMNS'])
            except:
                cr = (25, 80)
        return cr[1]

    def __init__(self, main, helper, console, format):
        self.main = main
        self.helper = helper
        self.cuu = None
        self.stdinbackup = None
        self.interactive = sys.stdout.isatty()
        self.footer_present = False
        self.lastpids = []

        if not self.interactive:
            return

        import curses
        import termios
        self.curses = curses
        self.termios = termios
        try:
            fd = sys.stdin.fileno()
            self.stdinbackup = termios.tcgetattr(fd)
            new = copy.deepcopy(self.stdinbackup)
            new[3] = new[3] & ~termios.ECHO
            termios.tcsetattr(fd, termios.TCSADRAIN, new)
            curses.setupterm()
            self.ed = curses.tigetstr("ed")
            if self.ed:
                self.cuu = curses.tigetstr("cuu")
            try:
                self._sigwinch_default = signal.getsignal(signal.SIGWINCH)
                signal.signal(signal.SIGWINCH, self.sigwinch_handle)
            except:
                pass
            self.columns = self.getTerminalColumns()
        except:
            self.cuu = None
        console.addFilter(InteractConsoleLogFilter(self, format))

    def clearFooter(self):
        if self.footer_present:
            lines = self.footer_present
            sys.stdout.write(self.curses.tparm(self.cuu, lines))
            sys.stdout.write(self.curses.tparm(self.ed))
        self.footer_present = False

    def updateFooter(self):
        if not self.cuu:
            return
        activetasks = self.helper.running_tasks
        failedtasks = self.helper.failed_tasks
        runningpids = self.helper.running_pids
        if self.footer_present and (self.lastpids == runningpids):
            return
        if self.footer_present:
            self.clearFooter()
        if not activetasks:
            return
        tasks = []
        for t in runningpids:
            tasks.append("%s (pid %s)" % (activetasks[t]["title"], t))

        if self.main.shutdown:
            content = "Waiting for %s running tasks to finish:" % len(activetasks)
        else:
            content = "Currently %s running tasks (%s of %s):" % (len(activetasks), self.helper.tasknumber_current, self.helper.tasknumber_total)
        print content
        lines = 1 + int(len(content) / (self.columns + 1))
        for tasknum, task in enumerate(tasks):
            content = "%s: %s" % (tasknum, task)
            print content
            lines = lines + 1 + int(len(content) / (self.columns + 1))
        self.footer_present = lines
        self.lastpids = runningpids[:]

    def finish(self):
        if self.stdinbackup:
            fd = sys.stdin.fileno()
            self.termios.tcsetattr(fd, self.termios.TCSADRAIN, self.stdinbackup)

def main(server, eventHandler, tf = TerminalFilter):

    # Get values of variables which control our output
    includelogs = server.runCommand(["getVariable", "BBINCLUDELOGS"])
    loglines = server.runCommand(["getVariable", "BBINCLUDELOGS_LINES"])
    consolelogfile = server.runCommand(["getVariable", "BB_CONSOLELOG"])

    helper = uihelper.BBUIHelper()

    console = logging.StreamHandler(sys.stdout)
    format = bb.msg.BBLogFormatter("%(levelname)s: %(message)s")
    bb.msg.addDefaultlogFilter(console)
    console.setFormatter(format)
    logger.addHandler(console)
    if consolelogfile:
        bb.utils.mkdirhier(os.path.dirname(consolelogfile))
        consolelog = logging.FileHandler(consolelogfile)
        bb.msg.addDefaultlogFilter(consolelog)
        consolelog.setFormatter(format)
        logger.addHandler(consolelog)

    try:
        cmdline = server.runCommand(["getCmdLineAction"])
        if not cmdline:
            print("Nothing to do.  Use 'bitbake world' to build everything, or run 'bitbake --help' for usage information.")
            return 1
        elif not cmdline['action']:
            print(cmdline['msg'])
            return 1
        ret = server.runCommand(cmdline['action'])
        if ret != True:
            print("Couldn't get default commandline! %s" % ret)
            return 1
    except xmlrpclib.Fault as x:
        print("XMLRPC Fault getting commandline:\n %s" % x)
        return 1

    parseprogress = None
    cacheprogress = None
    main.shutdown = 0
    interrupted = False
    return_value = 0
    errors = 0
    warnings = 0
    taskfailures = []

    termfilter = tf(main, helper, console, format)

    while True:
        try:
            termfilter.updateFooter()
            event = eventHandler.waitEvent(0.25)
            if event is None:
                if main.shutdown > 1:
                    break
                continue
            helper.eventHandler(event)
            if isinstance(event, bb.runqueue.runQueueExitWait):
                if not main.shutdown:
                    main.shutdown = 1

            if isinstance(event, logging.LogRecord):
                if event.levelno >= format.ERROR:
                    errors = errors + 1
                    return_value = 1
                elif event.levelno == format.WARNING:
                    warnings = warnings + 1
                # For "normal" logging conditions, don't show note logs from tasks
                # but do show them if the user has changed the default log level to 
                # include verbose/debug messages
                if event.taskpid != 0 and event.levelno <= format.NOTE:
                    continue
                logger.handle(event)
                continue

            if isinstance(event, bb.build.TaskFailed):
                return_value = 1
                logfile = event.logfile
                if logfile and os.path.exists(logfile):
                    termfilter.clearFooter()
                    print("ERROR: Logfile of failure stored in: %s" % logfile)
                    if includelogs and not event.errprinted:
                        print("Log data follows:")
                        f = open(logfile, "r")
                        lines = []
                        while True:
                            l = f.readline()
                            if l == '':
                                break
                            l = l.rstrip()
                            if loglines:
                                lines.append(' | %s' % l)
                                if len(lines) > int(loglines):
                                    lines.pop(0)
                            else:
                                print('| %s' % l)
                        f.close()
                        if lines:
                            for line in lines:
                                print(line)
            if isinstance(event, bb.build.TaskBase):
                logger.info(event._message)
                continue
            if isinstance(event, bb.event.ParseStarted):
                if event.total == 0:
                    continue
                parseprogress = new_progress("Parsing recipes", event.total).start()
                continue
            if isinstance(event, bb.event.ParseProgress):
                parseprogress.update(event.current)
                continue
            if isinstance(event, bb.event.ParseCompleted):
                if not parseprogress:
                    continue

                parseprogress.finish()
                print(("Parsing of %d .bb files complete (%d cached, %d parsed). %d targets, %d skipped, %d masked, %d errors."
                    % ( event.total, event.cached, event.parsed, event.virtuals, event.skipped, event.masked, event.errors)))
                continue

            if isinstance(event, bb.event.CacheLoadStarted):
                cacheprogress = new_progress("Loading cache", event.total).start()
                continue
            if isinstance(event, bb.event.CacheLoadProgress):
                cacheprogress.update(event.current)
                continue
            if isinstance(event, bb.event.CacheLoadCompleted):
                cacheprogress.finish()
                print("Loaded %d entries from dependency cache." % event.num_entries)
                continue

            if isinstance(event, bb.command.CommandFailed):
                return_value = event.exitcode
                errors = errors + 1
                logger.error("Command execution failed: %s", event.error)
                main.shutdown = 2
                continue
            if isinstance(event, bb.command.CommandExit):
                if not return_value:
                    return_value = event.exitcode
                continue
            if isinstance(event, (bb.command.CommandCompleted, bb.cooker.CookerExit)):
                main.shutdown = 2
                continue
            if isinstance(event, bb.event.MultipleProviders):
                logger.info("multiple providers are available for %s%s (%s)", event._is_runtime and "runtime " or "",
                            event._item,
                            ", ".join(event._candidates))
                logger.info("consider defining a PREFERRED_PROVIDER entry to match %s", event._item)
                continue
            if isinstance(event, bb.event.NoProvider):
                return_value = 1
                errors = errors + 1
                if event._runtime:
                    r = "R"
                else:
                    r = ""

                if event._dependees:
                    logger.error("Nothing %sPROVIDES '%s' (but %s %sDEPENDS on or otherwise requires it)", r, event._item, ", ".join(event._dependees), r)
                else:
                    logger.error("Nothing %sPROVIDES '%s'", r, event._item)
                if event._reasons:
                    for reason in event._reasons:
                        logger.error("%s", reason)
                continue

            if isinstance(event, bb.runqueue.sceneQueueTaskStarted):
                logger.info("Running setscene task %d of %d (%s)" % (event.stats.completed + event.stats.active + event.stats.failed + 1, event.stats.total, event.taskstring))
                continue

            if isinstance(event, bb.runqueue.runQueueTaskStarted):
                if event.noexec:
                    tasktype = 'noexec task'
                else:
                    tasktype = 'task'
                logger.info("Running %s %s of %s (ID: %s, %s)",
                            tasktype,
                            event.stats.completed + event.stats.active +
                                event.stats.failed + 1,
                            event.stats.total, event.taskid, event.taskstring)
                continue

            if isinstance(event, bb.runqueue.runQueueTaskFailed):
                taskfailures.append(event.taskstring)
                logger.error("Task %s (%s) failed with exit code '%s'",
                             event.taskid, event.taskstring, event.exitcode)
                continue

            if isinstance(event, bb.runqueue.sceneQueueTaskFailed):
                logger.warn("Setscene task %s (%s) failed with exit code '%s' - real task will be run instead",
                             event.taskid, event.taskstring, event.exitcode)
                continue

            # ignore
            if isinstance(event, (bb.event.BuildBase,
                                  bb.event.StampUpdate,
                                  bb.event.ConfigParsed,
                                  bb.event.RecipeParsed,
                                  bb.event.RecipePreFinalise,
                                  bb.runqueue.runQueueEvent,
                                  bb.runqueue.runQueueExitWait,
                                  bb.event.OperationStarted,
                                  bb.event.OperationCompleted,
                                  bb.event.OperationProgress)):
                continue

            logger.error("Unknown event: %s", event)

        except EnvironmentError as ioerror:
            termfilter.clearFooter()
            # ignore interrupted io
            if ioerror.args[0] == 4:
                pass
        except KeyboardInterrupt:
            termfilter.clearFooter()
            if main.shutdown == 1:
                print("\nSecond Keyboard Interrupt, stopping...\n")
                server.runCommand(["stateStop"])
            if main.shutdown == 0:
                interrupted = True
                print("\nKeyboard Interrupt, closing down...\n")
                server.runCommand(["stateShutdown"])
            main.shutdown = main.shutdown + 1
            pass

    summary = ""
    if taskfailures:
        summary += pluralise("\nSummary: %s task failed:",
                             "\nSummary: %s tasks failed:", len(taskfailures))
        for failure in taskfailures:
            summary += "\n  %s" % failure
    if warnings:
        summary += pluralise("\nSummary: There was %s WARNING message shown.",
                             "\nSummary: There were %s WARNING messages shown.", warnings)
    if return_value:
        summary += pluralise("\nSummary: There was %s ERROR message shown, returning a non-zero exit code.",
                             "\nSummary: There were %s ERROR messages shown, returning a non-zero exit code.", errors)
    if summary:
        print(summary)

    if interrupted:
        print("Execution was interrupted, returning a non-zero exit code.")
        if return_value == 0:
            return_value = 1

    termfilter.finish()

    return return_value
