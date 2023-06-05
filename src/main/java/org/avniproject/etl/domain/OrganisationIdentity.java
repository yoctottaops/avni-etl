package org.avniproject.etl.domain;

import java.util.ArrayList;
import java.util.List;

public class OrganisationIdentity {
    private final String dbUser;
    private final String schemaName;
    private final List<String> groupDbUsers = new ArrayList<>();

    public OrganisationIdentity(String dbUser, String schemaName) {
        this.dbUser = dbUser;
        this.schemaName = schemaName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public String toString() {
        return String.format("Schema: %s DB User: %s", schemaName, dbUser);
    }

    public String getDbUser() {
        return dbUser;
    }

    public List<String> getGroupDbUsers() {
        return groupDbUsers;
    }

    public void setGroupDbUsers(List<String> groupDbUsers) {
        this.groupDbUsers.addAll(groupDbUsers);
    }
}
