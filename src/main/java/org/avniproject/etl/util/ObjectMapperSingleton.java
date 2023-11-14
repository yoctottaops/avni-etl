package org.avniproject.etl.util;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class ObjectMapperSingleton {

    @Bean
    public JodaModule jodaModule() {
        JodaModule module = new JodaModule();
        return module;
    }
}