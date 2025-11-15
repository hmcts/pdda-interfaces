package uk.gov.hmcts.pdda.courtlog.helpers.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogCrudValue;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.XmlServices;
import uk.gov.hmcts.pdda.courtlog.exceptions.CourtLogRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"unchecked", "PMD"})
class CourtLogXmlHelperTest {

    private static final String NULL = "Result is null";
    private static final String EVENT_DATA = "<event><data/></event>";

    @Mock
    private CourtLogCrudValue crudValueMock;

    @Test
    void testGetXmlValid() {
        when(crudValueMock.getEventType()).thenReturn(42);
        when(crudValueMock.getEntryFreeText()).thenReturn("Some text");
        Map<String, Object> propertyMap = new ConcurrentHashMap<>();
        when(crudValueMock.getPropertyMap()).thenReturn(propertyMap);

        CourtLogMarshaller marshaller = mock(CourtLogMarshaller.class);
        when(marshaller.marshall(Mockito.anyMap(), Mockito.anyString())).thenReturn(EVENT_DATA);

        // Call method under test
        String xml = CourtLogXmlHelper.getXml(crudValueMock);

        assertNotNull(xml, NULL);
    }

    @Test
    void testGetXmlTriggersAddSchemaBlock() {
        // Arrange
        when(crudValueMock.getEventType()).thenReturn(42);
        when(crudValueMock.getEntryFreeText()).thenReturn("Some text");
        when(crudValueMock.getPropertyMap()).thenReturn(new ConcurrentHashMap<>());

        // Mock constructor so any new CourtLogMarshaller returns our controlled mock
        try (MockedConstruction<CourtLogMarshaller> mocked =
            Mockito.mockConstruction(CourtLogMarshaller.class,
                (mock, context) -> when(mock.marshall(Mockito.anyMap(), Mockito.anyString()))
                    .thenReturn(EVENT_DATA))) {
            // Act
            String xml = CourtLogXmlHelper.getXml(crudValueMock);

            // Assert
            assertNotNull(xml, NULL);
            assertTrue(xml.contains("xmlns:xsi="),
                "Schema attributes should be added when <event> exists");
            assertTrue(xml.contains("42.xsd"), "Schema location should reference event type");
            // Confirm our mock marshaller was actually used
            Mockito.verify(mocked.constructed().get(0)).marshall(Mockito.anyMap(),
                Mockito.anyString());
        }
    }

    @Test
    void testValidateXmlDelegatesToXmlServices() {
        String sampleXml = "<event></event>";
        Integer eventType = 5;

        XmlServices mockXmlServices = mock(XmlServices.class);
        try (MockedStatic<CsServices> csMock = Mockito.mockStatic(CsServices.class)) {
            csMock.when(CsServices::getXmlServices).thenReturn(mockXmlServices);

            CourtLogXmlHelper.validateXml(sampleXml, eventType);
        }
    }

    @Test
    void testGetPropertySetParsesXmlIntoMap() {
        String xml = "<event><key>value</key></event>";
        Map<String, Object> mockMap = new ConcurrentHashMap<>();
        mockMap.put("key", "value");
        try (MockedConstruction<CourtLogMarshaller> mocked =
            Mockito.mockConstruction(CourtLogMarshaller.class, (mock,
                context) -> when(mock.unmarshall(Mockito.any(String.class))).thenReturn(mockMap))) {
            Map<?, ?> result = CourtLogXmlHelper.getPropertySet(xml);
            assertNotNull(result);
            assertEquals("value", result.get("key"));
            Mockito.verify(mocked.constructed().get(0)).unmarshall(xml);
        }
    }

    @Test
    void testCreateDocumentFromInputStreamBuildsDocument() throws Exception {
        String xml = "<event><data>ok</data></event>";
        try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            Document doc = CourtLogXmlHelper.createDocument(inputStream);
            assertNotNull(doc);
            assertEquals("event", doc.getDocumentElement().getTagName());
        }
    }

    @Test
    void testCreateDocumentFromStringBuildsDocument() {
        String xml = "<event><data>ok</data></event>";
        Document doc = CourtLogXmlHelper.createDocument(xml);
        assertNotNull(doc);
        assertEquals("event", doc.getDocumentElement().getTagName());
    }

    @Test
    void testCreateDocumentThrowsCourtLogRuntimeExceptionOnParseError() {
        String invalidXml = "<event><data></event>"; // malformed

        assertThrows(CourtLogRuntimeException.class,
            () -> CourtLogXmlHelper.createDocument(invalidXml));
    }

    @Test
    void testCreateDocumentFromStreamThrowsCourtLogRuntimeExceptionOnParseError() throws IOException {
        byte[] invalidXml = "<event><data></event>".getBytes(StandardCharsets.UTF_8);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidXml)) {
            assertThrows(CourtLogRuntimeException.class,
                () -> CourtLogXmlHelper.createDocument(inputStream));
        }
    }
}
