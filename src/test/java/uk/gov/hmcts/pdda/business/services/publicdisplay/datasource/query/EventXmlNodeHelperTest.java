package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uk.gov.hmcts.framework.services.CsServices;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: EventXmlNodeHelper Test.
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
class EventXmlNodeHelperTest {

    private static final String NULL = "Result is Null";
    
    private static final String TEST_XML = "<testNode><testChildNode>courtroomname</testChildNode></testNode>";

    @Test
    void testBuildEventNode() {
        assertNotNull(EventXmlNodeHelper.buildEventNode(TEST_XML), NULL);
    }
    
    @Test
    void testBuildEventNodeWithNodeParam() {
        assertNotNull(EventXmlNodeHelper.buildEventNode(getDummyNode()), NULL);
    }
    
    private Node getDummyNode() {
        Document doc = CsServices.getXmlServices().createDocFromString(TEST_XML);
        return doc.getDocumentElement();
    }
}
