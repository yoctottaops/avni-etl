build_jar: ## Builds the jar file
	./gradlew clean build -x test

test:
	./gradlew clean test --stacktrace

build_server: build_jar

# <server>
start_server: build_server
	java -jar ./build/libs/etl-1.0.0-SNAPSHOT.jar

boot_run:
	./gradlew bootRun

create-extensions:
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "uuid-ossp"';
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "ltree"';
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "hstore"';

open-test-results:
	open build/reports/tests/test/index.html
