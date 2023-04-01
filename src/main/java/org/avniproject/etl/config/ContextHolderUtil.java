package org.avniproject.etl.config;

import org.avniproject.etl.domain.ContextHolder;
import org.avniproject.etl.domain.OrganisationIdentity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ContextHolderUtil {

    static Map<String, Object> parameters = new HashMap<>(1);

    public static void setParameters(String schemaName, String db) {
        ContextHolder.setContext(new OrganisationIdentity(db, schemaName));
        parameters.put("schema", ContextHolder.getDbSchema());
    }

    public static Map<String, Object> getParameters() {
        return parameters;
    }
}
