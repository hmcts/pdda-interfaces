package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.xml.xpath.XPathExpressionException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: IWPXMLMergeUtils Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Chris Vincent
 */
@ExtendWith(EasyMockExtension.class)
class IwpXmlMergeUtilsTest {

    private static final String EQUALS = "Results are not Equal";

    @TestSubject
    private static IwpXmlMergeUtils classUnderTest;

    @BeforeAll
    public static void setUp() {
        try {
            classUnderTest = new IwpXmlMergeUtils();
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
        final String[] expectedArray = {"currentcourtstatus/court/courtsites/courtsite/courtrooms"};

        // Run
        final String[] stringArr = classUnderTest.getRootNodes();

        // Checks
        assertArrayEquals(expectedArray, stringArr, EQUALS);
    }

    @Test
    void testGetNodePositionArray() {
        // Setup
        final String[] expectedArray = {"courtroomname"};

        // Run
        final String[] stringArr = classUnderTest.getNodePositionArray();

        // Checks
        assertArrayEquals(expectedArray, stringArr, EQUALS);
    }

    @Test
    void testGetNodeMatchArray() {
        // Setup
        final String[] expectedArray = {"courtsitename"};

        // Run
        final String[] stringArr = classUnderTest.getNodeMatchArray();

        // Checks
        assertArrayEquals(expectedArray, stringArr, EQUALS);
    }

    @Test
    void testGetMergeType() {
        // Setup
        final String expectedString = "IWP";

        // Run
        final String actualString = classUnderTest.getMergeType();

        // Checks
        assertEquals(expectedString, actualString, EQUALS);
    }

}
