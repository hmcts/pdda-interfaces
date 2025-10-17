package uk.gov.hmcts;

import uk.gov.courtservice.xhibit.common.publicdisplay.events.ActivateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.AddCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.CaseStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.ConfigurationChangeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.HearingStatusEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.MoveCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicNoticeEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.UpdateCaseEvent;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseChangeInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CaseCourtLogInformation;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.types.CourtRoomIdentifier;
import uk.gov.courtservice.xhibit.common.publicdisplay.types.configuration.CourtConfigurationChange;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogSubscriptionValue;
import uk.gov.courtservice.xhibit.courtlog.vos.CourtLogViewValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public final class DummyEventUtil {

    private static final String NO20603 = "20603";
    private static final String NO20901 = "20901";
    private static final String NO20904 = "20904";
    private static final String NO20935 = "20935";
    private static final String NO30200 = "30200";
    private static final String NO20903 = "20903";
    private static final String NO30100 = "30100";
    private static final String NOTNULL = "Result is Null";
    private static final String NULL = "Result is not Null";
    private static final String FALSE = "Result is not False";
    private static final String TEST_COURT = "Test Court";

    private static final String MOVE_CASE_EVENT = "Pdda_MoveCase";
    private static final String ADD_CASE_EVENT = "Pdda_AddCase";
    private static final String UPDATE_CASE_EVENT = "Pdda_UpdateCase";
    private static final String CASE_STATUS_EVENT = "Pdda_CaseStatus";
    private static final String ACTIVATE_CASE_EVENT = "Pdda_ActivateCase";
    private static final String HEARING_STATUS_EVENT = "Pdda_HearingStatus";
    private static final String PUBLIC_NOTICE_EVENT = "Pdda_PublicNotice";
    private static final String CONFIG_CHANGE_EVENT = "Pdda_ConfigurationChange";
    public static final String[] VALID_XHIBIT_MESSAGE_TYPE =
        {MOVE_CASE_EVENT, ADD_CASE_EVENT, UPDATE_CASE_EVENT, CASE_STATUS_EVENT, ACTIVATE_CASE_EVENT,
            HEARING_STATUS_EVENT, CONFIG_CHANGE_EVENT, PUBLIC_NOTICE_EVENT};

    private DummyEventUtil() {
        // Do nothing
    }

    public static MoveCaseEvent getMoveCaseEvent() {
        CourtRoomIdentifier from = new CourtRoomIdentifier(-99, null, TEST_COURT, 123);
        CourtRoomIdentifier to = new CourtRoomIdentifier(-1, null, TEST_COURT, 123);
        from.setCourtId(from.getCourtId());
        from.setCourtRoomId(from.getCourtRoomId());
        CaseChangeInformation caseChangeInformation = new CaseChangeInformation(true);
        MoveCaseEvent result = new MoveCaseEvent(from, to, caseChangeInformation);
        result.setFromCourtRoomIdentifier(result.getFromCourtRoomIdentifier());
        result.setToCourtRoomIdentifier(result.getToCourtRoomIdentifier());
        assertNotNull(result.getEventType(), NOTNULL);
        return result;
    }

    public static UpdateCaseEvent getUpdateCaseEvent() {
        CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(-99, null, TEST_COURT, 123);
        CaseChangeInformation caseUpdatedInfo = new CaseChangeInformation(false);
        caseUpdatedInfo.setCaseActive(caseUpdatedInfo.isCaseActive());
        UpdateCaseEvent result = new UpdateCaseEvent(courtRoom, caseUpdatedInfo);
        assertNotNull(result.getEventType(), NOTNULL);
        return result;
    }

    public static AddCaseEvent getAddCaseEvent() {
        CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(-99, null, TEST_COURT, 123);
        CaseChangeInformation caseUpdatedInfo = new CaseChangeInformation(false);
        caseUpdatedInfo.setCaseActive(caseUpdatedInfo.isCaseActive());
        AddCaseEvent result = new AddCaseEvent(courtRoom, caseUpdatedInfo);
        result.setCaseActive(result.isCaseActive());
        assertNotNull(result.getEventType(), NOTNULL);
        return result;
    }

    public static PublicNoticeEvent getPublicNoticeEvent() {
        CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(-99, null, TEST_COURT, 123);
        PublicNoticeEvent result = new PublicNoticeEvent(courtRoom, false);
        assertNotNull(result.getEventType(), NOTNULL);
        assertFalse(result.isReportingRestrictionsChanged(), FALSE);
        return result;
    }

    public static ActivateCaseEvent getActivateCaseEvent() {
        CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(-99, null, TEST_COURT, 123);
        CaseChangeInformation caseChangeInformation = new CaseChangeInformation(false);
        ActivateCaseEvent result = new ActivateCaseEvent(courtRoom, caseChangeInformation);
        assertNotNull(result.getEventType(), NOTNULL);
        return result;
    }

    public static ConfigurationChangeEvent getConfigurationChangeEvent() {
        CourtConfigurationChange courtConfigurationChange =
            new CourtConfigurationChange(-99, TEST_COURT);
        ConfigurationChangeEvent result = new ConfigurationChangeEvent(courtConfigurationChange);
        assertNotNull(result.getEventType(), NOTNULL);
        assertNotNull(result.getChange(), NOTNULL);
        return result;
    }

    public static HearingStatusEvent getHearingStatusEvent() {
        CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(-99, null, TEST_COURT, 123);
        CaseChangeInformation caseChangeInformation = new CaseChangeInformation(false);
        HearingStatusEvent result = new HearingStatusEvent(courtRoom, caseChangeInformation);
        assertNotNull(result.getEventType(), NOTNULL);
        assertNotNull(result.getCourtId(), NOTNULL);
        assertNotNull(result.getCaseChangeInformation(), NOTNULL);
        return result;
    }

    public static CaseStatusEvent getCaseStatusEvent() {
        CourtLogSubscriptionValue courtLogSubscriptionValue =
            DummyCourtUtil.getCourtLogSubscriptionValue();
        assertNull(courtLogSubscriptionValue.getCourtLogViewValue(), NULL);
        assertNull(courtLogSubscriptionValue.getDefendantOnCaseId(), NULL);
        assertNull(courtLogSubscriptionValue.getDefendantOnOffenceId(), NULL);
        assertNull(courtLogSubscriptionValue.getScheduledHearingId(), NULL);
        CourtLogViewValue courtLogViewValue = DummyCourtUtil.getCourtLogViewValue();
        courtLogSubscriptionValue.setCourtLogViewValue(courtLogViewValue);
        assertNull(courtLogSubscriptionValue.getDefendantOnCaseId(), NULL);
        assertNull(courtLogSubscriptionValue.getDefendantOnOffenceId(), NULL);
        assertNull(courtLogSubscriptionValue.getScheduledHearingId(), NULL);
        courtLogSubscriptionValue.getCourtLogViewValue().setDefendantOnCaseId(1);
        courtLogSubscriptionValue.getCourtLogViewValue().setDefendantOnOffenceId(1);
        courtLogSubscriptionValue.getCourtLogViewValue().setScheduledHearingId(1);
        assertNotNull(courtLogSubscriptionValue.getDefendantOnCaseId(), NOTNULL);
        assertNotNull(courtLogSubscriptionValue.getDefendantOnOffenceId(), NOTNULL);
        assertNotNull(courtLogSubscriptionValue.getScheduledHearingId(), NOTNULL);
        courtLogSubscriptionValue.setCourtSiteId(courtLogSubscriptionValue.getCourtSiteId());
        courtLogSubscriptionValue.setCourtRoomId(courtLogSubscriptionValue.getCourtRoomId());
        courtLogSubscriptionValue.setCourtUrn(courtLogSubscriptionValue.getCourtUrn());
        courtLogSubscriptionValue.setHearingId(courtLogSubscriptionValue.getHearingId());
        courtLogSubscriptionValue.setPnEventType(courtLogSubscriptionValue.getPnEventType());
        CaseCourtLogInformation caseCourtLogInformation =
            new CaseCourtLogInformation(courtLogSubscriptionValue, false);
        caseCourtLogInformation
            .setCourtLogSubscriptionValue(caseCourtLogInformation.getCourtLogSubscriptionValue());
        CourtRoomIdentifier courtRoom = new CourtRoomIdentifier(-99, null, TEST_COURT, 123);
        CaseStatusEvent result = new CaseStatusEvent(courtRoom, caseCourtLogInformation);
        assertNotNull(result.getEventType(), NOTNULL);
        assertNotNull(result.getCaseCourtLogInformation(), NOTNULL);
        assertNotNull(courtLogSubscriptionValue.getDefendantOnCaseId(), NOTNULL);
        return result;
    }

    public static PublicDisplayEvent getEvent(String messageType) {
        if (MOVE_CASE_EVENT.equals(messageType)) {
            return getMoveCaseEvent();
        } else if (ADD_CASE_EVENT.equals(messageType)) {
            return getAddCaseEvent();
        } else if (UPDATE_CASE_EVENT.equals(messageType)) {
            return getUpdateCaseEvent();
        } else if (CASE_STATUS_EVENT.equals(messageType)) {
            return getCaseStatusEvent();
        } else if (HEARING_STATUS_EVENT.equals(messageType)) {
            return getHearingStatusEvent();
        } else if (ACTIVATE_CASE_EVENT.equals(messageType)) {
            return getActivateCaseEvent();
        } else if (CONFIG_CHANGE_EVENT.equals(messageType)) {
            return getConfigurationChangeEvent();
        } else {
            return getPublicNoticeEvent();
        }
    }

    public static BranchEventXmlNode eventCheck(BranchEventXmlNode node, String event,
        String laoType) {
        if (NO30200.equals(event)) {
            node.add(DummyNodeUtil.getE30200LongAdjournOptions(laoType));
        } else if (NO20603.equals(event)) {
            node.add(DummyNodeUtil.getE20603WitnessSwornOptions(laoType));
        } else if (NO20901.equals(event)) {
            node.add(DummyNodeUtil.getE20901TimeEstimateOptions(laoType));
        } else if (NO20904.equals(event)) {
            node.add(DummyNodeUtil.getE20904WitnessSwornOptions(laoType));
        } else if (NO20903.equals(event)) {
            node.add(DummyNodeUtil.getE20903ProsecutionCaseOptions(laoType));
        } else if (NO20935.equals(event)) {
            node.add(DummyNodeUtil.getE20935WitnessReadOptions(laoType));
        } else if (NO30100.equals(event)) {
            node.add(DummyNodeUtil.getE30100ShortAdjournOptions(laoType));
        }
        return node;
    }
}
