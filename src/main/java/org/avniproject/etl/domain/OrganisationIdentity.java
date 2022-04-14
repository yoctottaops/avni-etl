package org.avniproject.etl.domain;

public class OrganisationIdentity {
    public String getSchemaName() {
        return schemaName;
    }

    private final String dbUser;
    private final String schemaName;

    public OrganisationIdentity(String dbUser, String schemaName) {
        this.dbUser = dbUser;
        this.schemaName = schemaName;
    }

    @Override
    public String toString() {
        return String.format("Schema: %s DB User: %s", schemaName, dbUser);
    }

    public String getDbUser() {
        return dbUser;
    }
}
