package uk.gov.hmcts.pdda.business.services.formatting;

import org.easymock.EasyMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>
 * Title: MergeComparisonUtils Test.
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
class MergeComparisonUtilsTest {

    private static final String NOT_EQUAL = "Result is Not Equal";

    private static final int LESS_THAN = -1;
    private static final int EQUAL_TO = 0;
    private static final int GREATER_THAN = 1;

    private static final String IWP = "IWP";
    private static final String NODE1 = "Node1";
    private static final String ONE = "1";
    private static final String EMPTY_STRING = "";

    @Test
    void testCompareNode1NullLessThanWarnedList() {
        assertEquals(LESS_THAN, MergeComparisonUtils.compareNode1Null("cs:FixedDate", false, true),
            NOT_EQUAL);
    }

    @Test
    void testCompareNode1NullLessThanFirmList() {
        assertEquals(LESS_THAN, MergeComparisonUtils.compareNode1Null("cs:SittingAt", true, false),
            NOT_EQUAL);
    }

    @Test
    void testCompareNode2NullLessThanWarnedList() {
        assertEquals(GREATER_THAN,
            MergeComparisonUtils.compareNode2Null("cs:FixedDate", false, true), NOT_EQUAL);
    }

    @Test
    void testCompareNode2NullLessThanFirmList() {
        assertEquals(GREATER_THAN,
            MergeComparisonUtils.compareNode2Null("cs:SittingAt", true, false), NOT_EQUAL);
    }

    @Test
    void testCompareNodesNumericValue() {
        Map<String, String> map1 = new ConcurrentHashMap<>();
        map1.put(NODE1, ONE);
        Map<String, String> map2 = new ConcurrentHashMap<>();
        map2.put(NODE1, ONE);
        List<String> nodesToMatch = new ArrayList<>();
        nodesToMatch.add(NODE1);
        assertEquals(EQUAL_TO,
            MergeComparisonUtils.compareNodes(map1, map2, IWP, nodesToMatch, false, true), NOT_EQUAL);
    }

    @Test
    void testCompareNodesNullNode1Value() {
        Map<String, String> map1 = new ConcurrentHashMap<>();
        map1.put(NODE1, EMPTY_STRING);
        Map<String, String> map2 = new ConcurrentHashMap<>();
        map2.put(NODE1, ONE);
        List<String> nodesToMatch = new ArrayList<>();
        nodesToMatch.add(NODE1);
        assertEquals(GREATER_THAN,
            MergeComparisonUtils.compareNodes(map1, map2, IWP, nodesToMatch, false, true), NOT_EQUAL);
    }

    @Test
    void testCompareNodesNullNode2Value() {
        Map<String, String> map1 = new ConcurrentHashMap<>();
        map1.put(NODE1, ONE);
        Map<String, String> map2 = new ConcurrentHashMap<>();
        map2.put(NODE1, EMPTY_STRING);
        List<String> nodesToMatch = new ArrayList<>();
        nodesToMatch.add(NODE1);
        assertEquals(LESS_THAN,
            MergeComparisonUtils.compareNodes(map1, map2, IWP, nodesToMatch, false, true), NOT_EQUAL);
    }

    @Test
    void testCompareNodesEmpty() {
        Map<String, String> map1 = new ConcurrentHashMap<>();
        Map<String, String> map2 = new ConcurrentHashMap<>();
        List<String> nodesToMatch = new ArrayList<>();
        nodesToMatch.add(NODE1);
        assertEquals(EQUAL_TO,
            MergeComparisonUtils.compareNodes(map1, map2, IWP, nodesToMatch, false, true), NOT_EQUAL);
    }

    @Test
    void testCompareNodesNotNumeric() {
        Map<String, String> map1 = new ConcurrentHashMap<>();
        map1.put(NODE1, "NotNull");
        Map<String, String> map2 = new ConcurrentHashMap<>();
        map2.put(NODE1, "NotNull");
        List<String> nodesToMatch = new ArrayList<>();
        nodesToMatch.add(NODE1);
        assertEquals(EQUAL_TO,
            MergeComparisonUtils.compareNodes(map1, map2, IWP, nodesToMatch, false, true), NOT_EQUAL);
    }
}
