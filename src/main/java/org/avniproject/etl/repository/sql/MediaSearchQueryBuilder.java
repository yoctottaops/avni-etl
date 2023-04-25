package org.avniproject.etl.repository.sql;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.dto.MediaSearchRequest;
import org.stringtemplate.v4.ST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.avniproject.etl.repository.sql.SqlFile.readFile;

public class MediaSearchQueryBuilder {
    private final ST template;
    private final Map<String, Object> parameters = new HashMap<>();

    public MediaSearchQueryBuilder() {
        this.template = new ST(readFile("/sql/api/media.sql"));
        addDefaultParameters();
    }

    public MediaSearchQueryBuilder withMediaSearchRequest(MediaSearchRequest request) {
        template.add("request", request);
        addParameters(request);
        return this;
    }

    private void addParameters(MediaSearchRequest request) {
        addParameter("subjectTypeNames", request.getSubjectTypeNames());
        addParameter("programNames", request.getProgramNames());
        addParameter("encounterTypeNames", request.getEncounterTypeNames());
        addParameter("imageConcepts", request.getImageConcepts());
    }

    public MediaSearchQueryBuilder withPage(Page page) {
        parameters.put("offset", page.offset());
        parameters.put("limit", page.limit());
        return this;
    }

    public Query build() {
        return new Query(template.render(), parameters);
    }

    private void addParameter(String key, List value) {
        if (value != null && !value.isEmpty()) {
            parameters.put(key, value);
        }
    }

    private void addDefaultParameters() {
        template.add("schemaName", ContextHolder.getDbSchema());
        parameters.put("offset", 0);
        parameters.put("limit", 10);
    }
}
