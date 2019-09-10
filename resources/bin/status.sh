#!/bin/bash
pid=`ps -ef | grep 'com.tony.MTSServer' | grep -v grep | awk '{print $2}'`
echo ${pid}
