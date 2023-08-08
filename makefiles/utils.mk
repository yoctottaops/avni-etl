check_db:
ifndef db
	@echo "Provde the db variable"
	exit 1
endif

check_orgSchema:
ifndef orgSchema
	@echo "Provde the orgSchema variable"
	exit 1
endif

check_dbUser:
ifndef dbUser
	@echo "Provde the dbUser variable"
	exit 1
endif

check_dbOwner:
ifndef dbOwner
	@echo "Provde the dbOwner variable"
	exit 1
endif

delete_organisation: check_db check_dbOwner check_dbUser check_orgSchema ## Delete organisation
	-psql -h localhost -U openchs $(db) -c "select delete_etl_metadata_for_schema('$(orgSchema)', '$(dbUser)', '$(dbOwner)')";

delete_table: check_db check_dbOwner check_orgSchema  ## Delete a table and related metadata
ifndef tableName
	@echo "Provde the tableName variable"
	exit 1
endif
	-psql -h localhost -U openchs $(db) -c "select delete_etl_table_metadata('$(orgSchema)', '$(dbOwner)', '$(tableName)')";
