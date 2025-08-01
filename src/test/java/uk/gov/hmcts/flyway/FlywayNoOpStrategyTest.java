package uk.gov.hmcts.flyway;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.flywaydb.core.internal.info.MigrationInfoImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class FlywayNoOpStrategyTest {
    
    private static final String TRUE = "Result is not True";

    @Mock
    private Flyway mockFlyway;

    @Mock
    private MigrationInfoService mockMigrationInfoService;

    @Mock
    private MigrationInfoImpl mockMigrationInfoImpl;


    @TestSubject
    private final FlywayNoOpStrategy classUnderTest = new FlywayNoOpStrategy();

    @Test
    void testMigrate() {
        // Setup
        MigrationInfo[] migrationInfo = {mockMigrationInfoImpl};
        // Expects
        EasyMock.expect(mockFlyway.info()).andReturn(mockMigrationInfoService);
        EasyMock.expect(mockMigrationInfoService.all()).andReturn(migrationInfo);
        EasyMock.expect(mockMigrationInfoImpl.getState()).andReturn(MigrationState.AVAILABLE);
        EasyMock.expect(mockMigrationInfoImpl.getScript()).andReturn("");
        // Replay
        EasyMock.replay(mockFlyway);
        EasyMock.replay(mockMigrationInfoService);
        EasyMock.replay(mockMigrationInfoImpl);

        // Run
        Assertions.assertThrows(PendingMigrationScriptException.class, () -> {
            classUnderTest.migrate(mockFlyway);
        });
    }

    @Test
    void testMigrateNotAvailable() {
        // Setup
        MigrationInfo[] migrationInfo = {mockMigrationInfoImpl};
        // Expects
        EasyMock.expect(mockFlyway.info()).andReturn(mockMigrationInfoService);
        EasyMock.expect(mockMigrationInfoService.all()).andReturn(migrationInfo);
        EasyMock.expect(mockMigrationInfoImpl.getState()).andReturn(MigrationState.FAILED);
        EasyMock.expect(mockMigrationInfoImpl.getScript()).andReturn("");
        // Replay
        EasyMock.replay(mockFlyway);
        EasyMock.replay(mockMigrationInfoService);
        EasyMock.replay(mockMigrationInfoImpl);

        // Run
        boolean result = false;
        try {
            classUnderTest.migrate(mockFlyway);
            result = true;
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
        assertTrue(result, TRUE);
    }
}
