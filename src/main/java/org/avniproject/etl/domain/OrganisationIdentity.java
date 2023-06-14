package org.avniproject.etl.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrganisationIdentity {
    private final String dbUser;
    private final String schemaName;
    private final String schemaUser;
    private List<String> orgGroupOrgDbUsers;

    private OrganisationIdentity(String dbUser, String schemaName, String schemaUser) {
        this.dbUser = dbUser;
        this.schemaName = schemaName;
        this.schemaUser = schemaUser;
    }

    /**
     * @param dbUser The database user who has access the source data of the organisation and is also a schema user
     * @param schemaName The destination schema name to be created/updated
     */
    public static OrganisationIdentity createForOrganisation(String dbUser, String schemaName) {
        return new OrganisationIdentity(dbUser, schemaName, dbUser);
    }

    /**
     * @param dbUser The database user who has access the source data of the organisation
     * @param schemaName The destination schema name to be created/updated
     * @param schemaUser The destination user who will have access to the schema
     */
    public static OrganisationIdentity createForOrganisationGroup(String dbUser, String schemaName, String schemaUser) {
        return new OrganisationIdentity(dbUser, schemaName, schemaUser);
    }

    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public String toString() {
        return String.format("Schema: %s, DB User: %s, Schema User: %s", schemaName, dbUser, schemaUser);
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getSchemaUser() {
        return schemaUser;
    }

    public List<String> getUsersWithSchemaAccess() {
        if (dbUser.equals(schemaUser)) return Collections.singletonList(dbUser);
        List<String> dbUsers = new ArrayList<>();
        dbUsers.add(this.schemaUser);
        dbUsers.addAll(this.orgGroupOrgDbUsers);
        return dbUsers;
    }

    public void setOrgGroupOrgDbUsers(List<String> orgGroupOrgDbUsers) {
        this.orgGroupOrgDbUsers = orgGroupOrgDbUsers;
    }
}
