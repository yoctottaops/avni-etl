package org.avniproject.etl;

import org.avniproject.etl.domain.OrganisationIdentity;
import org.avniproject.etl.service.EtlService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataSyncIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EtlService etlService;

    private static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(toDate(LocalDateTime.now().minus(Duration.ofSeconds(10))));
    }

    private String getCurrentTime(long subtractSeconds) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(toDate(LocalDateTime.now().minus(Duration.ofSeconds(10)).minus(Duration.ofSeconds(subtractSeconds))));
    }

    private void runDataSync() {
        etlService.runForOrganisation(new OrganisationIdentity("orgc", "orgc"));
    }

    private List<Map<String, Object>> getPersons() {
        return jdbcTemplate.queryForList("select * from orgc.person;");
    }

    private Map<String, Object> getPersonById(Integer id) {
        return jdbcTemplate.queryForMap(format("select * from orgc.person where id = %d", id));
    }

    private void addApprovalStatus(Integer id, String uuid, Integer statusId, String dateTime) {
        jdbcTemplate.execute(format("insert into entity_approval_status (id, uuid, entity_id, entity_type, approval_status_id, approval_status_comment,organisation_id, audit_id, version, status_date_time, created_by_id, last_modified_by_id, created_date_time, last_modified_date_time) values (%d, '%s', 574170, 'Subject', %d, null, 12, create_audit(), 0,  '%s', 1,1, '%s', '%s')", id, uuid, statusId, dateTime, dateTime, dateTime));
    }


    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addingNewFormElementAddsNewColumn() {
        runDataSync();
        assertThat(Objects.equals(getPersons().get(0).get("New concept"), new BigDecimal(123)), is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void editingOlderRecordWillUpdateTheTable() {
        jdbcTemplate.execute("update individual set observations = observations || jsonb_build_object('01ee7e6b-6184-498d-bd54-dbe16e2f66dd', 456), last_modified_date_time = '2022-04-13 10:51:44.705 +00:00' where subject_type_id = 339;");
        runDataSync();
        assertThat(Objects.equals(getPersons().get(0).get("New concept"), new BigDecimal(456)), is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addingNewRecordWillAddOneRow() {
        runDataSync();
        assertThat(Objects.equals(getPersons().size(), 1), is(true));
        String newPersonQuery = format("INSERT INTO individual (id, uuid, address_id, observations, version, date_of_birth, date_of_birth_verified, gender_id, registration_date, organisation_id, first_name, last_name, is_voided, audit_id, facility_id, registration_location, subject_type_id, legacy_id, created_by_id, last_modified_by_id, created_date_time, last_modified_date_time, sync_concept_1_value, sync_concept_2_value) VALUES (574172, '751bb8c8-ef18-4250-a73d-73106e7a5b66', 107786, '{}', 0, '2001-04-05', false, 389, '2022-04-05', 12, 'New', 'Person', false, create_audit(), null, null, 339, null, 1, 1, '%s', '%s', null, null);", getCurrentTime(), getCurrentTime());
        jdbcTemplate.execute(newPersonQuery);
        runDataSync();
        assertThat(Objects.equals(getPersons().size(), 2), is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void changingConceptNameChangesColumnName() {
        jdbcTemplate.execute(format("update concept set name = 'Changed Concept', last_modified_date_time = '%s' where name = 'New concept'", getCurrentTime()));
        runDataSync();
        List<Map<String, Object>> persons = getPersons();
        assertThat(persons.get(0).containsKey("New concept"), is(false));
        assertThat(persons.get(0).containsKey("Changed Concept"), is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void changingConceptAnswerShouldUpdateTheOldData() {
        runDataSync();
        Map<String, Object> person = getPersonById(574170);
        assertThat(Objects.equals(person.get("Single Select Question"), "Alpha"), is(true));
        jdbcTemplate.execute(format("update concept set name = 'alpha', last_modified_date_time = '%s' where name = 'Alpha'", getCurrentTime()));
        jdbcTemplate.execute(format("insert into answer_concept_migration (id, uuid, concept_id, old_answer_concept_name, new_answer_concept_name,organisation_id, version, created_by_id, last_modified_by_id, created_date_time, last_modified_date_time) values (100, 'babf6656-c731-414a-96cd-30ebd00c6bfc', 107565, 'Alpha', 'alpha', 12, 0, 1, 1, '%s', '%s')", getCurrentTime(), getCurrentTime()));
        runDataSync();
        Map<String, Object> updatedPerson = getPersonById(574170);
        assertThat(Objects.equals(updatedPerson.get("Single Select Question"), "alpha"), is(true));
    }


    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void multipleChangesOfConceptAnswersShouldBeRunOneAfterAnother() {
        jdbcTemplate.execute(format("update concept set name = 'secondChange', last_modified_date_time = '%s' where name = 'Alpha'", getCurrentTime()));
        jdbcTemplate.execute(format("insert into answer_concept_migration (id, uuid, concept_id, old_answer_concept_name, new_answer_concept_name,organisation_id, version, created_by_id, last_modified_by_id, created_date_time, last_modified_date_time) values (100, 'babf6656-c731-414a-96cd-30ebd00c6bfd', 107565, 'Alpha', 'firstChange', 12, 0, 1, 1, '%s', '%s')", getCurrentTime(), getCurrentTime(1)));
        jdbcTemplate.execute(format("insert into answer_concept_migration (id, uuid, concept_id, old_answer_concept_name, new_answer_concept_name,organisation_id, version, created_by_id, last_modified_by_id, created_date_time, last_modified_date_time) values (101, 'babf6656-c731-414a-96cd-30ebd00c6bfc', 107565, 'firstChange', 'secondChange', 12, 0, 1, 1, '%s', '%s')", getCurrentTime(), getCurrentTime()));
        runDataSync();
        Map<String, Object> updatedPerson = getPersonById(574170);
        assertThat(Objects.equals(updatedPerson.get("Single Select Question"), "secondChange"), is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void changingConceptAnswerShouldUpdateTheOldMultiSelectData() {
        runDataSync();
        Map<String, Object> person = getPersonById(574170);
        assertThat(Objects.equals(person.get("Multi Select Question"), "Delta, Kappa"), is(true));
        jdbcTemplate.execute(format("update concept set name = 'kappa', last_modified_date_time = '%s' where name = 'Kappa'", getCurrentTime()));
        jdbcTemplate.execute(format("insert into answer_concept_migration (id, uuid, concept_id, old_answer_concept_name, new_answer_concept_name,organisation_id, version, created_by_id, last_modified_by_id, created_date_time, last_modified_date_time) values (101, 'babf6656-c731-414a-96cd-30ebd00c3bfc', 107569, 'Kappa', 'kappa', 12, 0, 1, 1, '%s', '%s')", getCurrentTime(), getCurrentTime()));
        runDataSync();
        Map<String, Object> updatedPerson = getPersonById(574170);
        assertThat(Objects.equals(updatedPerson.get("Multi Select Question"), "Delta, kappa"), is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void latestApprovalStatusShouldGetAdded() {
        addApprovalStatus(100, "5f169db3-8c30-4647-ad60-181a4b4728f8", 1, "2022-04-05 11:47:04.749000");
        addApprovalStatus(101, "55195e01-9bcd-4492-a8d3-e59fff1e3a91", 2, "2022-04-05 12:47:04.749000");
        addApprovalStatus(102, "946ee24d-5b2e-4664-baca-e7477a245259", 3, "2022-04-05 13:47:04.749000");
        runDataSync();
        Map<String, Object> person = getPersonById(574170);
        assertThat(Objects.equals(person.get("latest_approval_status"), "Rejected"), is(true));
        addApprovalStatus(103, "5df91bc6-7e1a-42b8-9520-d420d2aadb39", 2, "2022-04-05 14:47:04.749000");
        runDataSync();
        Map<String, Object> newPerson = getPersonById(574170);
        assertThat(Objects.equals(newPerson.get("latest_approval_status"), "Approved"), is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void groupFormElementGetsAddedAsColumns() {
        runDataSync();
        Map<String, Object> person = getPersonById(574170);
        assertThat(person.containsKey("Question group Child 1"), is(true));
        assertThat(person.containsKey("Question group Child 2"), is(true));
        assertThat(Objects.equals(person.get("Question group Child 1"), "value 1"), is(true));
        assertThat(Objects.equals(person.get("Question group Child 2"), "value 2"), is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addressLevelColumnsAreCreated() {
        runDataSync();
        List<Map<String, Object>> addresses = jdbcTemplate.queryForList("select * from orgc.address;");
        Map<String, Object> address = addresses.get(0);
        assertThat(address.containsKey("District"), is(true));
        assertThat(address.containsKey("Block"), is(true));
        assertThat(address.containsKey("Gram Panchayat"), is(true));
        assertThat(addresses.size() == 6, is(true));
    }

    @Test
    @Sql({"/test-data-teardown.sql", "/test-data.sql", "/new-form-element.sql"})
    @Sql(scripts = "/test-data-teardown.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void allTablesHaveAddressIdAndIndividualIdColumns() {
        runDataSync();
        List<Map<String, Object>> enrolments = jdbcTemplate.queryForList("select * from orgc.person_nutrition;");
        assertThat(enrolments.size() == 2, is(true));
        List<Map<String, Object>> exits = jdbcTemplate.queryForList("select * from orgc.person_nutrition_exit;");
        assertThat(exits.size() == 1, is(true));
        List<Map<String, Object>> programEncounters = jdbcTemplate.queryForList("select * from orgc.person_nutrition_growth_monitoring;");
        assertThat(programEncounters.size() == 1, is(true));
        List<Map<String, Object>> programEncounterCancels = jdbcTemplate.queryForList("select * from orgc.person_nutrition_growth_monitoring_cancel;");
        assertThat(programEncounterCancels.size() == 1, is(true));
        List<Map<String, Object>> encounters = jdbcTemplate.queryForList("select * from orgc.person_general_encounter;");
        assertThat(encounters.size() == 1, is(true));
        List<Map<String, Object>> encounterCancels = jdbcTemplate.queryForList("select * from orgc.person_general_encounter_cancel;");
        assertThat(encounterCancels.size() == 1, is(true));
        Arrays.asList(enrolments, programEncounters, encounters, exits, programEncounterCancels, encounterCancels).forEach(entity -> {
            assertThat(entity.get(0).containsKey("individual_id"), is(true));
            assertThat(entity.get(0).containsKey("address_id"), is(true));
        });
    }

}
