package uk.gov.hmcts.flyway;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(EasyMockExtension.class)
class FlywayConfigurationTest {

    @TestSubject
    private final FlywayConfiguration classUnderTest = new FlywayConfiguration();

    @Test
    void testFlywayMigrationStrategy() {
        FlywayMigrationStrategy result = classUnderTest.flywayMigrationStrategy();
        assertNotNull(result, "Result is Null");
    }
}
