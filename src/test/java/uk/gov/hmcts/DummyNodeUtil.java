package uk.gov.hmcts;

import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DummyNodeUtil {
  
    private static final String NOTNULL = "Result is Null";
    private static final String EQUALS = "Results are not Equal";
    private static final String LIST = "List";
    private static final String ONETWOTHREE = "123";
    
    private DummyNodeUtil() {
        // Do nothing
    }
    
    public static BranchEventXmlNode getEvent(String event) {
        BranchEventXmlNode result = new BranchEventXmlNode("branch");
        result.toDebug();
        result.add(getDateEventNode());
        result.add(new LeafEventXmlNode("type", event));
        result.add(new LeafEventXmlNode("E20906_Defence_CO_Name", "Bob"));
        result.add(new LeafEventXmlNode("E31000_Witness_Number", ONETWOTHREE));
        result.add(new LeafEventXmlNode("E32000_Witness_Number", ONETWOTHREE));
        result.add(new LeafEventXmlNode("E20613_Witness_Number", ONETWOTHREE));
        result.add(new LeafEventXmlNode("E20920_Witness_Number", ONETWOTHREE));
        result.add(new LeafEventXmlNode("E20931_Witness_Number", ONETWOTHREE));
        result.add(new LeafEventXmlNode("E20932_Witness_Number", ONETWOTHREE));
        result.add(new LeafEventXmlNode("E20606_Appellant_CO_Name", ONETWOTHREE));
        result.add(new LeafEventXmlNode("defendant_on_case_id", ONETWOTHREE));
        result.add(new LeafEventXmlNode("E20910_Defence_CC_Name", ONETWOTHREE));
        result.add(new LeafEventXmlNode("E20502_P_And_D_Hearing_On_Date", "11-05-2023"));
        result.add(new LeafEventXmlNode("defendant_name", "Bob"));
        result.add(new LeafEventXmlNode("free_text", "free text"));
        result.add(getListNode());
        result.add(result);
        result.getName();
        return result;
    }
    
    private static BranchEventXmlNode getListNode() {
        BranchEventXmlNode result = new BranchEventXmlNode("TestListNode");
        // Test list
        List<String> list = new ArrayList<>();
        list.add("testItem");
        result.getMap().put(LIST, list);
        result.getList(LIST);
        result.get(LIST);
        result.add(new LeafEventXmlNode(LIST, "new value"));
        // Test non-list type
        result.getMap().put("HashMap", new HashMap<String, String>());
        result.getList("HashMap");
        result.add(new LeafEventXmlNode("HashMap", "new value"));
        return result;
    }

    private static LeafEventXmlNode getDateEventNode() {
        LeafEventXmlNode result = new LeafEventXmlNode("Test", "--");
        assertEquals(0, result.getDay().length(), EQUALS);
        assertEquals(0, result.getMonth().length(), EQUALS);
        assertEquals(0, result.getYear().length(), EQUALS);
        result = new LeafEventXmlNode("Test", "01-03-2023");
        result.getName();
        assertNotNull(result.getDay(), NOTNULL);
        assertNotNull(result.getMonth(), NOTNULL);
        assertNotNull(result.getYear(), NOTNULL);
        assertNotNull(result.getValue(), NOTNULL);
        assertNotNull(result.toDebug(), NOTNULL);
        return result;
    }
    
    public static BranchEventXmlNode addBranch(String branchname, String leafname, String leafValue) {
        BranchEventXmlNode result = new BranchEventXmlNode(branchname);
        result.add(new LeafEventXmlNode(leafname, leafValue));
        result.add(result);
        result.getMap();
        result.getName();
        return result;
    }
    
    public static BranchEventXmlNode getE20603WitnessSwornOptions(String laoType) {
        BranchEventXmlNode result =
            addBranch("E20603_Witness_Sworn_Options", "E20603_WS_List", laoType);
        result.add(new LeafEventXmlNode("E20603_Witness_No", ONETWOTHREE));
        return result;
    }

    public static BranchEventXmlNode getE20935WitnessReadOptions(String laoType) {
        BranchEventXmlNode result =
            addBranch("E20935_Witness_Read_Options", "E20935_WR_Type", laoType);
        result.add(new LeafEventXmlNode("E20935_WR_Number", ONETWOTHREE));
        return result;
    }

    public static BranchEventXmlNode getE20904WitnessSwornOptions(String laoType) {
        BranchEventXmlNode result =
            addBranch("E20904_Witness_Sworn_Options", "E20904_WSO_Type", laoType);
        result.add(new LeafEventXmlNode("E20904_WSO_Number", ONETWOTHREE));
        return result;
    }

    public static BranchEventXmlNode getE20903ProsecutionCaseOptions(String laoType) {
        return addBranch("E20903_Prosecution_Case_Options", "E20903_PCO_Type", laoType);
    }

    public static BranchEventXmlNode getE20901TimeEstimateOptions(String laoType) {
        BranchEventXmlNode result =
            addBranch("E20901_Time_Estimate_Options", "E20901_TEO_units", laoType);
        result.add(new LeafEventXmlNode("E20901_TEO_time", "14:21"));
        return result;
    }

    public static BranchEventXmlNode getE30100ShortAdjournOptions(String laoType) {
        BranchEventXmlNode result =
            addBranch("E30100_Short_Adjourn_Options", "E30100_SAO_Type", laoType);
        result.add(new LeafEventXmlNode("E30100_SAO_Time", "14:21"));
        return result;
    }

    public static BranchEventXmlNode getE30200LongAdjournOptions(String laoType) {
        BranchEventXmlNode result =
            addBranch("E30200_Long_Adjourn_Options", "E30200_LAO_Type", laoType);
        result.add(new LeafEventXmlNode("E30200_LAO_Date", "01-01-2023"));
        result.add(new LeafEventXmlNode("E30200_LAO_PSR_Required", "true"));
        result.add(new LeafEventXmlNode("E30200_LAO_Not_Reserved", "true"));
        result.add(new LeafEventXmlNode("E30200_LAO_Reserved_To_Judge_Name", "Judgename"));
        return result;
    }
}