package org.avniproject.etl.builder;

import org.avniproject.etl.domain.OrganisationIdentity;

public class OrganisationIdentityBuilder {
    private Integer id = 1;
    private String dbUser = "dbUser";
    private final String schemaName = "schema";
    private final OrganisationIdentity.OrganisationType organisationType = OrganisationIdentity.OrganisationType.Organisation;

    public OrganisationIdentityBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public OrganisationIdentity build() {
        return new OrganisationIdentity(id, dbUser, schemaName, organisationType);
    }

    public OrganisationIdentityBuilder withDbUser(String dbUser) {
        this.dbUser = dbUser;
        return this;
    }
}
