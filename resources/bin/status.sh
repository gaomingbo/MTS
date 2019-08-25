#!/bin/bash
pid=`ps -ef | grep 'com.tony.Server' | grep -v grep | awk '{print $2}'`
echo ${pid}
