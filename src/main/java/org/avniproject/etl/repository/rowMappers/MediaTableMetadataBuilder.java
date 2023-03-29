package org.avniproject.etl.repository.rowMappers;

import org.avniproject.etl.domain.metadata.ColumnMetadata;
import org.avniproject.etl.domain.metadata.TableMetadata;
import org.avniproject.etl.repository.rowMappers.tableMappers.MediaTable;

import java.util.stream.Collectors;

public class MediaTableMetadataBuilder {
    public static TableMetadata build() {
        TableMetadata mediaTableMetadata = new TableMetadata();
        MediaTable mediaTable = new MediaTable();
        mediaTableMetadata.setName(mediaTable.name(null));
        mediaTableMetadata.setType(TableMetadata.Type.Media);
        mediaTableMetadata.addColumnMetadata(mediaTable.columns().stream().map(column -> new ColumnMetadata(column, null, null, null)).collect(Collectors.toList()));

        return mediaTableMetadata;
    }
}
