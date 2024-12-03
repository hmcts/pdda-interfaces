package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * Title: ListObjectHelper.
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
 * @author HarrisM
 * @version 1.0
 */
@SuppressWarnings({"PMD.NullAssignment", "PMD.TooManyMethods", "PMD.ExcessiveParameterList"})
public class ListObjectHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ListObjectHelper.class);
    private static final String COURTHOUSECODE = "cs:CourtHouseCode";
    private static final String COURTHOUSENAME = "cs:CourtHouseName";
    private static final String COURTROOMNO = "cs:CourtRoomNo";
    private static final String PUBLISHEDTIME = "cs:PublishedTime";
    private static final String STARTDATE = "cs:StartDate";
    private static final String VERSION = "cs:Version";
    private static final String[] COURTSITE_NODES = {COURTHOUSECODE, COURTHOUSENAME};
    private static final String[] COURTROOM_NODES = {COURTROOMNO};

    private final DataHelper dataHelper = new DataHelper();
    private Optional<XhbCourtSiteDao> xhbCourtSiteDao;
    private Optional<XhbCourtRoomDao> xhbCourtRoomDao;

    public void validateNodeMap(Map<String, String> nodesMap, String lastEntryName) {
        if (Arrays.asList(COURTSITE_NODES).contains(lastEntryName)) {
            xhbCourtSiteDao = validateCourtSite(nodesMap);
            validateHearingList(nodesMap);
        } else if (Arrays.asList(COURTROOM_NODES).contains(lastEntryName)) {
            xhbCourtRoomDao = validateCourtRoom(nodesMap);
        }
    }

    private Optional<XhbCourtSiteDao> validateCourtSite(Map<String, String> nodesMap) {
        LOG.info("validateCourtSite()");
        String courtHouseName = nodesMap.get(COURTHOUSENAME);
        String courtHouseCode = nodesMap.get(COURTHOUSECODE);
        if (courtHouseName != null && courtHouseCode != null) {
            return dataHelper.validateCourtSite(courtHouseName, courtHouseCode);
        }
        return Optional.empty();
    }

    private Optional<XhbCourtRoomDao> validateCourtRoom(Map<String, String> nodesMap) {
        LOG.info("validateCourtRoom()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtSiteId = xhbCourtSiteDao.get().getCourtSiteId();
            Integer crestCourtRoomNo = Integer.valueOf(nodesMap.get(COURTROOMNO));
            if (courtSiteId != null && crestCourtRoomNo != null) {
                return dataHelper.validateCourtRoom(courtSiteId, crestCourtRoomNo);
            }
        }
        return Optional.empty();
    }

    private void validateHearingList(Map<String, String> nodesMap) {
        LOG.info("validateHearingList()");
        if (xhbCourtSiteDao.isPresent()) {
            Integer courtId = xhbCourtSiteDao.get().getCourtId();
            Integer crestListId = 1;
            String listType = "D";
            String status = nodesMap.get(VERSION).substring(0, 5);
            String startDateString = nodesMap.get(STARTDATE)+"T00:00:00.000";
            LocalDateTime startDate = parseDate(startDateString, DateTimeFormatter.ISO_DATE_TIME);
            String publishedTimeString = nodesMap.get(PUBLISHEDTIME);
            LocalDateTime publishedTime = parseDate(publishedTimeString, DateTimeFormatter.ISO_DATE_TIME);
            final String printReference = "/";
            if (courtId != null && status != null && startDate != null) {
                dataHelper.validateHearingList(courtId, crestListId, listType, status, startDate,
                    publishedTime, printReference);
            }
        }
    }

    private LocalDateTime parseDate(String dateAsString, DateTimeFormatter dateFormat) {
        try {
            return LocalDateTime.parse(dateAsString, dateFormat);
        } catch (DateTimeParseException ex) {
            LOG.error("Unable to format date string {} to {}", dateAsString, dateFormat);
            return null;
        }
    }
}
