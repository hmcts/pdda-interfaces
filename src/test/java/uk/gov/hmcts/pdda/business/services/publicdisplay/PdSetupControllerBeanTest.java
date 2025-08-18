
package uk.gov.hmcts.pdda.business.services.publicdisplay;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationRepository;
import uk.gov.hmcts.pdda.business.services.publicdisplay.setup.ejb.PdSetupControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown.CourtDrillDown;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**

 * Title: PDSetupControllerBean Test.


 * Description:


 * Copyright: Copyright (c) 2022


 * Company: CGI

 * @author Chris Vincent
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class PdSetupControllerBeanTest {

    private static final String EQUALS = "Results are not Equal";
    private static final String TRUE = "Result is not True";
    private static final String CLEAR_REPOSITORIES_MESSAGE =
        "Repository should be null after clearRepositories()";

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;

    @Mock
    private XhbDisplayLocationRepository mockXhbDisplayLocationRepository;

    @Mock
    private XhbDisplayRepository mockXhbDisplayRepository;

    @TestSubject
    private final PdSetupControllerBean classUnderTest =
        new PdSetupControllerBean(mockEntityManager);

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testDefaultConstructor() {
        boolean result = false;
        try {
            new PdSetupControllerBean();
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        assertTrue(result, TRUE);
    }

    @Test
    @SuppressWarnings("unused")
    void testGetDrillDownForCourt() {
        // Setup
        Integer courtId = 94;
        XhbCourtDao courtDao = DummyCourtUtil.getXhbCourtDao(courtId, "Test Court");
        List<XhbCourtSiteDao> courtSiteList = (ArrayList<XhbCourtSiteDao>) getDummyCourtSiteList();

        EasyMock.expect(mockXhbCourtRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock.expect(mockXhbCourtRepository.findByIdSafe(courtId))
            .andReturn(Optional.of(courtDao));
        EasyMock
            .expect(
                mockXhbCourtSiteRepository.findByCrestCourtIdValueSafe(EasyMock.isA(String.class)))
            .andReturn(courtSiteList);

        List<XhbDisplayLocationDao> displayLocationList;
        List<XhbDisplayDao> displaysList;

        for (XhbCourtSiteDao xhbCourtSite : courtSiteList) {
            displayLocationList = (ArrayList<XhbDisplayLocationDao>) getDummyDisplayLocationList();
            EasyMock
                .expect(
                    mockXhbDisplayLocationRepository
                        .findByCourtSiteSafe(EasyMock.isA(Integer.class)))
                .andReturn(displayLocationList);

            for (XhbDisplayLocationDao displayLocation : displayLocationList) {
                displaysList = (ArrayList<XhbDisplayDao>) getDummyDisplayList();
                EasyMock
                    .expect(mockXhbDisplayRepository
                        .findByDisplayLocationIdSafe(EasyMock.isA(Integer.class)))
                    .andReturn(displaysList);
            }
        }
        EasyMock.expect(mockXhbDisplayLocationRepository.getEntityManager())
            .andReturn(mockEntityManager).anyTimes();

        replayMocks();

        // Run
        CourtDrillDown result = classUnderTest.getDrillDownForCourt(courtId);

        // Checks
        verifyMocks();
        assertEquals(2, result.getValues().size(), EQUALS);
    }

    @Test
    void testGetDrillDownForCourtFailure() {
        // Setup
        Integer courtId = 94;

        Assertions.assertThrows(CourtNotFoundException.class, () -> {
            
            EasyMock.expect(mockXhbCourtRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
            EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
            
            EasyMock.expect(mockXhbCourtRepository.findByIdSafe(courtId))
                .andReturn(Optional.empty());
            EasyMock.replay(mockXhbCourtRepository);
            EasyMock.replay(mockEntityManager);
            // Run
            classUnderTest.getDrillDownForCourt(courtId);
        });
    }

    @Test
    void testGetAllCourts() {
        // Setup
        List<XhbCourtDao> dummyCourtList = new ArrayList<>();
        dummyCourtList.add(DummyCourtUtil.getXhbCourtDao(1, "TestCourt1"));
        dummyCourtList.add(DummyCourtUtil.getXhbCourtDao(3, "TestCourt3"));
        dummyCourtList.add(DummyCourtUtil.getXhbCourtDao(5, "TestCourt5"));
        
        EasyMock.expect(mockXhbCourtRepository.getEntityManager()).andReturn(mockEntityManager).anyTimes();
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        
        EasyMock.expect(mockXhbCourtRepository.findAllSafe()).andReturn(dummyCourtList);
        replayMocks();

        // Run
        XhbCourtDao[] result = classUnderTest.getAllCourts();

        // Checks
        verifyMocks();
        assertArrayEquals(dummyCourtList.toArray(), result, EQUALS);
    }

    /**
     * Replay the mocked objects.
     */
    private void replayMocks() {
        EasyMock.replay(mockXhbCourtRepository);
        EasyMock.replay(mockXhbCourtSiteRepository);
        EasyMock.replay(mockXhbDisplayLocationRepository);
        EasyMock.replay(mockXhbDisplayRepository);
        EasyMock.replay(mockEntityManager);
    }

    /**
     * Verify the mocked objects.
     */
    private void verifyMocks() {
        EasyMock.verify(mockXhbCourtRepository);
        EasyMock.verify(mockXhbCourtSiteRepository);
        EasyMock.verify(mockEntityManager);
    }

    private List<XhbCourtSiteDao> getDummyCourtSiteList() {
        List<XhbCourtSiteDao> siteList = new ArrayList<>();
        siteList.add(DummyCourtUtil.getXhbCourtSiteDao());
        siteList.add(DummyCourtUtil.getXhbCourtSiteDao());
        return siteList;
    }

    private List<XhbDisplayLocationDao> getDummyDisplayLocationList() {
        List<XhbDisplayLocationDao> xdlList = new ArrayList<>();
        xdlList.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        xdlList.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        return xdlList;
    }

    private List<XhbDisplayDao> getDummyDisplayList() {
        List<XhbDisplayDao> xdList = new ArrayList<>();
        xdList.add(DummyPublicDisplayUtil.getXhbDisplayDao());
        xdList.add(DummyPublicDisplayUtil.getXhbDisplayDao());
        return xdList;
    }

    @SuppressWarnings({"PMD.UseExplicitTypes", "PMD.AvoidAccessibilityAlteration"})
    @Test
    void testClearRepositoriesSetsRepositoryToNull() throws Exception {
        // Given
        classUnderTest.clearRepositories();

        // Use reflection to check the private field
        var field = PdSetupControllerBean.class.getDeclaredField("xhbDisplayLocationRepository");
        field.setAccessible(true);
        Object repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);

        // Use reflection to check the private field
        field = PdSetupControllerBean.class.getDeclaredField("xhbDisplayRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);

        // Use reflection to check the private field
        field = PdSetupControllerBean.class.getDeclaredField("xhbCourtSiteRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);

    }
}
