package org.avniproject.etl.domain.metadata.diff;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RenameTableTest {

    @Test
    public void shouldRenameTable() {
        ContextHolder.create(new OrganisationIdentity(1, "dbUser", "schema", OrganisationIdentity.OrganisationType.Organisation));
        RenameTable renameTable = new RenameTable("old_name", "new_name");
        assertThat(renameTable.getSql(), is("alter table schema.old_name rename to new_name;"));
    }
}
