package org.avniproject.etl.domain.metadata;

import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.domain.Model;
import org.avniproject.etl.domain.metadata.diff.AddIndex;
import org.avniproject.etl.domain.metadata.diff.Diff;

import java.util.UUID;

public class IndexMetadata extends Model {
    private final String name;
    private final ColumnMetadata column;

    public IndexMetadata(ColumnMetadata column) {
        super();
        this.name = makeIndexName();
        this.column = column;
    }

    public IndexMetadata(Integer id, String name, ColumnMetadata column) {
        super(id);
        this.name = name;
        this.column = column;
    }

    private static String makeIndexName() {
        return  OrgIdentityContextHolder.getDbSchema() + "_" + UUID.randomUUID() + "_idx";
    }

    public String getName() {
        return name;
    }

    public boolean matches(IndexMetadata indexMetadata) {
        return this.column.matches(indexMetadata.column);
    }

    public Diff createIndex(String tableName) {
        return new AddIndex(name, tableName, column.getName());
    }

    public Integer getColumnId() {
        return column.getId();
    }

    public String getColumnName() {
        return column.getName();
    }

    public void mergeWith(IndexMetadata oldIndexMetadata) {
        setId(oldIndexMetadata.getId());
    }
}
