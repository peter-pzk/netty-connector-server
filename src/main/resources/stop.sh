#!/bin/sh
echo "===脚本执行开始==="

if [ ! -f "./.pid" ]; then
	echo "警告：服务已被正常关闭或存储服务进程编号的文件不存在，请确认服务状态！"
else
    MODULE_PID=$(cat ./.pid)
    nLines=`ps -ef |grep $MODULE_PID | wc -l`
    if [ ${nLines} = 1 ]; then
        rm -rf ./.pid
        echo "===服务进程不存在：$MODULE_PID==="
        exit 1;
    fi

    echo "===正在关闭服务进程：$MODULE_PID==="
    kill -15 $MODULE_PID

    sleep 4

    $nLines=`ps -ef |grep $MODULE_PID | wc -l`

    if [ ${nLines} -ne 1 ]; then
        echo "===强制杀死服务进程：$MODULE_PID==="
        kill -9 $MODULE_PID
    else
        echo "===关闭服务进程成功：$MODULE_PID==="
    fi

    rm -rf ./.pid
fi

echo "===脚本执行结束==="
