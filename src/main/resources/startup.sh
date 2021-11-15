#!/bin/sh
#打印执行明细
set -x

echo "===开始脚本执行脚本=="

ENV_VALUE=default

JVM_OPTS="-Xmx1g -Xms1g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC -XX:MaxGCPauseMillis=10 -XX:InitiatingHeapOccupancyPercent=35 -XX:ParallelGCThreads=20 -XX:ConcGCThreads=10 -XX:+HeapDumpOnOutOfMemoryError -XX:MaxInlineLevel=15 -Djava.awt.headless=true -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:app-gc.log"

if [ -f "./.pid" ] ;then
	echo "错误：后台服务未关闭，进程编号：$(cat ./.pid)，请先关闭正在执行的服务！"
else
    if [ ! -n "$ENV_VALUE" ] ;then
        echo "警告：未输入参数指定程序执行的profile环境， 例如：prd"
        exit -1
    fi

    java -version
    nohup java -server $JVM_OPTS -cp .:tdc-connector-acq.jar -Dspring.profiles.active=$ENV_VALUE org.springframework.boot.loader.PropertiesLauncher >/dev/null 2>&1 &
    pid=$!
    echo $pid > ./.pid
fi
echo "===脚本执行结束=="