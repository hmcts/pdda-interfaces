package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**

 * Title: MergeDocumentUtils Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class MergeDocumentUtilsTest {

    private static final String NULL = "Result is Null";
    private static final String NOT_NULL = "Result is Not Null";

    private static final String BASE_XML =
        "<cs:CourtList><cs:ReserveList>TestCourtHouse</cs:ReserveList></cs:CourtList>";

    private static final String PATH_TO_MATCH = "CourtList/ReserveList";

    @Test
    void testGetNodesToMergeAtParentLevel()
        throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        // Setup
        Document document = getDummyDoc(BASE_XML);
        XPathExpression rootNodeExpression =
            XPathFactory.newInstance().newXPath().compile(PATH_TO_MATCH);
        XPathExpression[] expressions = {rootNodeExpression};
        // Run & Check
        assertNotNull(MergeDocumentUtils.getNodesToMergeAtParentLevel(expressions, document), NULL);
    }

    @Test
    void testGetNodesToMergeAtParentLevelException()
        throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        // Setup
        Document document = getDummyDoc(BASE_XML);
        String pathToMatchOn = "InvalidPath";
        XPathExpression rootNodeExpression =
            XPathFactory.newInstance().newXPath().compile(pathToMatchOn);
        XPathExpression[] expressions = {rootNodeExpression};
        Assertions.assertThrows(IOException.class, () -> {
            // Run & Check
            MergeDocumentUtils.getNodesToMergeAtParentLevel(expressions, document);
        });
    }

    @Test
    void testGetFirmNode()
        throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        // Setup
        String xml = "<cs:FirmList></cs:FirmList>";
        Document document = getDummyDoc(xml);
        // Run & Check
        assertNotNull(MergeDocumentUtils.getFirmNode(document), NULL);
    }

    @Test
    void testGetFirmNodeNull()
        throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        // Setup
        String xml = "<cs:InvalidNode></cs:InvalidNode>";
        Document document = getDummyDoc(xml);
        // Run & Check
        assertNull(MergeDocumentUtils.getFirmNode(document), NOT_NULL);
    }

    @Test
    void testGetNodesToMerge2()
        throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        // Setup
        Document document = getDummyDoc(BASE_XML);
        XPathExpression rootNodeExpression =
            XPathFactory.newInstance().newXPath().compile(PATH_TO_MATCH);
        XPathExpression[] expressions = {rootNodeExpression};
        Document[] documents = {document, document};
        // Run & Check
        assertNotNull(MergeDocumentUtils.getNodesToMerge2(expressions, documents), NULL);
    }

    private Document getDummyDoc(String xml)
        throws SAXException, IOException, ParserConfigurationException {
        return DocumentUtils.createInputDocument(xml);
    }
}
