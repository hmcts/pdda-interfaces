package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcase.XhbCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogentry.XhbCourtLogEntryRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogeventdesc.XhbCourtLogEventDescDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtlogeventdesc.XhbCourtLogEventDescRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: PublicDisplayQueryLogEntry Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class PublicDisplayQueryLogEntryTest {

    private static final String TRUE = "Result is Not True";
    private static final String NOT_INSTANCE = "Result is Not An Instance of";
    private static final String TEST_XML =
        "<testNode><testChildNode>courtroomname</testChildNode></testNode>";
    private static final String CLEAR_REPOSITORIES_MESSAGE =
        "Repository should be null after clearRepositories()";

    @Mock
    private PublicDisplayValue mockPublicDisplayValue;

    @Mock
    private XhbCourtLogEntryRepository mockXhbCourtLogEntryRepository;

    @Mock
    private XhbCourtLogEventDescRepository mockXhbCourtLogEventDescRepository;

    @Mock
    protected EntityManager mockEntityManager;

    @TestSubject
    private final PublicDisplayQueryLogEntry classUnderTest =
        new PublicDisplayQueryLogEntry(EasyMock.createMock(EntityManager.class));

    @BeforeEach
    void setupEntityManager() {
        EasyMock.expect(mockEntityManager.isOpen()).andReturn(true).anyTimes();
        EasyMock.replay(mockEntityManager);
    }

    @Test
    void testPopulateEventData() {
        // Setup
        final boolean result = true;
        XhbCourtLogEntryDao xhbCourtLogEntryDao = DummyCourtUtil.getXhbCourtLogEntryDao();
        xhbCourtLogEntryDao.setLogEntryXml(TEST_XML);
        List<XhbCourtLogEntryDao> xhbCourtLogEntryDaos = new ArrayList<>();
        xhbCourtLogEntryDaos.add(xhbCourtLogEntryDao);
        xhbCourtLogEntryDaos.add(xhbCourtLogEntryDao);

        EasyMock
            .expect(mockXhbCourtLogEntryRepository.findByCaseIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(xhbCourtLogEntryDaos);

        XhbCourtLogEventDescDao xhbCourtLogEventDescDao = new XhbCourtLogEventDescDao();
        xhbCourtLogEventDescDao.setEventType(30_300);

        EasyMock
            .expect(mockXhbCourtLogEventDescRepository.findByIdSafe(EasyMock.isA(Integer.class)))
            .andReturn(Optional.of(xhbCourtLogEventDescDao));
        EasyMock.expectLastCall().times(2);

        EasyMock.replay(mockXhbCourtLogEntryRepository);
        EasyMock.replay(mockXhbCourtLogEventDescRepository);

        // Run
        classUnderTest.populateEventData(mockPublicDisplayValue, 1);

        // Checks
        EasyMock.verify(mockXhbCourtLogEntryRepository);
        EasyMock.verify(mockXhbCourtLogEventDescRepository);
        assertTrue(result, TRUE);
    }

    @Test
    void testGetXhbCaseRepository() {
        assertInstanceOf(XhbCaseRepository.class, classUnderTest.getXhbCaseRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbCourtSiteRepository() {
        assertInstanceOf(XhbCourtSiteRepository.class, classUnderTest.getXhbCourtSiteRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbCourtRoomRepository() {
        assertInstanceOf(XhbCourtRoomRepository.class, classUnderTest.getXhbCourtRoomRepository(),
            NOT_INSTANCE);
    }

    @SuppressWarnings({"PMD.UseExplicitTypes", "PMD.AvoidAccessibilityAlteration"})
    @Test
    void testClearRepositoriesSetsRepositoryToNull() throws Exception {
        // Given
        classUnderTest.clearRepositories();

        // Use reflection to check the private field
        var field = PublicDisplayQueryLogEntry.class.getDeclaredField("xhbCourtLogEntryRepository");
        field.setAccessible(true);
        Object repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);

        // Use reflection to check the private field
        field = PublicDisplayQueryLogEntry.class.getDeclaredField("xhbCourtLogEventDescRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);

        // Use reflection to check the private field
        field = PublicDisplayQueryLogEntry.class.getDeclaredField("xhbCaseRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);

        // Use reflection to check the private field
        field = PublicDisplayQueryLogEntry.class.getDeclaredField("xhbCourtSiteRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);


        // Use reflection to check the private field
        field = PublicDisplayQueryLogEntry.class.getDeclaredField("xhbCourtRoomRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest);

        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
    }

}
