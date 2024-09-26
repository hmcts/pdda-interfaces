package uk.gov.hmcts;

import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbhearinglist.XhbHearingListDao;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeDao;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantDao;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingDao;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingDao;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DummyHearingUtil {
    
    private static final String NOTNULL = "Result is Null";
    private static final String TEST1 = "Test1";
    private static final String TEST2 = "Test2";

    private DummyHearingUtil() {
        // Do nothing
    }
    
    public static XhbRefHearingTypeDao getXhbRefHearingTypeDao() {
        Integer refHearingTypeId = -1;
        String hearingTypeCode = "hearingTypeCode";
        String hearingTypeDesc = "hearingTypeDesc";
        String category = "category";
        Integer seqNo = -1;
        Integer listSequence = -1;
        Integer courtId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        String obsInd = "N";
        XhbRefHearingTypeDao result = new XhbRefHearingTypeDao();
        result.setRefHearingTypeId(refHearingTypeId);
        result.setHearingTypeCode(hearingTypeCode);
        result.setHearingTypeDesc(hearingTypeDesc);
        result.setCategory(category);
        result.setSeqNo(seqNo);
        result.setListSequence(listSequence);
        result.setCourtId(courtId);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        refHearingTypeId = result.getPrimaryKey();
        assertNotNull(refHearingTypeId, NOTNULL);
        return new XhbRefHearingTypeDao(result);
    }

    public static XhbHearingDao getXhbHearingDao() {
        Integer hearingId = -1;
        Integer caseId = -1;
        Integer refHearingTypeId = -1;
        Integer courtId = -1;
        String mpHearingType = "mpHearingType";
        Double lastCalculatedDuration = Double.valueOf(-1);
        LocalDateTime hearingStartDate = LocalDateTime.now();
        LocalDateTime hearingEndDate = LocalDateTime.now();
        Integer linkedHearingId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        XhbHearingDao result = new XhbHearingDao();
        result.setHearingId(hearingId);
        result.setCaseId(caseId);
        result.setRefHearingTypeId(refHearingTypeId);
        result.setCourtId(courtId);
        result.setMpHearingType(mpHearingType);
        result.setLastCalculatedDuration(lastCalculatedDuration);
        result.setHearingStartDate(hearingStartDate);
        result.setHearingEndDate(hearingEndDate);
        result.setLinkedHearingId(linkedHearingId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        hearingId = result.getPrimaryKey();
        assertNotNull(hearingId, NOTNULL);
        return new XhbHearingDao(result);
    }
    
    public static XhbHearingListDao getXhbHearingListDao() {
        Integer hearingListId = -1;
        String listType = "listType";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        String status = "status";
        Integer editionNo = -1;
        LocalDateTime publishedTime = LocalDateTime.now();
        String printReference = "printReference";
        Integer crestListId = -1;
        Integer courtId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        XhbHearingListDao result = new XhbHearingListDao();
        result.setListId(hearingListId);
        result.setListType(listType);
        result.setStartDate(startDate);
        result.setEndDate(endDate);
        result.setStatus(status);
        result.setEditionNo(editionNo);
        result.setPublishedTime(publishedTime);
        result.setPrintReference(printReference);
        result.setCrestListId(crestListId);
        result.setCourtId(courtId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        hearingListId = result.getPrimaryKey();
        assertNotNull(hearingListId, NOTNULL);
        return new XhbHearingListDao(result);
    }
    
    public static XhbSittingDao getXhbSittingDao() {
        Integer sittingId = -1;
        Integer sittingSequenceNo = -1;
        String sittingJudge = "isSittingJudge";
        LocalDateTime sittingTime = LocalDateTime.now();
        String sittingNote = "sittingNote";
        Integer refJustice1Id = -1;
        Integer refJustice2Id = -1;
        Integer refJustice3Id = -1;
        Integer refJustice4Id = -1;
        String floating = "isFloating";
        Integer listId = -1;
        Integer refJudgeId = -1;
        Integer courtRoomId = 8112;
        Integer courtSiteId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        XhbSittingDao result = new XhbSittingDao();
        result.setSittingId(sittingId);
        result.setSittingSequenceNo(sittingSequenceNo);
        result.setIsSittingJudge(sittingJudge);
        result.setSittingTime(sittingTime);
        result.setSittingNote(sittingNote);
        result.setRefJustice1Id(refJustice1Id);
        result.setRefJustice2Id(refJustice2Id);
        result.setRefJustice3Id(refJustice3Id);
        result.setRefJustice4Id(refJustice4Id);
        result.setIsFloating(floating);
        result.setListId(listId);
        result.setRefJudgeId(refJudgeId);
        result.setCourtRoomId(courtRoomId);
        result.setCourtSiteId(courtSiteId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        sittingId = result.getPrimaryKey();
        assertNotNull(sittingId, NOTNULL);
        result.setXhbCourtSite(DummyCourtUtil.getXhbCourtSiteDao());
        result.setXhbCourtRoom(DummyCourtUtil.getXhbCourtRoomDao());
        return result;
    }

    public static XhbScheduledHearingDao getXhbScheduledHearingDao() {
        Integer scheduledHearingId = -1;
        Integer sequenceNo = -1;
        LocalDateTime notBeforeTime = LocalDateTime.now();
        LocalDateTime originalTime = LocalDateTime.now();
        String listingNote = "listingNote";
        Integer hearingProgress = -1;
        Integer sittingId = -1;
        Integer hearingId = -1;
        String caseActive = "Y";
        String movedFrom = "movedFrom";
        Integer movedFromCourtRoomId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        XhbScheduledHearingDao result = new XhbScheduledHearingDao();
        result.setScheduledHearingId(scheduledHearingId);
        result.setSequenceNo(sequenceNo);
        result.setNotBeforeTime(notBeforeTime);
        result.setOriginalTime(originalTime);
        result.setListingNote(listingNote);
        result.setHearingProgress(hearingProgress);
        result.setSittingId(sittingId);
        result.setHearingId(hearingId);
        result.setIsCaseActive(caseActive);
        result.setMovedFrom(movedFrom);
        result.setMovedFromCourtRoomId(movedFromCourtRoomId);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        scheduledHearingId = result.getPrimaryKey();
        assertNotNull(scheduledHearingId, NOTNULL);
        result.setXhbCrLiveDisplays(result.getXhbCrLiveDisplays());
        result.setXhbSitting(getXhbSittingDao());
        return result;
    }
    
    public static XhbSchedHearingDefendantDao getXhbSchedHearingDefendantDao() {
        Integer schedHearingDefendantId = -1;
        Integer scheduledHearingId = -1;
        Integer defendantOnCaseId = -1;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = TEST2;
        String createdBy = TEST1;
        Integer version = 3;
        XhbSchedHearingDefendantDao result =
            new XhbSchedHearingDefendantDao(schedHearingDefendantId, scheduledHearingId,
                defendantOnCaseId, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        schedHearingDefendantId = result.getPrimaryKey();
        assertNotNull(schedHearingDefendantId, NOTNULL);
        return new XhbSchedHearingDefendantDao(result);
    }
}
