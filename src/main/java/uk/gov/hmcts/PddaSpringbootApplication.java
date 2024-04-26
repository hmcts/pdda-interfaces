package uk.gov.hmcts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import uk.gov.hmcts.config.WebAppInitializer;

@SpringBootApplication
@ServletComponentScan
@EntityScan
@EnableAutoConfiguration
public class PddaSpringbootApplication extends SpringBootServletInitializer {
    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(PddaSpringbootApplication.class);
    private static final String TRUE = "true";
    
    public static void main(String[] args) {
        log.debug("Starting PDDA Springboot application...");
        final var instance =
            SpringApplication.run(new Class[] {PddaSpringbootApplication.class, WebAppInitializer.class}, args);
        if (TRUE.equalsIgnoreCase(System.getenv("STAGING"))) {
            log.info("STAGING found, closing instance");
            instance.close();
        }
    }

}
