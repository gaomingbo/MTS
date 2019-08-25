#!/bin/bash
echo 'Starting MTS system...'
START_SCRIPT_HOME="/opt/MTS/bin"
CLASSPATH="${START_SCRIPT_HOME}/../lib/*"
CONF_DIR="${START_SCRIPT_HOME}/../conf"
JAVA_HOME="/opt/tools/jdk/bin"

nohup ${JAVA_HOME}/java \
-XX:MetaspaceSize=64M \
-Xms128M \
-Xmx512M \
-Xmn128m \
-XX:+PrintGCDetails \
-server \
-XX:+UseG1GC \
-XX:G1HeapRegionSize=32M \
-XX:+UseStringDeduplication \
-XX:+UseGCOverheadLimit \
-XX:+ExplicitGCInvokesConcurrent \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:+ExitOnOutOfMemoryError \
-cp "${CONF_DIR}":"${CLASSPATH}" \
com.tony.Server 2>&1 >/dev/null &


pid=`sh ${START_SCRIPT_HOME}/status.sh`

if [ -n ${pid} ]; then
echo 'Starting MTS system success.'
else
echo 'Starting MTS system failed.'
fi