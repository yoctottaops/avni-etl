package org.avniproject.etl.builder;

import org.avniproject.etl.domain.OrganisationIdentity;

public class OrganisationIdentityBuilder {
    private Integer id = 1;
    private String dbUser = "dbUser";
    private String schemaName = "schema";

    public OrganisationIdentityBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public OrganisationIdentity build() {
        return new OrganisationIdentity(dbUser, schemaName);
    }

    public OrganisationIdentityBuilder withDbUser(String dbUser) {
        this.dbUser = dbUser;
        return this;
    }

    public OrganisationIdentityBuilder withSchemaName(String schemaName) {
        this.schemaName = schemaName;
        return this;
    }
}
