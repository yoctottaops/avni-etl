package org.avniproject.etl.domain;

import java.util.ArrayList;

public class User {
    public static final String USER = "user";
    public static final String ANALYTICS_USER = "analytics_user";
    public static final String ADMIN = "admin";

    private Long id;
    private String username;
    private String uuid;
    private Long organisationId;
    private boolean hasAnalyticsAccess;
    private boolean isAdmin;

    public User(String userName, String uuid, Long organisationId) {
        this.username = userName;
        this.uuid = uuid;
        this.organisationId = organisationId;
    }

    public User(Long id, String username, String uuid, Long organisationId, boolean hasAnalyticsAccess, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.uuid = uuid;
        this.organisationId = organisationId;
        this.hasAnalyticsAccess = hasAnalyticsAccess;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return this.username;
    }

    public String getUuid() {
        return this.uuid;
    }

    public Long getOrganisationId() { return this.organisationId; }

    public boolean isHasAnalyticsAccess() {
        return hasAnalyticsAccess;
    }

    public void setHasAnalyticsAccess(boolean hasAnalyticsAccess) {
        this.hasAnalyticsAccess = hasAnalyticsAccess;
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
        roles.add(USER);
        if (isHasAnalyticsAccess()) roles.add(ANALYTICS_USER);
        if (isAdmin()) roles.add(ADMIN);
        return roles.toArray(new String[0]);
    }
}
