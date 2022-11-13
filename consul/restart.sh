#!/bin/bash
app_port=9090
app_name=zero.jar
java_ops="
-Djava.io.tmpdir=./config/tmp
-Dserver.port=${app_port}
-Dspring.cloud.consul.host=22.10.204.11
-Dspring.cloud.consul.port=8500
-Dspring.config.additional-location=./config/application.yml
"
ps -ef|grep -v grep|grep ${app_name}|while read u p o
do
    kill -9 $p
    echo "kill [$p] sucess!"
done
nohup java ${java_ops} -jar ${app_name} >> app.log 2>&1 &
echo "${app_name} start success!"
tail -500f app.log
