package org.avniproject.etl.repository.dynamicInsert;

import org.avniproject.etl.builder.OrganisationIdentityBuilder;
import org.avniproject.etl.builder.domain.metadata.TableMetadataBuilder;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SqlGeneratorTest {

    @Test
    public void shouldGenerateSql() throws IOException {
        ContextHolder.create(new OrganisationIdentityBuilder().build());
        TableMetadata tableMetadata = new TableMetadataBuilder().build();
        System.out.println(new SqlGenerator().generateSql(tableMetadata));
    }

}