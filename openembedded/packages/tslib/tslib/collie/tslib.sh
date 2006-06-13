#!/bin/sh

case `uname -r` in
2.4*)
	TSLIB_TSDEVICE=/dev/ts
	TSLIB_TSEVENTTYPE=COLLIE
	TSLIB_CONFFILE=/usr/share/tslib/ts.conf-collie-2.4
	;;
*)
	TSLIB_TSDEVICE=/dev/input/touchscreen0
	TSLIB_TSEVENTTYPE=INPUT
	TSLIB_CONFFILE=/usr/share/tslib/ts-2.6.conf
	;;
esac

export TSLIB_TSDEVICE TSLIB_TSEVENTTYPE TSLIB_CONFFILE
