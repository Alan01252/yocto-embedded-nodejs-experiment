#! /bin/sh
autoreconf -v --install || exit 1
./configure "$@"
