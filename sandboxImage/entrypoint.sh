#!/bin/sh

chown -R student:student /execution/
exec runuser -u student -- "$@"

