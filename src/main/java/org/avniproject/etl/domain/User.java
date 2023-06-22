package org.avniproject.etl.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User {
    public static final String USER = "user";
    public static final String ORGANISATION_ADMIN = "organisation_admin";
    public static final String ADMIN = "admin";

    private Long id;
    private String username;
    private String uuid;
    private Long organisationId;
    private boolean isOrgAdmin;
    private Set<AccountAdmin> accountAdmin = new HashSet<>();
    private boolean isAdmin;

    public User(String userName, String uuid, Long organisationId) {
        this.username = userName;
        this.uuid = uuid;
        this.organisationId = organisationId;
    }

    public User(Long id, String userName, String uuid, Long organisationId, boolean isOrgAdmin) {
        this.id = id;
        this.username = userName;
        this.uuid = uuid;
        this.organisationId = organisationId;
        this.isOrgAdmin = isOrgAdmin;
    }

    public String getUsername() {
        return this.username;
    }

    public String getUuid() {
        return this.uuid;
    }

    public Long getOrganisationId() { return this.organisationId; }

    public boolean isOrgAdmin() {
        return isOrgAdmin;
    }

    public void setOrgAdmin(boolean orgAdmin) {
        isOrgAdmin = orgAdmin;
    }

    public Set<AccountAdmin> getAccountAdmin() {
        return accountAdmin;
    }

    public void setAccountAdmin(Set<AccountAdmin> accountAdmin) {
        this.accountAdmin = accountAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String[] getRoles() {
        ArrayList<String> roles = new ArrayList<>();
        if (!(isOrgAdmin || isAdmin())) roles.add(USER);
        if (isAdmin()) roles.add(ADMIN);
        if (isOrgAdmin) {
            roles.add(ORGANISATION_ADMIN);
            roles.add(USER);
        }
        return roles.toArray(new String[0]);
    }
}
