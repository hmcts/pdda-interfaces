package uk.gov.hmcts.framework.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class XslServicesTest {

    @Mock
    private URIResolver mockUriResolver;

    @Mock
    private TransformerFactory mockTransformerFactory;

    @InjectMocks
    private final XslServices classUnderTest = XslServices.getInstance();

    @BeforeEach
    public void setUp() {
        Mockito.mockStatic(TransformerFactory.class);
    }

    @AfterEach
    public void tearDown() {
        Mockito.clearAllCaches();
    }

    @Test
    void testGetTransformerFactory() {
        Mockito.when(TransformerFactory.newInstance()).thenReturn(mockTransformerFactory);
        // Run
        TransformerFactory result = classUnderTest.getTransformerFactory(mockUriResolver);
        assertNotNull(result, "Result is Null");
    }

}
