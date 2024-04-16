package uk.gov.hmcts.pdda.business.services.pdda.lighthouse;

import jakarta.persistence.EntityManager;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundRepository;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: Lighthouse PDDA Test.
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
 * @author Mark Harris
 */
@ExtendWith(EasyMockExtension.class)
class LighthousePddaTest {

    private static final String NOTNULL = "Result is Null";
    private static final String TRUE = "Result is not True";
    private static final String SAME = "Result is not Same";
    private static final String MESSAGE_STATUS_PROCESSED = "VP";
    private static final String MESSAGE_STATUS_INVALID = "INV";
    private static final String UNDERSCORE = "_";
    private static final Integer PART_NO = 3;

    @Mock
    private FileBasedConfigurationBuilder<PropertiesConfiguration> mockFileBasedConfigurationBuilder;

    @Mock
    private PropertiesConfiguration mockPropertiesConfiguration;

    @Mock
    private XhbPddaMessageRepository mockXhbPddaMessageRepository;

    @Mock
    private XhbCppStagingInboundRepository mockXhbCppStagingInboundRepository;

    @Mock
    private EntityManager mockEntityManager;

    @TestSubject
    private final LighthousePdda classUnderTest = new LighthousePdda(mockEntityManager);

    @Test
    void testGetConfiguration() {
        LighthousePdda localClassUnderTest = new LighthousePdda(mockEntityManager) {
            @Override
            protected FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigBuilder() {
                FileBasedConfigurationBuilder<PropertiesConfiguration> builder = getBuilder();
                assertNotNull(builder, NOTNULL);
                return mockFileBasedConfigurationBuilder;
            }
        };
        boolean result = false;
        try {
            localClassUnderTest.getConfiguration();
            result = true;
        } catch (ConfigurationException exception) {
            fail(exception.getMessage());
        }
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessFilesSuccess() {
        boolean result = testProcessFiles(MESSAGE_STATUS_PROCESSED);
        assertTrue(result, TRUE);
    }

    @Test
    void testProcessFilesFailure() {
        boolean result = testProcessFiles(MESSAGE_STATUS_INVALID);
        assertTrue(result, TRUE);
    }

    private boolean testProcessFiles(String expectedSavedStatus) {
        // new Parameters().properties().setFileName("");
        // Add Captured Values
        Capture<XhbPddaMessageDao> savedValue = EasyMock.newCapture();

        // Setup
        List<XhbPddaMessageDao> xhbPddaMessageDaoList = getDummyXhbPddaMessageDaoList();
        EasyMock.expect(mockPropertiesConfiguration.getInt(EasyMock.isA(String.class))).andReturn(1);
        EasyMock.expect(mockXhbPddaMessageRepository.findByLighthouse()).andReturn(xhbPddaMessageDaoList);
        for (XhbPddaMessageDao xhbPddaMessageDao : xhbPddaMessageDaoList) {
            String[] fileParts = xhbPddaMessageDao.getCpDocumentName().split(UNDERSCORE);
            if (fileParts.length != PART_NO) {
                continue;
            }
            EasyMock.expect(mockXhbPddaMessageRepository.findById(xhbPddaMessageDao.getPrimaryKey()))
                .andReturn(Optional.of(xhbPddaMessageDao));
            EasyMock.expectLastCall().times(2);
            mockXhbPddaMessageRepository
                .save(EasyMock.and(EasyMock.capture(savedValue), EasyMock.isA(XhbPddaMessageDao.class)));
            EasyMock.expectLastCall().times(2);
            if (MESSAGE_STATUS_INVALID.equals(expectedSavedStatus)) {
                // Failure
                EasyMock.expect(mockXhbCppStagingInboundRepository.update(EasyMock.isA(XhbCppStagingInboundDao.class)))
                    .andThrow(getRuntimeException());
            } else {
                // Success
                XhbCppStagingInboundDao stagingInboundDao = DummyPdNotifierUtil.getXhbCppStagingInboundDao();
                stagingInboundDao.setDocumentName(xhbPddaMessageDao.getCpDocumentName());
                EasyMock.expect(mockXhbCppStagingInboundRepository.update(EasyMock.isA(XhbCppStagingInboundDao.class)))
                    .andReturn(Optional.of(stagingInboundDao));
            }
        }
        EasyMock.replay(mockPropertiesConfiguration);
        EasyMock.replay(mockEntityManager);
        EasyMock.replay(mockXhbPddaMessageRepository);
        EasyMock.replay(mockXhbCppStagingInboundRepository);
        // Run
        classUnderTest.processFiles();
        // Checks
        EasyMock.verify(mockPropertiesConfiguration);
        EasyMock.verify(mockEntityManager);
        EasyMock.verify(mockXhbPddaMessageRepository);
        EasyMock.verify(mockXhbCppStagingInboundRepository);
        assertNotNull(savedValue, NOTNULL);
        assertSame(expectedSavedStatus, savedValue.getValue().getCpDocumentStatus(), SAME);
        return true;
    }

    private List<XhbPddaMessageDao> getDummyXhbPddaMessageDaoList() {
        List<XhbPddaMessageDao> result = new ArrayList<>();
        String[] cpDocumentNames =
            {"DailyList_453_20220811235559.xml", "WarnedList_111_20220810010433.xml", "FirmList_101_20220807010423.xml",
                "PublicDisplay_100_20220802030423.xml", "WebPage_122_20220804031423.xml", "Invalid_File.csv"};
        for (String cpDocumentName : cpDocumentNames) {
            XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
            xhbPddaMessageDao.setCpDocumentName(cpDocumentName);
            result.add(xhbPddaMessageDao);
        }
        return result;
    }

    private RuntimeException getRuntimeException() {
        return new RuntimeException();
    }
}
