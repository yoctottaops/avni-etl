help:
	@IFS=$$'\n' ; \
	help_lines=(`fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//'`); \
	for help_line in $${help_lines[@]}; do \
	    IFS=$$'#' ; \
	    help_split=($$help_line) ; \
	    help_command=`echo $${help_split[0]} | sed -e 's/^ *//' -e 's/ *$$//'` ; \
	    help_info=`echo $${help_split[2]} | sed -e 's/^ *//' -e 's/ *$$//'` ; \
	    printf "%-30s %s\n" $$help_command $$help_info ; \
	done

include makefiles/utils.mk

build_jar: ## Builds the jar file
	./gradlew clean build -x test

test:
	./gradlew clean test --stacktrace

build_server: build_jar

# <server>
start_server: build_server
	java -jar ./build/libs/etl-1.0.0-SNAPSHOT.jar

start_server_with_dump_data_org: build_server
	OPENCHS_DATABASE_NAME=avni_org OPENCHS_CLIENT_ID=dummy OPENCHS_KEYCLOAK_CLIENT_SECRET=dummy AVNI_IDP_TYPE=none java -jar ./build/libs/etl-1.0.0-SNAPSHOT.jar

debug_server: build_server
	java -Xmx2048m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar ./build/libs/etl-1.0.0-SNAPSHOT.jar

debug_server_with_dump_data_org: build_server
	OPENCHS_DATABASE_NAME=avni_org OPENCHS_CLIENT_ID=dummy OPENCHS_KEYCLOAK_CLIENT_SECRET=dummy AVNI_IDP_TYPE=none java -Xmx2048m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar ./build/libs/etl-1.0.0-SNAPSHOT.jar

boot_run:
	./gradlew bootRun

create-extensions:
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "uuid-ossp"';
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "ltree"';
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "hstore"';

open-test-results:
	open build/reports/tests/test/index.html

start: boot_run

debug:
	./gradlew bootRun -Dagentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

delete-etl-metadata:
ifndef schemaName
	@echo "Provde the schemaName variable"
	exit 1
endif
ifndef dbUser
	@echo "Provde the dbUser variable"
	exit 1
endif
ifndef db
	@echo "Provde the db variable"
	exit 1
endif
	-psql -h localhost -Uopenchs $(db) -c "select delete_etl_metadata_for_schema('$(schemaName)', '$(dbUser)')"

delete-etl-metadata-for-org:
ifndef schemaName
	@echo "Provde the schemaName variable"
	exit 1
endif
ifndef dbUser
	@echo "Provde the dbUser variable"
	exit 1
endif
ifndef db
	@echo "Provde the db variable"
	exit 1
endif
	-psql -h localhost -Uopenchs $(db) -c "select delete_etl_metadata_for_org('$(schemaName)', '$(dbUser)')"
