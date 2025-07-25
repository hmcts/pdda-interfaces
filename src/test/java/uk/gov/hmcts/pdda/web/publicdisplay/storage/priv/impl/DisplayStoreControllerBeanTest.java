package uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaystore.XhbDisplayStoreDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaystore.XhbDisplayStoreRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: DisplayStoreControllerBean Test.
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
 * @author Chris Vincent
 */
@ExtendWith(EasyMockExtension.class)
@SuppressWarnings({"PMD"})
class DisplayStoreControllerBeanTest {

    private static final String TRUE = "Result is not True";
    private static final String EQUALS = "Results are not Equal";
    private static final String RETRIEVAL_CODE = "RetrievalCode";
    private static final Long DISPLAY_STORE_ID = Long.valueOf(100);
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final String CONTENT = "Test Content";

    @Mock
    private XhbDisplayStoreRepository mockXhbDisplayStoreRepository;

    @Mock
    protected EntityManager mockEntityManager;

    private DisplayStoreControllerBean classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest =
            new DisplayStoreControllerBean(mockEntityManager, mockXhbDisplayStoreRepository);
    }


    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDoesEntryExistTrue() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
        Optional<XhbDisplayStoreDao> displayStoreDao =
            Optional.of(getDummyXhbDisplayStoreDao(DISPLAY_STORE_ID, RETRIEVAL_CODE));
        boolean result = testDoesEntryExist(displayStoreDao, true);
        assertTrue(result, TRUE);
    }

    @Test
    void testDoesEntryExistFalse() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
        Optional<XhbDisplayStoreDao> displayStoreDao = Optional.empty();
        boolean result = testDoesEntryExist(displayStoreDao, false);
        assertTrue(result, TRUE);
    }

    private boolean testDoesEntryExist(Optional<XhbDisplayStoreDao> displayStoreDao,
        boolean isValid) {
        EasyMock.expect(mockXhbDisplayStoreRepository.findByRetrievalCodeSafe(RETRIEVAL_CODE))
            .andReturn(displayStoreDao);
        EasyMock.replay(mockXhbDisplayStoreRepository);

        boolean result = classUnderTest.doesEntryExist(RETRIEVAL_CODE);

        EasyMock.verify(mockXhbDisplayStoreRepository);

        if (isValid) {
            assertTrue(result, "Result is not True");
        } else {
            assertFalse(result, "Result is not False");
        }

        return true;
    }

    @Test
    void testDeleteEntry() {
        Optional<XhbDisplayStoreDao> displayStoreDao =
            Optional.of(getDummyXhbDisplayStoreDao(DISPLAY_STORE_ID, RETRIEVAL_CODE));

        EasyMock.expect(mockXhbDisplayStoreRepository.findByRetrievalCodeSafe(RETRIEVAL_CODE))
            .andReturn(displayStoreDao);
        mockXhbDisplayStoreRepository.delete(displayStoreDao);
        EasyMock.replay(mockXhbDisplayStoreRepository);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);

        boolean result = false;
        try {
            classUnderTest.deleteEntry(RETRIEVAL_CODE);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        EasyMock.verify(mockXhbDisplayStoreRepository);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetLastModified() {
        final long lastmodified =
            Date.from(NOW.atZone(ZoneId.systemDefault()).toInstant()).getTime() / 1000 * 1000;
        Optional<XhbDisplayStoreDao> displayStoreDao =
            Optional.of(getDummyXhbDisplayStoreDao(DISPLAY_STORE_ID, RETRIEVAL_CODE));

        EasyMock.expect(mockXhbDisplayStoreRepository.findByRetrievalCodeSafe(RETRIEVAL_CODE))
            .andReturn(displayStoreDao);
        EasyMock.replay(mockXhbDisplayStoreRepository);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);

        long result = classUnderTest.getLastModified(RETRIEVAL_CODE);

        EasyMock.verify(mockXhbDisplayStoreRepository);
        assertEquals(lastmodified, result, EQUALS);
    }

    @Test
    void testReadFromDatabase() {
        Optional<XhbDisplayStoreDao> displayStoreDao =
            Optional.of(getDummyXhbDisplayStoreDao(DISPLAY_STORE_ID, RETRIEVAL_CODE));

        EasyMock.expect(mockXhbDisplayStoreRepository.findByRetrievalCodeSafe(RETRIEVAL_CODE))
            .andReturn(displayStoreDao);
        EasyMock.replay(mockXhbDisplayStoreRepository);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);

        String result = classUnderTest.readFromDatabase(RETRIEVAL_CODE);

        EasyMock.verify(mockXhbDisplayStoreRepository);
        assertEquals(CONTENT, result, EQUALS);
    }

    @Test
    void testWriteToDatabaseSave() {
        final String newContent = "New Test Content";

        EasyMock.expect(mockXhbDisplayStoreRepository.findByRetrievalCodeSafe(RETRIEVAL_CODE))
            .andReturn(Optional.empty());
        mockXhbDisplayStoreRepository.save(EasyMock.isA(XhbDisplayStoreDao.class));
        EasyMock.replay(mockXhbDisplayStoreRepository);
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);

        boolean result = false;
        try {
            classUnderTest.writeToDatabase(RETRIEVAL_CODE, newContent);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        EasyMock.verify(mockXhbDisplayStoreRepository);
        assertTrue(result, TRUE);
    }

    @Test
    void testWriteToDatabaseUpdate() {
        final String newContent = "New Test Content";
        XhbDisplayStoreDao dao = getDummyXhbDisplayStoreDao(DISPLAY_STORE_ID, RETRIEVAL_CODE);
        Optional<XhbDisplayStoreDao> displayStoreDao = Optional.of(dao);

        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.expect(mockXhbDisplayStoreRepository.findByRetrievalCodeSafe(RETRIEVAL_CODE))
            .andReturn(displayStoreDao);

        // Expect the new saveWithRetry call with any retry count (match exact if needed)
        mockXhbDisplayStoreRepository.saveWithRetry(EasyMock.eq(dao), EasyMock.anyInt());
        EasyMock.expectLastCall().once();

        EasyMock.replay(mockEntityManager, mockXhbDisplayStoreRepository);

        boolean result = false;
        try {
            classUnderTest.writeToDatabase(RETRIEVAL_CODE, newContent);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }

        EasyMock.verify(mockEntityManager, mockXhbDisplayStoreRepository);
        assertTrue(result, TRUE);
    }



    /**
     * Returns a dummy XhbDisplayStoreDao object for use in testing.
     * 
     * @param displayStoreId display store id to set
     * @param retrievalCode retrieval code to set
     * @return XhbDisplayStoreDao
     */
    private XhbDisplayStoreDao getDummyXhbDisplayStoreDao(final Long displayStoreId,
        final String retrievalCode) {
        String content = CONTENT;
        String obsInd = "N";
        LocalDateTime lastUpdateDate = NOW;
        LocalDateTime creationDate = NOW.minusMinutes(15);
        String lastUpdatedBy = "Test2";
        String createdBy = "Test1";
        Integer version = 2;

        XhbDisplayStoreDao xcsi = new XhbDisplayStoreDao(displayStoreId, retrievalCode, content,
            lastUpdateDate, creationDate, lastUpdatedBy, createdBy, version, obsInd);
        return new XhbDisplayStoreDao(xcsi);
    }

}
