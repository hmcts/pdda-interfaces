
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;

import java.util.Collection;

public abstract class DisplayDocumentCompiledRendererDelegateEvents
    extends AbstractCompiledRendererDelegate {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(DisplayDocumentCompiledRendererDelegateEvents.class);

    protected static final String EMPTY_STRING = "";
    protected static final String E30200_LAO_DATE = "E30200_LAO_Date";
    protected static final String E30200_LAO_JUDGE_NAME = "E30200_LAO_Reserved_To_Judge_Name";
    
    private static final String EVENT_10100 = "10100";
    private static final String EVENT_10500 = "10500";
    private static final String EVENT_20502 = "20502";
    
    private static final String EVENT_20602 = "20602";
    private static final String EVENT_20603 = "20603";
    private static final String EVENT_20604 = "20604";
    private static final String EVENT_20605 = "20605";
    private static final String EVENT_20606 = "20606";
    private static final String EVENT_20607 = "20607";
    
    private static final String EVENT_20608 = "20608";
    private static final String EVENT_20609 = "20609";
    private static final String EVENT_20610 = "20610";
    private static final String EVENT_20611 = "20611";
    private static final String EVENT_20612 = "20612";
    private static final String EVENT_20613 = "20613";
    
    private static final String EVENT_20901 = "20901";
    private static final String EVENT_20902 = "20902";
    private static final String EVENT_20903 = "20903";
    private static final String EVENT_20904 = "20904";
    private static final String EVENT_20905 = "20905";
    
    private static final String EVENT_20906 = "20906";
    private static final String EVENT_20907 = "20907";
    private static final String EVENT_20908 = "20908";
    private static final String EVENT_20909 = "20909";
    
    private static final String EVENT_20910 = "20910";
    private static final String EVENT_20911 = "20911";
    private static final String EVENT_20912 = "20912";
    private static final String EVENT_20914 = "20914";
    private static final String EVENT_20916 = "20916";
    private static final String EVENT_20917 = "20917";
    
    private static final String EVENT_20918 = "20918";
    private static final String EVENT_20919 = "20919";
    private static final String EVENT_20920 = "20920";
    private static final String EVENT_20931 = "20931";
    private static final String EVENT_20932 = "20932";
    private static final String EVENT_20935 = "20935";
    
    private static final String EVENT_21100 = "21100";
    private static final String EVENT_21200 = "21200";
    private static final String EVENT_21201 = "21201";
    private static final String EVENT_30100 = "30100";
    private static final String EVENT_30200 = "30200";
    private static final String EVENT_30300 = "30300";
    private static final String EVENT_30400 = "30400";
    
    private static final String EVENT_30500 = "30500";
    private static final String EVENT_30600 = "30600";
    private static final String EVENT_31000 = "31000";
    private static final String EVENT_32000 = "32000";
    private static final String EVENT_40601 = "40601";
    private static final String CPP = "CPP";
    
    protected DisplayDocumentCompiledRendererDelegateEvents() {
        super();
    }

    public void appendEvents(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_10100.equals(type)) {
            AppendEventsTenToTwentyFiveUtils.appendEvent10100(buffer, documentI18n);
        } else if (EVENT_10500.equals(type)) {
            AppendEventsTenToTwentyFiveUtils.appendEvent10500(buffer, documentI18n);
        } else if (EVENT_20502.equals(type)) {
            AppendEventsTenToTwentyFiveUtils.appendEvent20502(buffer, branchNode, documentI18n);
        } else {
            append20600Events(buffer, branchNode, documentI18n, defendantNames, type);
        }
    }

    private void append20600Events(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_20602.equals(type)) {
            AppendEventsTwentySixHundredUtils.appendEvent20602(buffer, documentI18n);
        } else if (EVENT_20603.equals(type)) {
            AppendEventsTwentySixHundredUtils.appendEvent20603(buffer, branchNode, documentI18n);
        } else if (EVENT_20604.equals(type)) {
            AppendEventsTwentySixHundredUtils.appendEvent20604(buffer, documentI18n);
        } else if (EVENT_20605.equals(type)) {
            AppendEventsTwentySixHundredUtils.appendEvent20605(buffer, documentI18n);
        } else if (EVENT_20606.equals(type)) {
            AppendEventsTwentySixHundredUtils.appendEvent20606(buffer, branchNode, documentI18n,
                defendantNames);
        } else if (EVENT_20607.equals(type)) {
            AppendEventsTwentySixHundredUtils.appendEvent20607(buffer, documentI18n);
        } else {
            append20600ExcessEvents(buffer, branchNode, documentI18n, defendantNames, type);
        }
    }

    private void append20600ExcessEvents(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_20608.equals(type)) {
            AppendEventsTwentySixHundredUtils.appendEvent20608(buffer, documentI18n);
        } else if (EVENT_20609.equals(type)) {
            AppendEventsTwentySixHundredUtils.appendEvent20609(buffer, documentI18n);
        } else if (EVENT_20610.equals(type)) {
            AppendEventsTwentySixHundredTenUtils.appendEvent20610(buffer, documentI18n);
        } else if (EVENT_20611.equals(type)) {
            AppendEventsTwentySixHundredTenUtils.appendEvent20611(buffer, documentI18n);
        } else if (EVENT_20612.equals(type)) {
            AppendEventsTwentySixHundredTenUtils.appendEvent20612(buffer, documentI18n);
        } else if (EVENT_20613.equals(type)) {
            AppendEventsTwentySixHundredTenUtils.appendEvent20613(buffer, branchNode, documentI18n);
        } else {
            append20900Events(buffer, branchNode, documentI18n, defendantNames, type);
        }
    }

    private void append20900Events(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_20901.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20901(buffer, branchNode, documentI18n);
        } else if (EVENT_20902.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20902(buffer, documentI18n);
        } else if (EVENT_20903.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20903(buffer, branchNode, documentI18n);
        } else if (EVENT_20904.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20904(buffer, branchNode, documentI18n);
        } else if (EVENT_20905.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20905(buffer, documentI18n);
        } else {
            append20900ExcessEvents(buffer, branchNode, documentI18n, defendantNames, type);
        }
    }

    private void append20900ExcessEvents(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_20906.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20906(buffer, branchNode, documentI18n,
                defendantNames);
        } else if (EVENT_20907.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20907(buffer, documentI18n);
        } else if (EVENT_20908.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20908(buffer, documentI18n);
        } else if (EVENT_20909.equals(type)) {
            AppendEventsTwentyNineHundredUtils.appendEvent20909(buffer, branchNode, documentI18n,
                defendantNames);
        } else {
            append20910To20930Events(buffer, branchNode, documentI18n, defendantNames, type);
        }
    }

    private void append20910To20930Events(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_20910.equals(type)) {
            AppendEventsTwentyNineHundredTenUtils.appendEvent20910(buffer, branchNode, documentI18n,
                defendantNames);
        } else if (EVENT_20911.equals(type)) {
            AppendEventsTwentyNineHundredTenUtils.appendEvent20911(buffer, documentI18n);
        } else if (EVENT_20912.equals(type)) {
            AppendEventsTwentyNineHundredTenUtils.appendEvent20912(buffer, documentI18n);
        } else if (EVENT_20914.equals(type)) {
            AppendEventsTwentyNineHundredTenUtils.appendEvent20914(buffer, documentI18n);
        } else if (EVENT_20916.equals(type)) {
            AppendEventsTwentyNineHundredTenUtils.appendEvent20916(buffer, documentI18n);
        } else if (EVENT_20917.equals(type)) {
            AppendEventsTwentyNineHundredTenUtils.appendEvent20917(buffer, documentI18n);
        } else {
            append20910To20930ExcessEvents(buffer, branchNode, documentI18n, defendantNames, type);
        }
    }

    private void append20910To20930ExcessEvents(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_20918.equals(type)) {
            AppendEventsTwentyNineHundredTenUtils.appendEvent20918(buffer, documentI18n);
        } else if (EVENT_20919.equals(type)) {
            AppendEventsTwentyNineHundredTenUtils.appendEvent20919(buffer, documentI18n);
        } else if (EVENT_20920.equals(type)) {
            AppendEventsTwentyNineHundredExcessUtils.appendEvent20920(buffer, branchNode,
                documentI18n);
        } else if (EVENT_20931.equals(type)) {
            AppendEventsTwentyNineHundredExcessUtils.appendEvent20931(buffer, branchNode,
                documentI18n);
        } else if (EVENT_20932.equals(type)) {
            AppendEventsTwentyNineHundredExcessUtils.appendEvent20932(buffer, branchNode,
                documentI18n);
        } else if (EVENT_20935.equals(type)) {
            AppendEventsTwentyNineHundredExcessUtils.appendEvent20935(buffer, branchNode,
                documentI18n);
        } else {
            append21000To40000Events(buffer, branchNode, documentI18n, defendantNames, type);
        }
    }

    private void append21000To40000Events(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_21100.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent21100(buffer, documentI18n);
        } else if (EVENT_21200.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent21200(buffer, documentI18n);
        } else if (EVENT_21201.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent21201(buffer, documentI18n);
        } else if (EVENT_30100.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent30100(buffer, branchNode,
                documentI18n);
        } else if (EVENT_30200.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent30200(buffer, branchNode,
                documentI18n, defendantNames);
        } else if (EVENT_30300.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent30300(buffer, documentI18n);
        } else if (EVENT_30400.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent30400(buffer, documentI18n);
        } else {
            append30000To40000Events(buffer, branchNode, documentI18n, defendantNames, type);
        }
    }

    private void append30000To40000Events(StringBuilder buffer, BranchEventXmlNode branchNode,
        TranslationBundle documentI18n, Collection<DefendantName> defendantNames, String type) {
        if (EVENT_30500.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent30500(buffer, documentI18n);
        } else if (EVENT_30600.equals(type)) {
            AppendEventsTwentyOneHundredToThirtyUtils.appendEvent30600(buffer, branchNode,
                documentI18n, defendantNames);
        } else if (EVENT_31000.equals(type)) {
            AppendEventsThirtyOneToFourtyUtils.appendEvent31000(buffer, branchNode, documentI18n);
        } else if (EVENT_32000.equals(type)) {
            AppendEventsThirtyOneToFourtyUtils.appendEvent32000(buffer, branchNode, documentI18n);
        } else if (EVENT_40601.equals(type)) {
            AppendEventsThirtyOneToFourtyUtils.appendEvent40601(buffer, documentI18n);
        } else if (CPP.equals(type)) {
            AppendEventsThirtyOneToFourtyUtils.appendEventCpp(buffer, branchNode, documentI18n);
        } else {
            LOG.error("{}{}", "Unrecognised Event Type: ", type);
        }
    }
}
