package org.avniproject.etl.repository.dynamicInsert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SqlFile {
    private static final Logger log = LoggerFactory.getLogger(SqlFile.class);

    public static String readFile(String path) {
        try {
            return new BufferedReader(new InputStreamReader(new ClassPathResource(path).getInputStream())).lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error(String.format("Cannot read file at %s", path), e);
            throw new RuntimeException(e);
        }
    }
}
