package uk.gov.hmcts.pdda.business.services.pdda;

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
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * Title: PDDA Helper Test.
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
class PddaHelperConfigTest {
    private static final String EQUALS = "Results are not Equal";
    private static final String NOTNULL = "Result is Null";
    private static final String PDDA_BAIS_SFTP_USERNAME = "PDDA_BAIS_SFTP_USERNAME";


    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @TestSubject
    private final PddaHelper classUnderTest =
        new PddaHelper(EasyMock.createMock(EntityManager.class));

    @BeforeAll
    public static void setUp() {
        // Do nothing
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testGetMandatoryConfigValueSuccess() {
        // Setup
        String propertyName = PDDA_BAIS_SFTP_USERNAME;
        XhbConfigPropDao dummyXhbConfigPropDao = DummyServicesUtil.getXhbConfigPropDao(propertyName,
            propertyName.toLowerCase(Locale.getDefault()));
        String expectedResult = dummyXhbConfigPropDao.getPropertyValue();
        // Run
        String actualResult = testGetMandatoryConfigValue(dummyXhbConfigPropDao, expectedResult);
        // Checks
        assertNotNull(actualResult, NOTNULL);
        assertEquals(expectedResult, actualResult, EQUALS);
    }

    @Test
    void testGetMandatoryConfigValueFailure() {
        Assertions.assertThrows(PddaHelper.InvalidConfigException.class, () -> {
            // Setup
            String propertyName = PDDA_BAIS_SFTP_USERNAME;
            XhbConfigPropDao dummyXhbConfigPropDao = DummyServicesUtil
                .getXhbConfigPropDao(propertyName, propertyName.toLowerCase(Locale.getDefault()));
            String expectedResult = null;
            // Run
            testGetMandatoryConfigValue(dummyXhbConfigPropDao, expectedResult);
        });
    }

    private String testGetMandatoryConfigValue(XhbConfigPropDao dummyXhbConfigPropDao,
        String expectedResult) {
        // Setup
        List<XhbConfigPropDao> dummyXhbConfigPropDaoList = new ArrayList<>();
        if (expectedResult != null) {
            dummyXhbConfigPropDaoList.add(dummyXhbConfigPropDao);
        }
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(EasyMock.isA(String.class)))
            .andReturn(dummyXhbConfigPropDaoList);
        EasyMock.replay(mockXhbConfigPropRepository);
        // Run
        String actualResult =
            classUnderTest.getMandatoryConfigValue(dummyXhbConfigPropDao.getPropertyName());
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        return actualResult;
    }

    @Test
    void testGetMandatoryConfigValue() {
        // Setup
        String propertyName = PDDA_BAIS_SFTP_USERNAME;
        List<XhbConfigPropDao> dummyXhbConfigPropDaoList = getXhbConfigPropDaoList(propertyName);
        final String expectedResult = dummyXhbConfigPropDaoList.get(0).getPropertyValue();
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(dummyXhbConfigPropDaoList);
        EasyMock.replay(mockXhbConfigPropRepository);
        // Run
        String actualResult = classUnderTest.getMandatoryConfigValue(propertyName);
        // Checks
        EasyMock.verify(mockXhbConfigPropRepository);
        assertNotNull(actualResult, NOTNULL);
        assertEquals(expectedResult, actualResult, EQUALS);
    }

    private List<XhbConfigPropDao> getXhbConfigPropDaoList(String propertyName) {
        List<XhbConfigPropDao> result = new ArrayList<>();
        result.add(DummyServicesUtil.getXhbConfigPropDao(propertyName,
            propertyName.toLowerCase(Locale.getDefault())));
        return result;
    }

}
