package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: FirmListXMLMergeUtils Test.


 * Description:


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris Vincent
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class FirmListXmlMergeUtilsTest {

    private static final String NOT_EQUAL = "Results are Not Equal";

    @TestSubject
    private static FirmListXmlMergeUtils classUnderTest;

    @BeforeAll
    public static void setUp() {
        try {
            classUnderTest = new FirmListXmlMergeUtils();
        } catch (XPathExpressionException ex) {
            fail(ex.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetRootNodes() {
        // Setup
        final String[] expectedArray = {"FirmList/CourtLists/CourtList/Sittings", "FirmList/ReserveList/Hearing"};

        // Run
        final String[] stringArr = classUnderTest.getRootNodes();

        // Checks
        assertArrayEquals(expectedArray, stringArr, NOT_EQUAL);
    }

    @Test
    void testGetNodePositionArray() {
        // Setup
        final String[] expectedArray = {"cs:CourtRoomNumber", "cs:HearingDate", "cs:SittingAt"};

        // Run
        final String[] stringArr = classUnderTest.getNodePositionArray();

        // Checks
        assertArrayEquals(expectedArray, stringArr, NOT_EQUAL);
    }

    @Test
    void testGetNodeMatchArray() {
        // Setup
        final String[] expectedArray = {"cs:CourtHouseCode", "cs:CourtHouseName"};

        // Run
        final String[] stringArr = classUnderTest.getNodeMatchArray();

        // Checks
        assertArrayEquals(expectedArray, stringArr, NOT_EQUAL);
    }

    @Test
    void testGetMergeType() {
        // Setup
        final String expectedString = "LISTS";

        // Run
        final String actualString = classUnderTest.getMergeType();

        // Checks
        assertEquals(expectedString, actualString, NOT_EQUAL);
    }

    @Test
    void testSortFirmCourtLists() {
        // Setup
        final Document unsortedDoc = convertStringToDocument(getUnsortedFirmListXml());

        // Run
        final Document sortedDoc = classUnderTest.sortFirmCourtLists(unsortedDoc);

        // Checks
        final String[] expectedOrder = {"2020-05-01", "2020-09-01", "2021-01-01", "2022-04-01", "2022-08-01"};

        try {
            String pathToMatchOn = "FirmList/CourtLists/CourtList";
            XPathExpression rootNodeExpression = XPathFactory.newInstance().newXPath().compile(pathToMatchOn);
            NodeList nodeList = (NodeList) rootNodeExpression.evaluate(sortedDoc, XPathConstants.NODESET);
            for (int nodeNo = 0; nodeNo < nodeList.getLength(); nodeNo++) {
                String sittingDate = nodeList.item(nodeNo).getAttributes().getNamedItem("SittingDate").getNodeValue();
                assertEquals(expectedOrder[nodeNo], sittingDate, NOT_EQUAL);
            }
        } catch (XPathExpressionException exception) {
            fail(exception);
        }
    }

    private String getUnsortedFirmListXml() {
        StringBuffer sb = new StringBuffer(1024);

        // XML Header
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<cs:FirmList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:apd=\"http://www.govtalk.gov.uk/people/AddressAndPersonalDetails\" xsi:schemaLocation=\"http://www.courtservice.gov.uk/schemas/courtservice FirmList-v5-2.xsd\">"
            + "<cs:CourtLists>"
            + "<cs:CourtList SittingDate=\"2022-08-01\"><cs:CourtHouse/><cs:Sittings/></cs:CourtList>"
            + "<cs:CourtList SittingDate=\"2020-09-01\"><cs:CourtHouse/><cs:Sittings/></cs:CourtList>"
            + "<cs:CourtList SittingDate=\"2021-01-01\"><cs:CourtHouse/><cs:Sittings/></cs:CourtList>"
            + "<cs:CourtList SittingDate=\"2022-04-01\"><cs:CourtHouse/><cs:Sittings/></cs:CourtList>"
            + "<cs:CourtList SittingDate=\"2020-05-01\"><cs:CourtHouse/><cs:Sittings/></cs:CourtList>"
            + "</cs:CourtLists></cs:FirmList>");

        return sb.toString();
    }

    /**
     * Converts a String into a Document object.

     * @param xml XML String
     * @return Document
     */
    private Document convertStringToDocument(final String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception exception) {
            fail(exception);
        }
        return null;
    }

    @Test
    void testFirmTagSuccess() {
        assertTrue(FirmTag.HEARING == FirmTag.fromString("cs:Hearing"), "Result is not True");
    }

    @Test
    void testFirmTagFailure() {
        assertFalse(FirmTag.HEARING == FirmTag.fromString("XXX"), "Result is not False");
    }
}
