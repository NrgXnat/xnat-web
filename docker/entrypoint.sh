#!/bin/bash
set -e
# Generate xnat-conf.properties from the runtime environment, then hand off.
# make-xnat-config.sh is a no-op when the file already exists (e.g. a conf mounted
# by the chart/operator), so runtime -e overrides take effect on a plain
# `docker run` while a mounted conf is left untouched.
# TZ is honored via the env var; no /etc/localtime symlink (that needed root).
/usr/local/bin/make-xnat-config.sh
exec "$@"
