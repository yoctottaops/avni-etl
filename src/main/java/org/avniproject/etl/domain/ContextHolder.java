package org.avniproject.etl.domain;

public class ContextHolder {
    private static ThreadLocal<OrganisationIdentity>  organisationIdentity = new ThreadLocal<>();

    public void setOrganisationIdentity(OrganisationIdentity organisationIdentity) {
        this.organisationIdentity.set(organisationIdentity);
    }

    private ContextHolder() {
    }

    public static void create(OrganisationIdentity context) {
        organisationIdentity.set(context);
    }

    public static OrganisationIdentity getOrganisationIdentity() {
        return organisationIdentity.get();
    }

    public static String getDbUser() {
        return organisationIdentity.get().getDbUser();
    }

    public static String getDbSchema() {
        return organisationIdentity.get().getSchemaName();
    }
}
