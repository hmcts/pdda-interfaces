package uk.gov.hmcts.pdda.web.publicdisplay.events;

import uk.gov.hmcts.pdda.business.entities.xhbcrlivedisplay.XhbCrLiveDisplayDao;
import uk.gov.hmcts.pdda.business.services.pdda.data.RepositoryHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("PMD")
public final class CrLiveEventHelper {
    
    private static RepositoryHelper repositoryHelper;
    
    private CrLiveEventHelper() {
        // Utility class
    }
    
    public static void populateEventIfPresent(PublicDisplayValue value) {
        if (value == null || value.getCourtRoomId() == null) {
            return;
        }

        Optional<XhbCrLiveDisplayDao> daoOpt =
            getRepositoryHelper().getXhbCrLiveDisplayRepository()
            .findByCourtRoomSafe(value.getCourtRoomId());

        if (daoOpt.isEmpty()) {
            return;
        }

        /*String xml = daoOpt.get().getStatus();
        CrLiveEventXmlParser.parse(xml).ifPresent(pr -> {
            value.setEvent(pr.node);       // BranchEventXmlNode
            if (pr.eventTime != null) {
                value.setEventTime(pr.eventTime); // LocalDateTime
            }
        });*/
        
        Optional<CrLiveEventXmlParser.ParseResult> opt = CrLiveEventXmlParser.parse(daoOpt.get().getStatus());
        if (opt.isEmpty()) {
            return;
        }
        CrLiveEventXmlParser.ParseResult pr = opt.get();

        // If the row is a defendant/ hearing row, only attach if it matches
        if (value instanceof AllCaseStatusValue acsv) {
            // Prefer the strongest ID match available
            if (pr.scheduledHearingId != null && Objects.equals(pr.scheduledHearingId, acsv.getScheduledHearingId())) {
                attach(pr, acsv);
                return;
            }
            if (pr.defendantOnCaseId != null && Objects.equals(pr.defendantOnCaseId, acsv.getDefendantOnCaseId())) {
                attach(pr, acsv);
                return;
            }
            if (pr.hearingId != null && Objects.equals(pr.hearingId, acsv.getHearingId())) {
                attach(pr, acsv);
                return;
            }
            // Fallback: compare defendant name (case-insensitive) + case number
            String eventDefendant = pr.defendantName;
            if (eventDefendant != null && eventDefendant.equalsIgnoreCase(acsv.getDefendantName().getName())) {
                attach(pr, acsv);
            }
            // no match -> do not attach
            return;
        }

        // For other value types (e.g. CourtDetailValue) you can attach by room-level logic
        value.setEvent(pr.node);
        if (pr.eventTime != null) {
            value.setEventTime(pr.eventTime);
        }
    }
    
    private static void attach(CrLiveEventXmlParser.ParseResult pr, PublicDisplayValue v) {
        v.setEvent(pr.node);
        if (pr.eventTime != null) {
            v.setEventTime(pr.eventTime);
        }
    }
    
    private static RepositoryHelper getRepositoryHelper() {
        if (repositoryHelper == null) {
            repositoryHelper = new RepositoryHelper();
        }
        return repositoryHelper;
    }
}

