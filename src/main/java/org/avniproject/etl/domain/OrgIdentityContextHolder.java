package org.avniproject.etl.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class OrgIdentityContextHolder {
    private static final ThreadLocal<OrganisationIdentity>  organisationIdentity = new ThreadLocal<>();

    public void setOrganisationIdentity(OrganisationIdentity organisationIdentity) {
        OrgIdentityContextHolder.organisationIdentity.set(organisationIdentity);
    }

    private OrgIdentityContextHolder() {
    }

    public static void setContext(OrganisationIdentity identity) {
        identity.setStartTime(toDate(LocalDateTime.now().minus(Duration.ofSeconds(10))));
        organisationIdentity.set(identity);
    }

    private static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static OrganisationIdentity getOrganisationIdentity() {
        return organisationIdentity.get();
    }

    public static String getDbUser() {
        return organisationIdentity.get().getDbUser();
    }

    public static String getSchemaUser() {
        return organisationIdentity.get().getSchemaUser();
    }

    public static String getDbSchema() {
        return organisationIdentity.get().getSchemaName();
    }

    public static Date dataSyncBoundaryTime() {
        return organisationIdentity.get().getStartTime();
    }

    public static void clear() {
        organisationIdentity.remove();
    }
}
