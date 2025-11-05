package uk.gov.hmcts.pdda.courtlog.helpers.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import uk.gov.hmcts.framework.services.xml.XmlServicesImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD")
class CourtLogMarshallerTest {

    @Test
    void constructorInitializesSuccessfully() {
        CourtLogMarshaller marshaller = new CourtLogMarshaller();
        assertNotNull(marshaller);
    }

    @Test
    void marshallDelegatesToXmlServicesImpl() {
        // Arrange
        Map<String, Object> props = new HashMap<>();
        props.put("key", "value");

        XmlServicesImpl mockXml = mock(XmlServicesImpl.class);
        when(mockXml.generateXmlFromPropSet(props, "root")).thenReturn("<xml/>");

        try (MockedStatic<XmlServicesImpl> xmlServicesMock = mockStatic(XmlServicesImpl.class)) {
            xmlServicesMock.when(XmlServicesImpl::getInstance).thenReturn(mockXml);

            CourtLogMarshaller marshaller = new CourtLogMarshaller();

            // Act
            String xml = marshaller.marshall(props, "root");

            // Assert
            assertEquals("<xml/>", xml);
            verify(mockXml).generateXmlFromPropSet(props, "root");
        }
    }

    @Test
    void unmarshallConvertsXmlToMap() {
        // Arrange
        String xml = "<event><key>value</key><list>one</list><list>two</list></event>";

        // Mock the static document creation
        Document doc = CourtLogXmlHelper.createDocument(xml);
        assertNotNull(doc);

        CourtLogMarshaller marshaller = new CourtLogMarshaller();

        // Act
        Map<String, Object> result = marshaller.unmarshall(xml);

        // Assert – indirect coverage of private methods:
        // - unmarshallNode() recursion
        // - CourtLogUnmarshalledNode.put() promotions to list
        assertNotNull(result);
        assertTrue(result.containsKey("key"));
        assertEquals("value", result.get("key"));
        assertTrue(result.containsKey("list"));
        Object listObject = result.get("list");
        assertTrue(listObject instanceof List);

        List<?> values = (List<?>) listObject;
        assertTrue(values.contains("one") && values.contains("two"));
    }

    @Test
    void unmarshallHandlesEmptyNodeGracefully() {
        String xml = "<event></event>";
        CourtLogMarshaller marshaller = new CourtLogMarshaller();

        Map<String, Object> result = marshaller.unmarshall(xml);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void unmarshallHandlesNestedElementsRecursively() {
        String xml = "<event><outer><inner>deepValue</inner></outer></event>";
        CourtLogMarshaller marshaller = new CourtLogMarshaller();

        Map<String, Object> result = marshaller.unmarshall(xml);

        assertNotNull(result);
        assertTrue(result.containsKey("outer"));
        Object nested = result.get("outer");
        assertTrue(nested instanceof Map);
        assertEquals("deepValue", ((Map<?, ?>) nested).get("inner"));
    }

    @Test
    void unmarshallReturnsEmptyMapWhenNoElementsPresent() {
        String xml = "<event/>";
        CourtLogMarshaller marshaller = new CourtLogMarshaller();
        Map<String, Object> result = marshaller.unmarshall(xml);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
