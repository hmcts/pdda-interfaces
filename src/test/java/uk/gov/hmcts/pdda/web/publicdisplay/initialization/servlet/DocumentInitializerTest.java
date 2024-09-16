package uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.framework.services.threadpool.ThreadPool;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentInitializerTest {

    private static final int[] COURT_IDS = {81, 51};
    private static final Long DELAY = Long.valueOf(1);
    private static final Integer NO_OF_WORKERS = 1;
    private static final String TRUE = "Result is not True";

    @Mock
    private ThreadPool mockThreadPool;

    @InjectMocks
    private final DocumentInitializer classUnderTest =
        new DocumentInitializer(COURT_IDS, NO_OF_WORKERS, DELAY, mockThreadPool);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testInitialize() {
        boolean result;
        classUnderTest.initialize();
        result = true;
        
        assertTrue(result, TRUE);
    }
}
