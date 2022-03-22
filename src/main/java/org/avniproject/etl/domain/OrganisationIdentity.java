package org.avniproject.etl.domain;

public class OrganisationIdentity extends Model{
    public String getSchemaName() {
        return schemaName;
    }

    public enum OrganisationType {
        Organisation,
        OrganisationGroup
    }
    private final String dbUser;
    private final String schemaName;
    private final OrganisationType organisationType;

    public OrganisationIdentity(Integer id, String dbUser, String schemaName, OrganisationType organisationType) {
        super(id);
        this.dbUser = dbUser;
        this.schemaName = schemaName;
        this.organisationType = organisationType;
    }

    @Override
    public String toString() {
        return String.format("Id: %s, DB User: %s, DB Type: %s", getId(), dbUser, organisationType);
    }

    public String getDbUser() {
        return dbUser;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }
}
