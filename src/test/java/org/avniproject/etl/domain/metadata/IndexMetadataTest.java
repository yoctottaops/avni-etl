package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.builder.OrganisationIdentityBuilder;
import org.avniproject.etl.domain.ContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


class IndexMetadataTest {

    @BeforeEach
    public void setup() {
        ContextHolder.setContext(new OrganisationIdentityBuilder().build());
    }

    @Test
    public void matches_shouldMatchColumnMetadata() {
        ColumnMetadata id = new ColumnMetadata(new Column("id", Column.Type.numeric), null, null, null);
        ColumnMetadata uuid = new ColumnMetadata(new Column("uuid", Column.Type.numeric), null, null, null);

        assertThat(id.matches(id), is(true));
        assertThat(id.matches(uuid), is(false));

        assertThat(new IndexMetadata(id).matches(new IndexMetadata(1, "orgc_as123_idx", id)), is(true));
        assertThat(new IndexMetadata(id).matches(new IndexMetadata(1, "orgc_as123_idx", uuid)), is(false));
        assertThat(new IndexMetadata(1, "orgc_as123_idx", uuid).matches(new IndexMetadata(id)), is(false));
    }

}