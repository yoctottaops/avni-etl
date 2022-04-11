package org.avniproject.etl.repository.dynamicInsert;

import org.avniproject.etl.builder.OrganisationIdentityBuilder;
import org.avniproject.etl.builder.domain.metadata.TableMetadataBuilder;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

class SqlGeneratorTest {

    @Test
    public void shouldGenerateSql() throws IOException {
        ContextHolder.setContext(new OrganisationIdentityBuilder().build());
        TableMetadata tableMetadata = new TableMetadataBuilder()
                .forIndividual()
                .withColumnMetadata(new ColumnMetadata(1, "numeric field", 10, ColumnMetadata.ConceptType.Numeric, UUID.randomUUID().toString()))
                .withColumnMetadata(new ColumnMetadata(1, "text field", 10, ColumnMetadata.ConceptType.Text, UUID.randomUUID().toString()))
                .withColumnMetadata(new ColumnMetadata(1, "single select", 10, ColumnMetadata.ConceptType.SingleSelect, UUID.randomUUID().toString()))
                .withColumnMetadata(new ColumnMetadata(1, "multi select", 10, ColumnMetadata.ConceptType.MultiSelect, UUID.randomUUID().toString()))
                .withColumnMetadata(new ColumnMetadata(1, "date", 10, ColumnMetadata.ConceptType.Date, UUID.randomUUID().toString()))
                .build();
        Calendar startTime = Calendar.getInstance();
        startTime.set(1970, 1, 12, 13, 23, 4);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2021, 10, 11, 13, 23, 4);

        System.out.println(new TransactionalSyncSqlGenerator().generateSql(tableMetadata, startTime.getTime(), endTime.getTime()));
    }

}