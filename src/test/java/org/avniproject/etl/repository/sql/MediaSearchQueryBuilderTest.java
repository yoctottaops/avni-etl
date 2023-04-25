package org.avniproject.etl.repository.sql;

import org.avniproject.etl.builder.OrganisationIdentityBuilder;
import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.dto.MediaSearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        assertThat("Query should have filter condition", query.sql(), containsString("and media.subject_type_name in :subjectTypeNames"));
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
        assertThat("Query should have filter condition", query.sql(), containsString("and media.program_name in :programNames"));
        assertThat("Query condition should not contain parameter", query.parameters(), hasKey("programNames"));

        mediaSearchRequest.setProgramNames(Collections.emptyList());
        query = new MediaSearchQueryBuilder().withMediaSearchRequest(mediaSearchRequest).build();
        assertThat("Query should not have filter condition for empty list", query.sql(), not(containsString("and media.program_name in :programNames")));
        assertThat("Query condition should not contain parameter for empty list", query.parameters(), not(hasKey("programNames")));
    }
}
