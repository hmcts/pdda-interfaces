package uk.gov.hmcts.pdda.business.services.xmlbinding.formatting;

import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.framework.exception.CsBusinessException;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.exception.formatting.FormattingException;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.xmlbinding.formatting.FormattingConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: Formatting Config Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
class FormattingConfigTest {

    private static final String LIST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<cs:DailyList xmlns:cs=\"http://www.courtservice.gov.uk/schemas/courtservice\" xmlns:apd=\"http://www.govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns=\"http://www.govtalk.gov.uk/people/AddressAndPersonalDetails\" xmlns:p2=\"http://www.govtalk.gov.uk/people/bs7666\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.courtservice.gov.uk/schemas/courtservice DailyList-v5-9.xsd\">"
        + "    <cs:DocumentID>" + "        <cs:DocumentName>DailyList_999_200108141220.xml</cs:DocumentName>"
        + "        <cs:UniqueID>0149051e-9db2-4b47-999e-fef76909ee73</cs:UniqueID>"
        + "        <cs:DocumentType>DL</cs:DocumentType>" + "    </cs:DocumentID>" + "    <cs:ListHeader>"
        + "        <cs:ListCategory>Criminal</cs:ListCategory>" + "        <cs:StartDate>2020-01-21</cs:StartDate>"
        + "        <cs:EndDate>2020-01-21</cs:EndDate>" + "        <cs:Version>NOT VERSIONED</cs:Version>"
        + "        <cs:PublishedTime>2020-01-07T18:45:03.000</cs:PublishedTime>"
        + "        <cs:CRESTlistID>510</cs:CRESTlistID>" + "    </cs:ListHeader>" + "    <cs:CrownCourt>"
        + "        <cs:CourtHouseType>Crown Court</cs:CourtHouseType>"
        + "        <cs:CourtHouseCode CourtHouseShortName=\"SNARE\">453</cs:CourtHouseCode>"
        + "        <cs:CourtHouseName>SNARESBROOK</cs:CourtHouseName>" + "    </cs:CrownCourt>" + "    <cs:CourtLists>"
        + "    </cs:CourtLists>" + "</cs:DailyList>";

    private static final String DOCTYPE_DAILY_LIST = "DL";
    private static final String HTM = "HTM";
    private static final String EMAIL = "EMAIL";
    private static final String TRUE = "Result is not True";


    @TestSubject
    private final FormattingConfig classUnderTest = FormattingConfig.Singleton.INSTANCE;

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetXslTransformsFailure() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Run
            classUnderTest.getXslTransforms(null);
        });
    }

    @Test
    void testFormattingException() {
        Assertions.assertThrows(FormattingException.class, () -> {
            throw new FormattingException("An error occured reading formatting config.", new CsBusinessException());
        });
        Assertions.assertThrows(FormattingException.class, () -> {
            throw new FormattingException("An error occured reading formatting config.");
        });
    }

    @Test
    void testGetXslTransformsSuccess() {
        // Setup
        boolean result;
        XhbClobDao xhbClobDao = getDummyXhbClobDao(LIST_XML);
        XhbCppListDao xhbCppListDao = getDummyCppList(xhbClobDao, DOCTYPE_DAILY_LIST);
        FormattingValue formattingValue =
            getDummyFormattingValue(xhbClobDao.getClobData(), DOCTYPE_DAILY_LIST, HTM, EMAIL, xhbCppListDao);
        // Run
        classUnderTest.getXslTransforms(formattingValue);
        result = true;

        assertTrue(result, TRUE);
    }

    private XhbClobDao getDummyXhbClobDao(String clobData) {
        Long clobId = Long.valueOf(1);
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = Integer.valueOf(3);
        XhbClobDao result =
            new XhbClobDao(clobId, clobData, lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version);
        return new XhbClobDao(result);
    }

    private XhbCppListDao getDummyCppList(XhbClobDao listClob, String listType) {
        Integer cppListId = getRandomInt();
        Integer courtCode = Integer.valueOf(81);
        LocalDateTime timeLoaded = null;
        LocalDateTime listStartDate = LocalDateTime.now().minusDays(5);
        LocalDateTime listEndDate = LocalDateTime.now().plusDays(1);
        Long mergedClobId = null;
        String status = null;
        String errorMessage = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = Integer.valueOf(3);
        String obsInd = "N";
        XhbCppListDao result = new XhbCppListDao();
        result.setCppListId(cppListId);
        result.setCourtCode(courtCode);
        result.setListType(listType);
        result.setTimeLoaded(timeLoaded);
        result.setListStartDate(listStartDate);
        result.setListEndDate(listEndDate);
        result.setListClobId(listClob.getClobId());
        result.setMergedClobId(mergedClobId);
        result.setStatus(status);
        result.setErrorMessage(errorMessage);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        result = new XhbCppListDao(result);
        return result;
    }

    private FormattingValue getDummyFormattingValue(String xml, String documentTypeIn, String mimeTypeIn,
        String distributionTypeIn, XhbCppListDao cppList) {
        Integer majorVersion = Integer.valueOf(1);
        Integer minorVersion = Integer.valueOf(1);
        String language = "language";
        String country = "country";
        try (Reader readerIn = new StringReader(xml)) {
            OutputStream outputStreamIn = new ByteArrayOutputStream(1024);
            Integer courtId = Integer.valueOf(81);

            FormattingValue result = new FormattingValue(distributionTypeIn, mimeTypeIn, documentTypeIn, majorVersion,
                minorVersion, language, country, readerIn, outputStreamIn, courtId, cppList);
            result.setFormattingId(getRandomInt());
            return result;
        } catch (IOException exception) {
            fail(exception);
            return null;
        }
    }

    private Integer getRandomInt() {
        return Double.valueOf(Math.random()).intValue();
    }
}
