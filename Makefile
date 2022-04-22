build_jar: ## Builds the jar file
	./gradlew clean build -x test

test:
	./gradlew clean test --stacktrace

create-extensions:
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "uuid-ossp"';
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "ltree"';
	-psql -h localhost -Uopenchs openchs_test -c 'create extension if not exists "hstore"';
