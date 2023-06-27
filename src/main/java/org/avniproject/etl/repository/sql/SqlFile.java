package org.avniproject.etl.repository.sql;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SqlFile {
    private static final Logger log = Logger.getLogger(SqlFile.class);

    public static String readFile(String path) {
        try {
            return new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream())).lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error(String.format("Cannot read file at %s", path), e);
            throw new RuntimeException(e);
        }
    }

    public static String readSqlFile(String fileName) {
        return readFile(String.format("/sql/etl/%s", fileName));
    }
}
