package org.avniproject.etl.repository.sql;

import java.util.Map;

public record Query (String sql, Map<String, Object> parameters){}
