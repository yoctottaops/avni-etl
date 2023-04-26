package org.avniproject.etl.repository.sql;

import org.avniproject.etl.builder.OrganisationIdentityBuilder;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.dto.AddressRequest;
import org.avniproject.etl.dto.MediaSearchRequest;
import org.avniproject.etl.dto.SyncValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MediaSearchQueryBuilderTest {

    @BeforeEach
    public void setup() {
        ContextHolder.setContext(new OrganisationIdentityBuilder().withSchemaName("schema").build());
    }

    @Test
    public void shouldCreateDefaultOffsetAndLimit() {
        Query query = new MediaSearchQueryBuilder().build();
        assertThat(query.parameters(), hasEntry("offset", 0));
        assertThat(query.parameters(), hasEntry("limit", 10));
    }

    @Test
    public void shouldAddOffsetAndLimitIfProvided() {
        Query query = new MediaSearchQueryBuilder().withPage(new Page(2, 10)).build();
        assertThat(query.parameters(), hasEntry("offset", 20));
        assertThat(query.parameters(), hasEntry("limit", 10));
    }

    @Test
    public void shouldAddQueryConditionAndParameterWhenSubjectTypeNamesProvided() {
        MediaSearchRequest mediaSearchRequest = new MediaSearchRequest();
        mediaSearchRequest.setSubjectTypeNames(Arrays.asList("person"));

        Query query = new MediaSearchQueryBuilder().withMediaSearchRequest(mediaSearchRequest).build();
        System.out.println(query.sql());

        assertThat("Query should have filter condition", query.sql(), containsString("and media.subject_type_name in (:subjectTypeNames)"));
        assertThat("Query should have not have missing filter conditions", query.sql(), not(containsString(":programNames")));

        assertThat("Query condition should contain SubjectTypeName parameter", query.parameters(), hasKey("subjectTypeNames"));
        assertThat("Query condition should contain SubjectTypeName parameter", (List<String>) query.parameters().get("subjectTypeNames"), contains("person"));
    }

    @Test
    public void shouldNotAddParametersForEmptyArrays() {
        MediaSearchRequest mediaSearchRequest = new MediaSearchRequest();
        mediaSearchRequest.setProgramNames(new ArrayList<>());

        mediaSearchRequest.setProgramNames(Arrays.asList("new Program"));
        Query query = new MediaSearchQueryBuilder().withMediaSearchRequest(mediaSearchRequest).build();
        assertThat("Query should have filter condition", query.sql(), containsString("and media.program_name in (:programNames)"));
        assertThat("Query condition should not contain parameter", query.parameters(), hasKey("programNames"));

        mediaSearchRequest.setProgramNames(Collections.emptyList());
        query = new MediaSearchQueryBuilder().withMediaSearchRequest(mediaSearchRequest).build();
        assertThat("Query should not have filter condition for empty list", query.sql(), not(containsString("and media.program_name in (:programNames)")));
        assertThat("Query condition should not contain parameter for empty list", query.parameters(), not(hasKey("programNames")));
    }

    @Test
    public void shouldWorkWellForSyncParameters() {
        MediaSearchRequest mediaSearchRequest = new MediaSearchRequest();
        SyncValue syncValue1 = new SyncValue("a", List.of("b"));
        SyncValue syncValue2 = new SyncValue("c", List.of("d"));
        mediaSearchRequest.setSyncValues(Arrays.asList(syncValue1, syncValue2));

        Query query = new MediaSearchQueryBuilder().withMediaSearchRequest(mediaSearchRequest).build();
        System.out.println(query.sql());
    }

    @Test
    public void shouldWorkWellForAddresses() {
        MediaSearchRequest mediaSearchRequest = new MediaSearchRequest();
        AddressRequest address = new AddressRequest();
        address.setAddressLevelType("District");
        address.setAddressLevelIds(List.of(1, 2, 3));
        mediaSearchRequest.setAddresses(List.of(address));

        Query query = new MediaSearchQueryBuilder().withMediaSearchRequest(mediaSearchRequest).build();
        assertThat(query.sql(), containsString("address.\"District id\" in (:addressLevelIds_0)"));
        assertThat(query.parameters(), hasKey("addressLevelIds_0"));
        List<Integer> addressLevelParameter = (List<Integer>) query.parameters().get("addressLevelIds_0");
        assertThat(addressLevelParameter, is(iterableWithSize(3)));
        assertThat(addressLevelParameter, contains(1, 2, 3));
    }

    @Test
    public void shouldHandleFromDate() {
        MediaSearchRequest mediaSearchRequest = new MediaSearchRequest();
        Date fromDate = new Date();
        mediaSearchRequest.setFromDate(fromDate);

        Query query = new MediaSearchQueryBuilder().withMediaSearchRequest(mediaSearchRequest).build();
        assertThat(query.sql(), containsString("and media.created_date_time >= :fromDate"));
        assertThat(query.parameters(), hasEntry("fromDate", fromDate));
    }
}
