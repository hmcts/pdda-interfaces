package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled.DocumentUtils;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * <p>
 * Title: FirmListUtils Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class FirmListUtilsTest {

    private static final String NOT_EQUAL = "Result is Not Equal";
    private static final String NOT_NULL = "Result is Not Null";
    private static final String NULL = "Result is Null";

    private static final String KEY_1 = "Key1";
    private static final String KEY_2 = "Key2";

    @Test
    void testGetNodeUniqueKeySameKey() {
        assertEquals(KEY_1, FirmListUtils.getNodeUniqueKey(KEY_1, KEY_1), NOT_EQUAL);
    }

    @Test
    void testGetNodeUniqueKey() {
        assertEquals(KEY_2, FirmListUtils.getNodeUniqueKey(KEY_1, KEY_2), NOT_EQUAL);
    }

    @Test
    void testGetNodeUniqueKeyNullKey() {
        assertNull(FirmListUtils.getNodeUniqueKey(null, null), NOT_NULL);
    }

    @Test
    void testGetSittingDateFromCourtNode()
        throws SAXException, IOException, ParserConfigurationException {
        // Setup
        String xml = "<cs:CourtList><cs:SittingDate></cs:SittingDate></cs:CourtList>";
        Node node = getDummyDoc(xml).getFirstChild().getChildNodes().item(0);
        // Run & Checks
        assertNotNull(FirmListUtils.getSittingDateFromCourtNode(node), NULL);
    }

    @Test
    void testGetCourtNodeNodeBeforeInsertNullValue()
        throws SAXException, IOException, ParserConfigurationException {
        // Setup
        String xml = "<cs:CourtList><cs:SittingDate>Test</cs:SittingDate>"
            + "<cs:HearingDate>Test</cs:HearingDate></cs:CourtList>";
        Node node = getDummyDoc(xml).getFirstChild().getChildNodes().item(0);
        Node nodeToMerge = getDummyDoc(xml).getFirstChild().getChildNodes().item(0);
        // Run & Checks
        assertNull(FirmListUtils.getCourtNodeNodeBeforeInsert(node, nodeToMerge), NOT_NULL);
    }

    private Document getDummyDoc(String xml)
        throws SAXException, IOException, ParserConfigurationException {
        return DocumentUtils.createInputDocument(xml);
    }
}
