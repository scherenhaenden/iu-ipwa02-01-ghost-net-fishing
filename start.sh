#!/bin/sh
if [ "$DEBUG" = "true" ]; then
  exec catalina.sh jpda run
else
  exec catalina.sh run
fi
