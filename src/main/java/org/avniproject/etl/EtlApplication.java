package org.avniproject.etl;

import org.avniproject.etl.service.EtlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
//Pesky little thing runs every time an integration test is added. This property is the only thing
//that can turn it off
@ConditionalOnProperty(
		prefix = "application.runner",
		value = "enabled",
		havingValue = "true",
		matchIfMissing = true)
public class EtlApplication implements CommandLineRunner {
	public static final int INT_CONSTANT_ZERO = 0;
	public static final int ORG_SCHEMA_NAMES_INDEX = 0;
	private List<String> organisationSchemaNameFilter = new ArrayList<>();
	public static final String ORG_SCHEMA_NAMES = "orgSchemaNames";
	public static final String SCHEMA_NAME_SEPARATOR = "\\s*,\\s*";
	private static final Logger log = LoggerFactory.getLogger(EtlApplication.class);

	private final EtlService etlService;

	public EtlApplication(EtlService etlService) {
		this.etlService = etlService;
	}

	public static void main(String[] args) {
		SpringApplication.run(EtlApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Starting ETL job");
 		extractJobParametersFromArgs(args);
		etlService.runForOrganisationSchemaNames(organisationSchemaNameFilter);
		log.info("ETL job complete");
	}

	private void extractJobParametersFromArgs(String... args) {
		String orgSchemaNames = args != null && args.length > INT_CONSTANT_ZERO ?
				args[ORG_SCHEMA_NAMES_INDEX]:null;
		if(StringUtils.hasText(orgSchemaNames)) {
			organisationSchemaNameFilter = Arrays.asList(orgSchemaNames.split(SCHEMA_NAME_SEPARATOR));
			log.info("ETL job will be run only for following schemas: "+ organisationSchemaNameFilter);
		}
	}
}
