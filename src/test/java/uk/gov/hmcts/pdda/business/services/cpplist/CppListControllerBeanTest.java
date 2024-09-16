package uk.gov.hmcts.pdda.business.services.cpplist;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListDao;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: Cpp List Controller Bean Test.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2022
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class CppListControllerBeanTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String SAME = "Results are not Same";
    
    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCppListRepository mockcppListRepo;

    @TestSubject
    private final CppListControllerBean classUnderTest = new CppListControllerBean(mockEntityManager);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetLatestCppList() {
        // Setup
        XhbCppListDao xhbCppListDao = getDummyXhbCppListDao();
        List<XhbCppListDao> xhbCppListDaoList = new ArrayList<>();
        xhbCppListDaoList.add(xhbCppListDao);
        EasyMock.expect(mockcppListRepo.findByCourtCodeAndListTypeAndListDate(xhbCppListDao.getCourtCode(),
            xhbCppListDao.getListType(), xhbCppListDao.getListStartDate())).andReturn(xhbCppListDaoList);
        EasyMock.replay(mockcppListRepo);
        // Run
        XhbCppListDao actualResult = classUnderTest.getLatestCppList(xhbCppListDao.getCourtCode(),
            xhbCppListDao.getListType(), xhbCppListDao.getListStartDate());
        // Checks
        EasyMock.verify(mockcppListRepo);
        assertNotNull(actualResult, NOTNULL);
        assertSame(xhbCppListDao, actualResult, SAME);
    }

    @Test
    void testUpdateCppList() {
        // Setup
        XhbCppListDao xhbCppListDao = getDummyXhbCppListDao();
        EasyMock.expect(mockcppListRepo.update(xhbCppListDao)).andReturn(Optional.of(xhbCppListDao));
        EasyMock.replay(mockcppListRepo);
        // Run
        boolean result = false;
        try {
            classUnderTest.updateCppList(xhbCppListDao);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockcppListRepo);
        assertTrue(result, TRUE);
    }

    @Test
    void testCheckForExistingCppListRecord() {
        // Setup
        XhbCppListDao xhbCppListDao = getDummyXhbCppListDao();
        List<XhbCppListDao> xhbCppListDaoList = new ArrayList<>();
        xhbCppListDaoList.add(xhbCppListDao);
        EasyMock
            .expect(
                mockcppListRepo.findByCourtCodeAndListTypeAndListStartDateAndListEndDate(xhbCppListDao.getCourtCode(),
                    xhbCppListDao.getListType(), xhbCppListDao.getListStartDate(), xhbCppListDao.getListEndDate()))
            .andReturn(xhbCppListDaoList);
        EasyMock.replay(mockcppListRepo);
        // Run
        XhbCppListDao actualResult = classUnderTest.checkForExistingCppListRecord(xhbCppListDao.getCourtCode(),
            xhbCppListDao.getListType(), xhbCppListDao.getListStartDate(), xhbCppListDao.getListEndDate());
        // Checks
        EasyMock.verify(mockcppListRepo);
        assertNotNull(actualResult, NOTNULL);
        assertSame(xhbCppListDao, actualResult, SAME);
    }

    private XhbCppListDao getDummyXhbCppListDao() {

        Integer cppListId = Double.valueOf(Math.random()).intValue();
        Integer courtCode = 453;
        String listType = "D";
        LocalDateTime timeLoaded = LocalDateTime.now();
        LocalDateTime listStartDate = LocalDateTime.now().minusMinutes(5);
        LocalDateTime listEndDate = LocalDateTime.now();
        Long listClobId = Double.valueOf(Math.random()).longValue();
        Long mergedClobId = Double.valueOf(Math.random()).longValue();
        String status = "MS";
        String errorMessage = null;
        LocalDateTime lastUpdateDate = LocalDateTime.now();
        LocalDateTime creationDate = LocalDateTime.now().minusMinutes(1);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 3;
        String obsInd = "N";

        XhbCppListDao result = new XhbCppListDao();
        result.setCppListId(cppListId);
        result.setCourtCode(courtCode);
        result.setListType(listType);
        result.setTimeLoaded(timeLoaded);
        result.setListStartDate(listStartDate);
        result.setListEndDate(listEndDate);
        result.setListClobId(listClobId);
        result.setMergedClobId(mergedClobId);
        result.setStatus(status);
        result.setErrorMessage(errorMessage);
        result.setObsInd(obsInd);
        result.setLastUpdateDate(lastUpdateDate);
        result.setCreationDate(creationDate);
        result.setLastUpdatedBy(lastUpdatedBy);
        result.setCreatedBy(createdBy);
        result.setVersion(version);
        return new XhbCppListDao(result);
    }
}
