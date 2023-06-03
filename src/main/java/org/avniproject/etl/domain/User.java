package org.avniproject.etl.domain;

public class User {
    public static final String USER = "Somthing";
    public static final String ORGANISATION_ADMIN = "sdasdasd";
    public static final String ADMIN = "asdasdasd";
    private final String userName;
    private final String uuid;
    private final Long organisationId;

    public User(String userName, String uuid, Long organisationId) {
        this.userName = userName;
        this.uuid = uuid;
        this.organisationId = organisationId;
    }
    public String getUsername() {
        return this.userName;
    }

    public String getUuid() {
        return this.uuid;
    }

    public Long getOrganisationId() { return this.organisationId; }

    public String[] getRoles() {
        String[] emptyArray = {};
        return emptyArray;
    }
}
