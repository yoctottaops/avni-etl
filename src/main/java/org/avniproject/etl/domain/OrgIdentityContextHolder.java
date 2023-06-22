package org.avniproject.etl.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class OrgIdentityContextHolder {
    private static final ThreadLocal<OrganisationIdentity>  organisationIdentity = new ThreadLocal<>();
    private static Date startTime;


    public void setOrganisationIdentity(OrganisationIdentity organisationIdentity) {
        OrgIdentityContextHolder.organisationIdentity.set(organisationIdentity);
    }

    private OrgIdentityContextHolder() {
    }

    public static void setContext(OrganisationIdentity identity) {
        organisationIdentity.set(identity);
        startTime = toDate(LocalDateTime.now().minus(Duration.ofSeconds(10)));
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
        return startTime;
    }

    public static void clear() {
        organisationIdentity.remove();
    }
}
