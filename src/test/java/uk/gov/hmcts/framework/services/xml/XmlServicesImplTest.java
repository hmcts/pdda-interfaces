package uk.gov.hmcts.framework.services.xml;

import org.eclipse.tags.shaded.org.apache.xpath.XPathAPI;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.gov.hmcts.framework.business.vos.CsValueObject;
import uk.gov.hmcts.framework.exception.CsUnrecoverableException;
import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.framework.services.ErrorHandler;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings({"unchecked", "PMD"})
class XmlServicesImplTest {
    
    private static final String NULL = "Result is Null";
    private static final String TRUE = "Result is True";
    private static final String NOT_EQUAL = "Result is Not Equal";
    
    private static final String NAME = "name";
    private static final String ROOT = "root";
    
    private MockedStatic<DocumentUtils> documentUtilsMock;
    private MockedStatic<XPathAPI> xpathApiMock;
    private MockedStatic<CsServices> csServicesMock;
    private ErrorHandler handler;

    @BeforeEach
    void setUp() {
        handler = mock(ErrorHandler.class);
        csServicesMock = mockStatic(CsServices.class);
        csServicesMock.when(CsServices::getDefaultErrorHandler).thenReturn(handler);
    }

    @AfterEach
    void tearDown() {
        if (documentUtilsMock != null) {
            documentUtilsMock.close();
        }
        if (xpathApiMock != null) {
            xpathApiMock.close();
        }
        if (csServicesMock != null) {
            csServicesMock.close();
        }
    }
    
    @Test
    void testCreateDocFromValueSuccess() throws Exception {
        CsValueObject vo = mock(CsValueObject.class);

        try (MockedConstruction<Marshaller> cons = mockConstruction(
                Marshaller.class,
                (mock, ctx) -> {
                    // no behavior needed; we just verify calls later
                    doNothing().when(mock).setSuppressXSIType(false);
                    doNothing().when(mock).marshal(any());
                })) {

            // When
            Document out = XmlServicesImpl.getInstance().createDocFromValue(vo);

            // Then (single assert for PMD)
            assertNotNull(out, "Document should be created and returned");

            // Verifications (not counted as assertions by PMD)
            Marshaller created = cons.constructed().get(0);
            verify(created).setSuppressXSIType(false);
            verify(created).marshal(vo);
        }
    }

    @Test
    void testCreateDocFromValueFail()
        throws ParserConfigurationException, MarshalException, ValidationException {
        CsValueObject vo = mock(CsValueObject.class);
        // Make the constructed Marshaller throw MarshalException on marshal()
        try (MockedConstruction<Marshaller> cons = mockConstruction(
                Marshaller.class,
                (mock, ctx) -> doThrow(new MarshalException("boom")).when(mock).marshal(any())
        )) {

            // When / Then
            CsXmlServicesException ex = assertThrows(
                    CsXmlServicesException.class,
                    () -> XmlServicesImpl.getInstance().createDocFromValue(vo)
            );

            // Verify the handler was invoked with the wrapped exception and class
            verify(handler).handleError(ex, XmlServicesImpl.class);

            // Sanity: make sure our Marshaller was actually constructed and used
            assertFalse(cons.constructed().isEmpty(), TRUE);
            verify(cons.constructed().get(0)).setSuppressXSIType(false);
            verify(cons.constructed().get(0)).marshal(vo);
        }
        
    }
    
    @Test
    void testAddCollectionToXmlDocSuccess() throws Exception {
        // Arrange
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        
        // Use ConcurrentHashMap/ArrayList to keep deterministic order
        Map<String, Object> m1 = new ConcurrentHashMap<>();
        m1.put("id", 1);

        Map<String, Object> m2 = new ConcurrentHashMap<>();
        m2.put(NAME, "Alice");

        List<Map<String, Object>> input = new ArrayList<>();
        input.add(m1);
        input.add(m2);

        // Reflectively access the private method
        Method method = XmlServicesImpl.class.getDeclaredMethod(
                "addCollectionToXmlDoc", Document.class, Collection.class, String.class);
        method.setAccessible(true);

        // Act
        XmlServicesImpl impl = XmlServicesImpl.getInstance();
        Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();
        Collection<Element> out = (Collection<Element>) method.invoke(impl, doc, input, "item");

        // Assert
        assertNotNull(out, NULL);
    }

    @Test
    void testAddCollectionToXmlDocFail() throws Exception {
        XmlServicesImpl impl = XmlServicesImpl.getInstance();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();

        List<Map<String, Object>> input = Collections.emptyList();

        Method method = XmlServicesImpl.class.getDeclaredMethod(
                "addCollectionToXmlDoc", Document.class, Collection.class, String.class);
        method.setAccessible(true);

        Collection<Element> out = (Collection<Element>) method.invoke(impl, doc, input, "item");
        assertNotNull(out, NULL);
    }
    
    @Test
    void testAddPropertyMapToXmlDocSuccess() throws Exception {
        // Arrange
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        // Prepare nested map + collection
        Map<String, Object> innerMap = new ConcurrentHashMap<>();
        innerMap.put("innerKey", "innerValue");

        Map<String, Object> child1 = new ConcurrentHashMap<>();
        child1.put(NAME, "Alice");
        child1.put("age", 30);

        Map<String, Object> child2 = new ConcurrentHashMap<>();
        child2.put(NAME, "Bob");
        child2.put("age", 25);

        List<Map<String, Object>> people = Arrays.asList(child1, child2);

        Map<String, Object> rootMap = new ConcurrentHashMap<>();
        rootMap.put("title", "Example");
        rootMap.put("meta", innerMap);
        rootMap.put("people", people);

        // Access the private method
        Method method = XmlServicesImpl.class.getDeclaredMethod(
                "addPropertyMapToXmlDoc", Document.class, Map.class, String.class);
        method.setAccessible(true);

        // Act
        XmlServicesImpl impl = XmlServicesImpl.getInstance();
        Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();
        Element rootElement = (Element) method.invoke(impl, doc, rootMap, ROOT);

        // Assert
        assertEquals(ROOT, rootElement.getTagName(), NOT_EQUAL);
    }

    @Test
    void testAddPropertyMapToXmlDocFail() throws Exception {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("key1", "");
        map.put("key2", "value");

        Method method = XmlServicesImpl.class.getDeclaredMethod(
                "addPropertyMapToXmlDoc", Document.class, Map.class, String.class);
        method.setAccessible(true);

        XmlServicesImpl impl = XmlServicesImpl.getInstance();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element result = (Element) method.invoke(impl, doc, map, ROOT);

        assertEquals(ROOT, result.getTagName(), NOT_EQUAL);
    }
    
    @Test
    void testCreateDocFromStringSuccess() throws Exception {
        // Arrange
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document expectedDoc = documentBuilder.newDocument();
        Element root = expectedDoc.createElement(ROOT);
        root.appendChild(expectedDoc.createElement("child"));
        expectedDoc.appendChild(root);

        String xml = "<root><child>ok</child></root>";
        documentUtilsMock = mockStatic(DocumentUtils.class);
        documentUtilsMock.when(() -> DocumentUtils.createInputDocument(xml)).thenReturn(expectedDoc);

        // Act
        Document result = XmlServicesImpl.getInstance().createDocFromString(xml);

        // Assert
        assertNotNull(result, NULL);
        verifyNoInteractions(handler);
    }

    @Test
    void testCreateDocFromStringException() {
        // Arrange
        String xml = "<bad>";
        documentUtilsMock = mockStatic(DocumentUtils.class);
        documentUtilsMock.when(() -> DocumentUtils.createInputDocument(xml))
                .thenThrow(new SAXException("invalid XML"));

        // Assert
        assertThrows(
                CsXmlServicesException.class,
                () -> XmlServicesImpl.getInstance().createDocFromString(xml)
        );
    }

    @Test
    void testCreateDocFromStringFail() {
        String xml = "<data/>";
        documentUtilsMock = mockStatic(DocumentUtils.class);
        documentUtilsMock.when(() -> DocumentUtils.createInputDocument(xml))
                .thenThrow(new FactoryConfigurationError("config broken"));

        // Assert
        assertThrows(
                CsXmlServicesException.class,
                () -> XmlServicesImpl.getInstance().createDocFromString(xml)
        );
    }
    
    @Test
    void testGetXpathValueFromXmlStringSuccess() throws Exception {
        // Arrange
        String xml = "<root><child>abc</child></root>";
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));

        Node mockNode = mock(Node.class);
        Mockito.when(mockNode.getNodeValue()).thenReturn("abc");

        documentUtilsMock = mockStatic(DocumentUtils.class);
        documentUtilsMock.when(() -> DocumentUtils.createInputDocument(xml)).thenReturn(doc);

        String xpath = "/root/child/text()";
        xpathApiMock = mockStatic(XPathAPI.class);
        xpathApiMock.when(() -> XPathAPI.selectSingleNode(doc, xpath)).thenReturn(mockNode);

        // Act
        String result = XmlServicesImpl.getInstance().getXpathValueFromXmlString(xml, xpath);

        // Assert
        assertEquals("abc", result, NOT_EQUAL);
        verifyNoInteractions(handler);
    }

    @Test
    void testGetXpathValueFromXmlStringNullArgs() {
        XmlServicesImpl service = XmlServicesImpl.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> service.getXpathValueFromXmlString(null, "/root"));
    }

    @Test
    void testGetXpathValueFromXmlStringNodeNotFoundException() throws Exception {
        String xml = "<root><child>abc</child></root>";
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();

        documentUtilsMock = mockStatic(DocumentUtils.class);
        documentUtilsMock.when(() -> DocumentUtils.createInputDocument(xml)).thenReturn(doc);

        String xpath = "/root/missing/text()";
        xpathApiMock = mockStatic(XPathAPI.class);
        xpathApiMock.when(() -> XPathAPI.selectSingleNode(doc, xpath)).thenReturn(null);

        XmlServicesImpl impl = XmlServicesImpl.getInstance();

        assertThrows(CsUnrecoverableException.class,
                () -> impl.getXpathValueFromXmlString(xml, xpath));
    }

    @Test
    void testGetXpathValueFromXmlStringExpectionHandled() {
        String xml = "<invalid>";
        
        documentUtilsMock = mockStatic(DocumentUtils.class);
        documentUtilsMock.when(() -> DocumentUtils.createInputDocument(xml))
                .thenThrow(new SAXException("parse error"));

        XmlServicesImpl impl = XmlServicesImpl.getInstance();

        String xpath = "/root/child";
        assertThrows(
                CsUnrecoverableException.class,
                () -> impl.getXpathValueFromXmlString(xml, xpath)
        );

        verify(handler).handleError(any(SAXException.class), any());
    }

    @Test
    void testGetXpathValueFromXmlStringTransformerException() throws Exception {
        String xml = "<root><child>abc</child></root>";
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));

        documentUtilsMock = mockStatic(DocumentUtils.class);
        documentUtilsMock.when(() -> DocumentUtils.createInputDocument(xml)).thenReturn(doc);

        String xpath = "/root/child/text()";
        xpathApiMock = mockStatic(XPathAPI.class);
        xpathApiMock.when(() -> XPathAPI.selectSingleNode(doc, xpath))
                .thenThrow(new TransformerException("broken xpath"));

        XmlServicesImpl impl = XmlServicesImpl.getInstance();

        assertThrows(
                CsUnrecoverableException.class,
                () -> impl.getXpathValueFromXmlString(xml, xpath)
        );

        verify(handler).handleError(any(TransformerException.class), any());
    }
    
    @Test
    void testAddElementByTagNameNullDocument() {
        XmlServicesImpl impl = XmlServicesImpl.getInstance();
        // Just ensure no exception
        assertDoesNotThrow(() ->
                impl.addElementByTagName(null, "tag", "value", "beforeTag"));
    }

    @Test
    void testAddElementByTagNameSuccess() throws Exception {
        // Arrange
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();

        Element root = doc.createElement(ROOT);
        Element before = doc.createElement("target");
        Element existingChild = doc.createElement("existing");
        existingChild.appendChild(doc.createTextNode("E"));
        before.appendChild(existingChild);
        root.appendChild(before);
        doc.appendChild(root);

        // Act
        XmlServicesImpl impl = XmlServicesImpl.getInstance();
        impl.addElementByTagName(doc, "inserted", "V", "target");

        // Assert
        NodeList targets = doc.getElementsByTagName("target");
        assertEquals(1, targets.getLength(), NOT_EQUAL);
    }

    @Test
    void testAddElementByTagNameTagMissing() throws Exception {
        // Arrange
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();

        Element root = doc.createElement(ROOT);
        Element another = doc.createElement("somethingElse");
        root.appendChild(another);
        doc.appendChild(root);

        // Act
        XmlServicesImpl impl = XmlServicesImpl.getInstance();
        impl.addElementByTagName(doc, "inserted", "V", "missingTag");

        // Assert: no new tag added
        assertEquals(0, doc.getElementsByTagName("inserted").getLength(), NOT_EQUAL);
    }
    
    @Test
    void testGenerateXmlFromPropSetSuccess() throws Exception {
        // Arrange
        Map<String, Object> props = new ConcurrentHashMap<>();
        props.put(NAME, "Alice");
        props.put("age", 30);
        props.put("city", "London");

        // Act
        XmlServicesImpl impl = XmlServicesImpl.getInstance();
        String xml = impl.generateXmlFromPropSet(props, "person");

        // Assert
        assertNotNull(xml, NULL);
    }
    
    @Test
    void testGenerateXmlFromPropSetHandlesNestedMap() {
        // Arrange: create a nested map so the 'instanceof Map' branch executes
        Map<String, Object> innerMap = new ConcurrentHashMap<>();
        innerMap.put("innerKey", "innerValue");

        Map<String, Object> outerMap = new ConcurrentHashMap<>();
        outerMap.put("nested", innerMap);

        XmlServicesImpl xmlServices = new XmlServicesImpl();

        // Act
        String xml = xmlServices.generateXmlFromPropSet(outerMap, "root");

        // Assert
        assertNotNull(xml, "Result should not be null");
        assertTrue(xml.contains("<nested>"));
        assertTrue(xml.contains("<innerKey>innerValue</innerKey>"));
    }
    
    @Test
    void testGenerateXmlFromPropSetHandlesCollectionOfMaps() {
        // Arrange: Create a collection that contains maps (to trigger the 'instanceof Collection' branch)
        Map<String, Object> innerMap1 = new ConcurrentHashMap<>();
        innerMap1.put("itemKey1", "itemValue1");

        Map<String, Object> innerMap2 = new ConcurrentHashMap<>();
        innerMap2.put("itemKey2", "itemValue2");

        List<Map<String, ?>> collectionOfMaps = new ArrayList<>();
        collectionOfMaps.add(innerMap1);
        collectionOfMaps.add(innerMap2);

        // Outer map uses the collection to trigger the 'else if (value instanceof Collection)' block
        Map<String, Object> outerMap = new ConcurrentHashMap<>();
        outerMap.put("collectionNode", collectionOfMaps);

        XmlServicesImpl xmlServices = new XmlServicesImpl();

        // Act
        String xml = xmlServices.generateXmlFromPropSet(outerMap, "root");

        // Assert
        assertNotNull(xml, "Resulting XML should not be null");
        assertTrue(xml.contains("<collectionNode>"));
        assertTrue(xml.contains("<itemKey1>itemValue1</itemKey1>"));
        assertTrue(xml.contains("<itemKey2>itemValue2</itemKey2>"));
    }
    
    @Test
    void testValidateXmlValid() {
        // XSD is placed under resources
        String schema = "/database/test-data/unit_test_schemas/xmlServicesImplTest.xsd";
        String validXml = """
            <person>
              <id>42</id>
              <name>Ada Lovelace</name>
            </person>
            """;

        assertDoesNotThrow(() ->
            XmlServicesImpl.getInstance().validateXml(validXml, schema)
        );

        verifyNoInteractions(handler);
    }

    @Test
    void testValidateXmlInvalid() {
        String schema = "/database/test-data/unit_test_schemas/xmlServicesImplTest.xsd";
        // Missing <name> element required by the schema
        String invalidXml = """
            <person>
              <id>7</id>
            </person>
            """;

        assertThrows(
            CsUnrecoverableException.class,
            () -> XmlServicesImpl.getInstance().validateXml(invalidXml, schema)
        );

        // Error handler must be called with the original cause and the class
        verify(handler).handleError(any(SAXException.class), any());
    }
}
