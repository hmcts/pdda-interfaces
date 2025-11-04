package uk.gov.hmcts.pdda.courtlog.helpers.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogCrudValue;
import uk.gov.hmcts.framework.services.XmlServices;
import uk.gov.hmcts.pdda.courtlog.exceptions.CourtLogRuntimeException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"PMD.AvoidAccessibilityAlteration"})
class CourtLogXmlHelperTest {
    
    private static final String NULL = "Result is null";
    private static final String NOT_EQUAL = "Result is not equal";
    
    private static final String EVENT_DATA = "<event><data/></event>";
    
    @Mock
    private CourtLogMarshaller marshallerMock;

    @Mock
    private CourtLogCrudValue crudValueMock;

    @Mock
    private XmlServices xmlServicesMock;

    @Test
    void testGetXmlValid() {
        when(crudValueMock.getEventType()).thenReturn(42);
        when(crudValueMock.getEntryFreeText()).thenReturn("Some text");
        Map<String, Object> propertyMap = new ConcurrentHashMap<>();
        when(crudValueMock.getPropertyMap()).thenReturn(propertyMap);

        // Mocking marshaller behavior
        CourtLogMarshaller marshaller = mock(CourtLogMarshaller.class);
        when(marshaller.marshall(Mockito.anyMap(), Mockito.anyString())).thenReturn(EVENT_DATA);

        // Call method under test
        String xml = CourtLogXmlHelper.getXml(crudValueMock);

        assertNotNull(xml, NULL);
    }

    @Test
    void testAddSchemaInsertTag() throws Exception {
        Method method = CourtLogXmlHelper.class.getDeclaredMethod("addSchema", String.class, Integer.class);
        method.setAccessible(true);

        String xml = EVENT_DATA;
        String result = (String) method.invoke(null, xml, 1001);

        assertNotNull(result, NULL);
    }

    @Test
    void testAddSchemaInvalid() throws Exception {
        Method method = CourtLogXmlHelper.class.getDeclaredMethod("addSchema", String.class, Integer.class);
        method.setAccessible(true);

        String xml = "<root><data/></root>";
        String result = (String) method.invoke(null, xml, 99);

        assertEquals("<root><data/></root>", result, NOT_EQUAL);
    }

    @Test
    void testGetPropertySetValid() {
        String xml = "<event><a>1</a></event>";
        CourtLogMarshaller marshallerSpy = spy(new CourtLogMarshaller());
        Map<String, Object> mockMap = new ConcurrentHashMap<>();
        mockMap.put("a", "1");
        doReturn(mockMap).when(marshallerSpy).unmarshall(xml);

        Map result = CourtLogXmlHelper.getPropertySet(xml);
        assertEquals("1", result.get("a"), NOT_EQUAL);
    }

    @Test
    void testCreateDocumentValid() {
        String xml = EVENT_DATA;
        Document doc = CourtLogXmlHelper.createDocument(xml);
        assertEquals("event", doc.getDocumentElement().getTagName(), NOT_EQUAL);
    }

    @Test
    void testCreateDocumentinvalid() {
        String badXml = "<event><data></event"; // malformed XML
        assertThrows(CourtLogRuntimeException.class, () -> CourtLogXmlHelper.createDocument(badXml));
    }
}
