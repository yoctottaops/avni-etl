package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.rowMappers.tableMappers.PersonTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CreateTableTest {

    @BeforeEach
    public void before() {
        ContextHolder.setContext(new OrganisationIdentity(1, "dbUser", "schema", OrganisationIdentity.OrganisationType.Organisation));
    }

    @Test
    public void shouldCreateSqlWithCommonColumns() {
        ContextHolder.setContext(new OrganisationIdentity(1, "dbUser", "schema", OrganisationIdentity.OrganisationType.Organisation));
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setName("tableName");

        assertThat(new CreateTable("tableName", new PersonTable().columns()).getSql(), is("create table schema.tableName (\"id\" integer, \"uuid\" text, \"is_voided\" boolean, \"created_by_id\" integer, \"last_modified_by_id\" integer, \"created_date_time\" timestamp with time zone, \"last_modified_date_time\" timestamp with time zone, \"address_id\" integer, \"registration_date\" date, \"first_name\" text, \"last_name\" text, \"registration_location\" point, \"legacy_id\" text, \"latest_approval_status\" text, \"date_of_birth\" date, \"date_of_birth_verified\" boolean, \"gender\" text );\ngrant all privileges on all tables in schema schema to dbUser;\ncreate index schematableNamecreated_by_ididx on schema.tableName (created_by_id );\ncreate index schematableNamelast_modified_by_ididx on schema.tableName (last_modified_by_id );\ncreate index schematableNameaddress_ididx on schema.tableName (address_id );\ncreate index schematableNameregistration_dateidx on schema.tableName (registration_date );\ncreate index schematableNamedate_of_birthidx on schema.tableName (date_of_birth );"));
    }
}
