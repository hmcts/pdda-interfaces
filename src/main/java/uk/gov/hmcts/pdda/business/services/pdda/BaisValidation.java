package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.courtservice.xhibit.common.publicdisplay.events.PublicDisplayEvent;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * BaisValidation.
 **/
public abstract class BaisValidation extends SftpValidation {
    private static final Logger LOG = LoggerFactory.getLogger(BaisValidation.class);

    private static final String FILENAME_DELIMETER = "_";
    private static final DateTimeFormatter YEARFORMAT = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter BATCH_FILENAME_DATETIMEFORMAT =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Integer DATE_LENGTH = 14;
    private static final Integer YEAR_MIN = 1900;
    private static final String EXTENSION = ".xml";
    
    protected XhbCourtRepository xhbCourtRepository;
    private final Integer noOfParts;


    protected BaisValidation(XhbCourtRepository courtRespository, boolean includeDirs, Integer noOfParts) {
        super(includeDirs);
        this.xhbCourtRepository = courtRespository;
        this.noOfParts = noOfParts;
    }

    public String[] getFilenameParts(String filename) {
        return filename.split(FILENAME_DELIMETER);
    }

    public boolean isValidNoOfParts(String filename) {
        return noOfParts.equals(getFilenameParts(filename).length);
    }

    protected String getFilenamePart(String filename, Integer partNo) {
        if (isValidNoOfParts(filename)) {
            return getFilenameParts(filename)[partNo];
        }
        return EMPTY_STRING;
    }
    
    public boolean validateDateTime(String part3Date) {
        String part3DateToUse = part3Date;
        // Remove the ".xml" off the end of the date string
        part3DateToUse = part3DateToUse.replaceAll(EXTENSION, "");

        // Check character length of the string == 14 Chars i.e YYYYmmddHHMMSS <- 14 Chars.
        if (part3DateToUse.length() != DATE_LENGTH) {
            LOG.debug("Date in an incorrect format - Length < 14");
            return false;
        }

        // Trying new Date format validation
        try {
            // First check is valid date
            LocalDateTime datePart = LocalDateTime.parse(part3DateToUse.trim(), BATCH_FILENAME_DATETIMEFORMAT);

            // Checking Year
            String year = YEARFORMAT.format(datePart);
            if (Integer.parseInt(year) < YEAR_MIN) {
                LOG.debug("Year less than 1900");
                return false;
            }

            return true;

        } catch (DateTimeException e) {
            LOG.debug("{}{}", "Date in an incorrect format - ", e.getMessage());
            return false;
        }
    }

    /**
     * Given a 3 digit crest court id, return the court id.
     * 
     * @param crestCourtId crest court id
     * @return court id
     */
    protected int getCourtIdFromCrestCourtId(String crestCourtId) {
        int courtId = 0;
        if (!EMPTY_STRING.equals(crestCourtId)) {
            List<XhbCourtDao> courtDao =
                xhbCourtRepository.findByCrestCourtIdValueSafe(crestCourtId);
            if (courtDao.isEmpty()) {
                LOG.debug("No court exists for crestCourtId {}", crestCourtId);
            } else {
                courtId = courtDao.get(0).getCourtId();
            }
        }
        return courtId;
    }

    public abstract boolean validateTitle(String filenamePart);

    public abstract String validateFilename(String filename, PublicDisplayEvent event);

    public abstract String validateFilename(String filename, PublicDisplayEvent event,
        boolean isList);

    public abstract Optional<XhbPddaMessageDao> getPddaMessageDao(PddaMessageHelper pddaMessageHelper,
        String filename);

    public abstract String getMessageType(String filename, PublicDisplayEvent event);

    public abstract Integer getCourtId(String filename, PublicDisplayEvent event);

    public abstract PublicDisplayEvent getPublicDisplayEvent(String filename, String fileContents);
}
