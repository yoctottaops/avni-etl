#!/bin/bash
if [ -z "$1" ];
  then echo "Organization Schema names filter isn't specified";
  else echo "Organization Schema names specified: $1";
fi
nohup java -DorgSchemaNames=$1 -Dlogging.file=/opt/avni-etl/avni.log -Dlogging.path=/opt/avni-etl/ -Dlogging.file.max-size=5mb -Xmx250m -XX:ErrorFile=/opt/avni-etl/jvm.log /opt/avni-etl/avni-etl.jar >> /opt/avni-etl/etl.log 2>&1
