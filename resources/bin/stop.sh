#!/bin/bash
START_SCRIPT_HOME="/opt/MTS/bin"

echo "Start stop MTS system..."
pid=`sh ${START_SCRIPT_HOME}/status.sh`
if [ -n "${pid}" ]; then
echo "kill ${pid}"
kill -9 ${pid}
echo "Stop MTS system success."
fi
