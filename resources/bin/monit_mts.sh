#!/bin/bash

START_SCRIPT_HOME="/opt/MTS/bin"
LOG_PATH="/opt/MTS/logs"

if [ ! -d ${LOG_PATH} ]; then
    mkdir -p ${LOG_PATH}
fi

pid=`ps -ef | grep 'com.tony.MTSServer' | grep -v grep | awk '{print $2}'`
if [ -z "${pid}" ]; then
    sh ${START_SCRIPT_HOME}/start.sh >> ${LOG_PATH}/start.out.1
fi

