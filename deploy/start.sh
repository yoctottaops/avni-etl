#!/bin/bash
nohup java -Dlogging.file=/opt/avni-etl/avni.log -Dlogging.path=/opt/avni-etl/ -Dlogging.file.max-size=5mb -Xmx250m -XX:ErrorFile=/opt/avni-etl/jvm.log /opt/avni-etl/avni-etl.jar >> /opt/avni-etl/etl.log 2>&1
