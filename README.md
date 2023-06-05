### Description
The ETL service is responsible for creation, maintenance and distribution of ETL schemas for the Avni database. 

Because of RLS, heavy usage of jsonb columns and hierarchical nature of the address tables, the public schema is not especially suited for analytical queries. This service converts this data structure into a more flat structure with all jsonb keys converted to columns. A scheduled job runs to keep populating data updated from the last run. 

```Organisation and organisation_group``` tables have fields ```schema_name``` that are used by ETL to figure out if an org has ETL enabled, and the schema where data should be transferred to. 
Since form fields can change, ETL adjusts tables to match the new application structure. The schema is stored in the ```table_metadata, column_metadata and index_metadata ``` tables on the public schema. 
```public.entity_sync_status``` stores the current sync status of each table.

### Developer setup
- Use jenv to match java version (Add whatever java version is mentioned)
- Clone avni-server and run ```make build_db build_test_db``` to have all required tables in your system
- ```make start``` to start server
- ```make test``` to run tests


[![CircleCI](https://circleci.com/gh/avniproject/avni-etl/tree/main.svg?style=svg)](https://circleci.com/gh/avniproject/avni-etl/tree/main)
