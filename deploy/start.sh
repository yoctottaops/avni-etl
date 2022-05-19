#!/bin/bash
if [ -z "$1" ];
  then echo "Organization Schema names filter isn't specified";
  else echo "Organization Schema names specified: $1";
fi
nohup java -Dlogging.file=/opt/avni-etl/avni.log -Dlogging.path=/opt/avni-etl/ -Dlogging.file.max-size=5mb -Xmx250m -XX:ErrorFile=/opt/avni-etl/jvm.log -jar /opt/avni-etl/avni-etl.jar $1 >> /opt/avni-etl/etl.log 2>&1
