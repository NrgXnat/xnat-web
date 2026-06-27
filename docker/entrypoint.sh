#!/bin/bash
set -e
# TZ is honored via the env var; no /etc/localtime symlink (that needed root).
# Kept as a hook for future non-privileged startup steps.
exec "$@"
