package uk.gov.hmcts.pdda.business.services.pdda;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyFileUtil;
import uk.gov.hmcts.DummyFileUtil.FileResults;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.services.pdda.sftp.SftpService.BaisCpValidation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * <p>
 * Title: PddaHelperValidationTest.
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
class PddaHelperValidationTest {
    
    private static final String SAME = "Result is not Same";
    private static final String FALSE = "Result is not False";
    private static final String[] VALID_CP_MESSAGE_TYPE = {"DailyList", "WarnList"};
    private static final String FILE_CONTENTS = " file contents";

  
    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @TestSubject
    private final BaisCpValidation cpValidationUnderTest = new BaisCpValidation(mockXhbCourtRepository);

    
    @Test
    void testCpValidationCrestCourtSuccess() {
        // Setup
        boolean expectedResult = true;
        // Run
        boolean actualResult = testValidationCrestCourt(true);
        // Checks
        assertSame(expectedResult, actualResult, SAME);
    }

    @Test
    void testCpValidationCrestCourtFailure() {
        // Setup
        boolean expectedResult = false;
        // Run
        boolean actualResult = testValidationCrestCourt(false);
        // Checks
        assertNotSame(expectedResult, actualResult, FALSE);
    }
    
    @Test 
    void testValidateDateTimeInvalidLength() {
        assertFalse(cpValidationUnderTest.validateDateTime("01"), FALSE);
    }
    
    @Test 
    void testValidateDateTimeInvalidFormat() {
        assertFalse(cpValidationUnderTest.validateDateTime("*%@*%@*%@*%@*%"), FALSE);
    }
    
    private boolean testValidationCrestCourt(boolean isSuccessTest) {
        // Setup
        List<FileResults> dummyFiles = getAllValidCpFiles(true);
        String filename = dummyFiles.get(0).filename;
        List<XhbCourtDao> xhbCourtDaoList = isSuccessTest ? getXhbCourtDaoList() : new ArrayList<>();
        EasyMock.expect(mockXhbCourtRepository.findByCrestCourtIdValue(EasyMock.isA(String.class)))
            .andReturn(xhbCourtDaoList);
        EasyMock.replay(mockXhbCourtRepository);
        // Run
        String result = cpValidationUnderTest.validateFilename(filename, null);
        // Checks
        EasyMock.verify(mockXhbCourtRepository);
        return result == null;

    }
    
    private List<FileResults> getAllValidCpFiles(boolean isValid) {
        List<FileResults> result = new ArrayList<>();
        String nowAsString = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        FileResults fileResult;
        for (String messageType : VALID_CP_MESSAGE_TYPE) {
            fileResult = DummyFileUtil.getFileResults();
            fileResult.filename = messageType + "_453_" + nowAsString + ".xml";
            fileResult.fileContents = messageType + FILE_CONTENTS;
            fileResult.isValid = isValid;
            String filenameToTest = VALID_CP_MESSAGE_TYPE[1];
            String filename = fileResult.filename;
            fileResult.alreadyProcessedTest = filename.startsWith(filenameToTest);
            result.add(fileResult);
        }
        return result;
    }

    private List<XhbCourtDao> getXhbCourtDaoList() {
        List<XhbCourtDao> result = new ArrayList<>();
        result.add(DummyCourtUtil.getXhbCourtDao(-453, "Court1"));
        return result;
    }
}
