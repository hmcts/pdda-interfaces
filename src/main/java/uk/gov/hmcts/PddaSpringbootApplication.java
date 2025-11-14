package uk.gov.hmcts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.config.WebAppInitializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
@ServletComponentScan
@EntityScan
@EnableAutoConfiguration
public class PddaSpringbootApplication extends SpringBootServletInitializer {
    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(PddaSpringbootApplication.class);

    public static void main(String[] args) {
        log.debug("Starting PDDA Springboot application...");
        SpringApplication
            .run(new Class[] {PddaSpringbootApplication.class, WebAppInitializer.class}, args);
    }

    @Bean
    ApplicationRunner logSecretsDir(Environment env, DataSourceProperties dsp) {
        return args -> {
            Path dir = Paths.get("/mnt/secrets/pdda");
            if (Files.exists(dir)) {
                try (Stream<Path> strPath = Files.list(dir)) {
                    strPath.forEach(p -> {
                        try {
                            long size = Files.isRegularFile(p) ? Files.size(p) : -1;
                            log.info("Secret entry present: {} ({} bytes)", p.getFileName(), size);
                        } catch (Exception e) {
                            log.warn("Could not stat {}", p, e);
                        }
                    });
                }
            } else {
                log.warn("Secrets dir NOT found: {}", dir);
            }

            // Show key resolved properties (masked)
            log.info("Active profiles: {}", String.join(",", env.getActiveProfiles()));
            log.info("DB url (from DataSourceProperties): {}", dsp.getUrl());
            log.info("DB username present: {}", dsp.getUsername() != null);
            log.info("Env DB_HOST: {}", env.getProperty("DB_HOST"));
            log.info("Env DB_NAME: {}", env.getProperty("DB_NAME"));
            log.info("Env DB_SCHEMA present: {}", env.containsProperty("DB_SCHEMA"));
        };
    }
}
