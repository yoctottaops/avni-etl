#!/bin/bash
if [ -z "$1" ];
  then echo "Organization Schema names filter isn't specified";
  else echo "Organization Schema names specified: $1";
fi

if [ ! -e /opt/avni-etl/etl.flg ]; then
	touch /opt/avni-etl/etl.flg;
	printf 'ETL Job Starting, at %s\n' "$(date)" >> /opt/avni-etl/log/etl-trigger.log
	nohup java -Dlogging.file=/opt/avni-etl/log/avni.log -Dlogging.path=/opt/avni-etl/log/ -Dlogging.file.max-size=5mb -Xmx250m -XX:ErrorFile=/opt/avni-etl/log/jvm.log -jar /opt/avni-etl/avni-etl.jar $1 >> /opt/avni-etl/log/etl.log 2>&1;
	rm /opt/avni-etl/etl.flg;
	printf 'ETL Job ended, at %s\n' "$(date)" >> /opt/avni-etl/log/etl-trigger.log
else
	printf 'ETL Job already in-prorgess, Not triggering it again. %s\n' "$(date)" >> /opt/avni-etl/log/etl-trigger.log
fi