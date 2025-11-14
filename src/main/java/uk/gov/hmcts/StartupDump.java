package uk.gov.hmcts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.stream.Stream;

@Configuration
class StartupDump {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    ApplicationRunner dumpProps(Environment env) {
        return args -> {
            // Anything that could disable JPA or affect DB:
            List<String> keys = List.of("spring.autoconfigure.exclude", "spring.jpa.database",
                "spring.jpa.hibernate.ddl-auto", "spring.datasource.url",
                "spring.datasource.username", "spring.datasource.password",
                "spring.datasource.driver-class-name", "spring.config.import");
            log.info("=== EFFECTIVE SPRING PROPERTIES ===");
            keys.forEach(k -> log.info(k + " = " + env.getProperty(k)));
            // Dump any property that starts with DB_ (from configtree)
            log.info("=== DB_* PROPERTIES (from configtree/env) ===");
            Stream.of("DB_HOST", "DB_PORT", "DB_NAME", "DB_USER_NAME", "DB_PASSWORD", "DB_SCHEMA")
                .forEach(k -> log.info(k + " = " + env.getProperty(k)));
            log.info("====================================");
        };
    }
}
