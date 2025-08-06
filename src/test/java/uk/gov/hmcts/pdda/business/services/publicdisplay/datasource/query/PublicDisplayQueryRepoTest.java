package uk.gov.hmcts.pdda.business.services.publicdisplay.datasource.query;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.pdda.business.entities.xhbcasereference.XhbCaseReferenceRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendant.XhbDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbdefendantoncase.XhbDefendantOnCaseRepository;
import uk.gov.hmcts.pdda.business.entities.xhbhearing.XhbHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefhearingtype.XhbRefHearingTypeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbrefjudge.XhbRefJudgeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbschedhearingdefendant.XhbSchedHearingDefendantRepository;
import uk.gov.hmcts.pdda.business.entities.xhbscheduledhearing.XhbScheduledHearingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbsitting.XhbSittingRepository;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**

 * Title: PublicDisplayQueryRepo Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@SuppressWarnings("PMD")
@ExtendWith(EasyMockExtension.class)
class PublicDisplayQueryRepoTest {

    private static final String NOT_INSTANCE = "Result is Not An Instance of";
    private static final String CLEAR_REPOSITORIES_MESSAGE =
        "Repository should be null after clearRepositories()";

    @TestSubject
    private final AllCaseStatusQuery classUnderTest =
        new AllCaseStatusQuery(EasyMock.createMock(EntityManager.class));

    @TestSubject
    private final PublicDisplayQueryRepo classUnderTest2 =
        new AllCaseStatusQuery(EasyMock.createMock(EntityManager.class));

    @Test
    void testGetXhbDefendantRepository() {
        assertInstanceOf(XhbDefendantRepository.class, classUnderTest.getXhbDefendantRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbRefHearingTypeRepository() {
        assertInstanceOf(XhbRefHearingTypeRepository.class,
            classUnderTest.getXhbRefHearingTypeRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetXhbHearingRepository() {
        assertInstanceOf(XhbHearingRepository.class, classUnderTest.getXhbHearingRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbRefJudgeRepository() {
        assertInstanceOf(XhbRefJudgeRepository.class, classUnderTest.getXhbRefJudgeRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbSittingRepository() {
        assertInstanceOf(XhbSittingRepository.class, classUnderTest.getXhbSittingRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbScheduledHearingRepository() {
        assertInstanceOf(XhbScheduledHearingRepository.class,
            classUnderTest.getXhbScheduledHearingRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetXhbSchedHearingDefendantRepository() {
        assertInstanceOf(XhbSchedHearingDefendantRepository.class,
            classUnderTest.getXhbSchedHearingDefendantRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetXhbCaseReferenceRepository() {
        assertInstanceOf(XhbCaseReferenceRepository.class,
            classUnderTest.getXhbCaseReferenceRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetXhbDefendantOnCaseRepository() {
        assertInstanceOf(XhbDefendantOnCaseRepository.class,
            classUnderTest.getXhbDefendantOnCaseRepository(), NOT_INSTANCE);
    }
    
    @SuppressWarnings({"PMD.UseExplicitTypes", "PMD.AvoidAccessibilityAlteration"})
    @Test
    void testClearRepositoriesSetsRepositoryToNull() throws Exception {
        // Given
        classUnderTest2.clearRepositories();

        // Use reflection to check the private field
        var field = PublicDisplayQueryRepo.class.getDeclaredField("xhbCaseReferenceRepository");
        field.setAccessible(true);
        Object repository = field.get(classUnderTest);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbDefendantRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbDefendantOnCaseRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbHearingRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbHearingListRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbRefJudgeRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbRefHearingTypeRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbSittingRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbScheduledHearingRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);
        
        // Use reflection to check the private field
        field = PublicDisplayQueryRepo.class.getDeclaredField("xhbSchedHearingDefendantRepository");
        field.setAccessible(true);
        repository = field.get(classUnderTest2);
        
        // Then
        assertTrue(repository == null, CLEAR_REPOSITORIES_MESSAGE);

    }
}
