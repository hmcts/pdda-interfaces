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

/**
 * <p>
 * Title: PublicDisplayQueryRepo Test.
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
 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class PublicDisplayQueryRepoTest {

    private static final String NOT_INSTANCE = "Result is Not An Instance of";

    @TestSubject
    private final AllCaseStatusQuery classUnderTest =
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
}
