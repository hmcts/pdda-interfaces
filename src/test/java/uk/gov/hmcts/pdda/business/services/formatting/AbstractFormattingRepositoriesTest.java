package uk.gov.hmcts.pdda.business.services.formatting;

import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.framework.services.XslServices;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformatting.XhbCppFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppformattingmerge.XhbCppFormattingMergeRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcpplist.XhbCppListRepository;
import uk.gov.hmcts.pdda.business.entities.xhbformatting.XhbFormattingRepository;
import uk.gov.hmcts.pdda.business.entities.xhbxmldocument.XhbXmlDocumentRepository;
import uk.gov.hmcts.pdda.business.xmlbinding.formatting.FormattingConfig;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**

 * Title: AbstractFormattingRepositories Test.


 * Description:


 * Copyright: Copyright (c) 2024


 * Company: CGI

 * @author Luke Gittins
 */
@ExtendWith(EasyMockExtension.class)
class AbstractFormattingRepositoriesTest {

    private static final String NOT_INSTANCE = "Result is Not An Instance of";

    @TestSubject
    private final AbstractFormattingRepositories classUnderTest =
        new AbstractFormattingRepositories(EasyMock.createMock(EntityManager.class));

    @Test
    void testGetXhbClobRepository() {
        assertInstanceOf(XhbClobRepository.class, classUnderTest.getXhbClobRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbCppListRepository() {
        assertInstanceOf(XhbCppListRepository.class, classUnderTest.getXhbCppListRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testgetXhbCppFormattingRepository() {
        assertInstanceOf(XhbCppFormattingRepository.class,
            classUnderTest.getXhbCppFormattingRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetXhbFormattingRepository() {
        assertInstanceOf(XhbFormattingRepository.class, classUnderTest.getXhbFormattingRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbXmlDocumentRepository() {
        assertInstanceOf(XhbXmlDocumentRepository.class,
            classUnderTest.getXhbXmlDocumentRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetXhbConfigPropRepository() {
        assertInstanceOf(XhbConfigPropRepository.class, classUnderTest.getXhbConfigPropRepository(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXhbCppFormattingMergeRepository() {
        assertInstanceOf(XhbCppFormattingMergeRepository.class,
            classUnderTest.getXhbCppFormattingMergeRepository(), NOT_INSTANCE);
    }

    @Test
    void testGetFormattingConfig() {
        assertInstanceOf(FormattingConfig.class, classUnderTest.getFormattingConfig(),
            NOT_INSTANCE);
    }

    @Test
    void testGetXslServices() {
        assertInstanceOf(XslServices.class, classUnderTest.getXslServices(), NOT_INSTANCE);
    }
}
