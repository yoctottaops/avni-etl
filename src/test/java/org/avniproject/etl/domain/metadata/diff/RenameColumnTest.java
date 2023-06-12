package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RenameColumnTest {

    @Test
    public void shouldCreateSqlForRenamingColumn() {
        ContextHolder.setContext(OrganisationIdentity.createForOrganisation("dbUser", "schema"));
        RenameColumn renameColumn = new RenameColumn("table_name", "oldName", "newName");
        System.out.println(renameColumn.getSql());
        assertThat(renameColumn.getSql(), is("alter table schema.table_name rename column \"oldName\" to \"newName\";"));
    }

}
