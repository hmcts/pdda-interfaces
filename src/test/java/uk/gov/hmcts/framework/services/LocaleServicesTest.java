package uk.gov.hmcts.framework.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.net.URL;
import java.util.Iterator;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocaleServicesTest {

    private static final String NOTNULL = "Result is Null";
    @Mock
    private LocaleServices mockLocaleServices;

    @Mock
    private URL mockUrl;

    @InjectMocks
    private final LocaleServices classUnderTest = LocaleServices.getInstance();


    @BeforeEach
    public void setUp() {
     // Do nothing
    }

    @AfterEach
    public void tearDown() {
        // Do nothing
    }

    @Test
    void testGetResource() {
        Assertions.assertThrows(RuntimeException.class, () -> {

            classUnderTest.getResource(Locale.UK, "name.txt");
        });
    }


    @Test
    void testOpenStream() {
        Assertions.assertThrows(RuntimeException.class, () -> {

            classUnderTest.openStream(Locale.UK, "name.txt");
        });
    }

    @Test
    void testGetBaseName() {
        String result = classUnderTest.getBaseName(Locale.UK, "name.txt");
        assertNotNull(result, NOTNULL);
        result = classUnderTest.getBaseName(Locale.UK, "name");
        assertNotNull(result, NOTNULL);
    }
    
    @Test
    void testGetCandidates() {
        Iterator<Object> result = classUnderTest.getCandidates(Locale.UK, "name.txt");
        assertNotNull(result, NOTNULL);
        result = classUnderTest.getCandidates(Locale.UK, "name");
        assertNotNull(result, NOTNULL);
    }
}
