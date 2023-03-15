package org.avniproject.etl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@SpringBootApplication
//Pesky little thing runs every time an integration test is added. This property is the only thing
//that can turn it off
@ConditionalOnProperty(
		prefix = "application.runner",
		value = "enabled",
		havingValue = "true",
		matchIfMissing = true)
public class EtlApplication {
	private static final Logger log = LoggerFactory.getLogger(EtlApplication.class);

	public EtlApplication() {
	}

	public static void main(String[] args) {
		SpringApplication.run(EtlApplication.class, args);
	}
}
