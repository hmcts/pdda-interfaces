package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Node;

import java.util.Arrays;

public final class MergeIwpUtils {

    private static final String COURT_ROOM_NAME = "courtroomname";
    private static final String CASES = "cases";
    private static final String EMPTY_STRING = "";

    private MergeIwpUtils() {

    }

    public static void replaceIwp(Node childNodeToMerge, Node insertBeforeNode, Node node) {
        String courtroomnumber1 =
            MergeNodeUtils.getNodeMapValues(Arrays.asList(COURT_ROOM_NAME), childNodeToMerge).get(COURT_ROOM_NAME);
        String courtroomnumber2 =
            MergeNodeUtils.getNodeMapValues(Arrays.asList(COURT_ROOM_NAME), insertBeforeNode).get(COURT_ROOM_NAME);

        if (courtroomnumber1 != null && courtroomnumber1.equals(courtroomnumber2)) {
            // if there is no cases information in the original xml then we replace with the newer
            // xml
            // otherwise we do nothing and keep the xml as is.
            String cases = MergeNodeUtils.getNodeMapValues(Arrays.asList(CASES), insertBeforeNode).get(CASES);

            // or if the time in cpp is after
            if (cases == null || EMPTY_STRING.equals(cases)
                || MergeDateUtils.isCppAfterXhibit(insertBeforeNode, childNodeToMerge)) {
                node.replaceChild(childNodeToMerge, insertBeforeNode);
            }
        }
    }
}
