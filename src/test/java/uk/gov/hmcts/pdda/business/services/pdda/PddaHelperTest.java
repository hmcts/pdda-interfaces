package uk.gov.hmcts.pdda.business.services.pdda;

import com.jcraft.jsch.Session;
import jakarta.persistence.EntityManager;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.env.Environment;
import uk.gov.hmcts.DummyPdNotifierUtil;
import uk.gov.hmcts.DummyServicesUtil;
import uk.gov.hmcts.pdda.business.entities.xhbclob.XhbClobRepository;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcppstaginginbound.XhbCppStagingInboundDao;
import uk.gov.hmcts.pdda.business.entities.xhbpddamessage.XhbPddaMessageDao;
import uk.gov.hmcts.pdda.business.services.cppstaginginboundejb3.CppStagingInboundHelper;
import uk.gov.hmcts.pdda.common.publicdisplay.jms.PublicDisplayNotifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.GodClass", "PMD.CouplingBetweenObjects"})
class PddaHelperTest {

    private static final String NOT_NULL = "Result is Null";
    private static final String NOT_TRUE = "Result is not True";
    private static final String TESTUSER = "TestUser";


    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private PddaMessageHelper mockPddaMessageHelper;

    @Mock
    private CppStagingInboundHelper mockCppStagingInboundHelper;

    @Mock
    private PddaSftpHelper mockPddaSftpHelper;

    @Mock
    private XhbConfigPropRepository mockXhbConfigPropRepository;

    @Mock
    private XhbCourtRepository mockXhbCourtRepository;

    @Mock
    private XhbClobRepository mockXhbClobRepository;

    @Mock
    private PublicDisplayNotifier mockPublicDisplayNotifier;

    @Mock
    private Session mockSession;

    @Mock
    private Environment mockEnvironment;

    @Mock
    private final PddaHelper mockPddaHelper = new PddaHelper(mockEntityManager);

    @TestSubject
    private final PddaHelper classUnderTest = new PddaHelper(mockEntityManager);

    @Test
    void testDefaultConstructor() {
        boolean result = true;
        new PddaHelper(mockEntityManager, mockXhbConfigPropRepository, mockEnvironment,
            mockPddaSftpHelper, mockPddaMessageHelper, mockXhbClobRepository,
            mockXhbCourtRepository);
        assertTrue(result, NOT_TRUE);
    }

    private static final class Config {

        // Database values
        public static final String DB_SFTP_HOST = "PDDA_BAIS_SFTP_HOSTNAME";
        public static final String DB_SFTP_PASSWORD = "PDDA_BAIS_SFTP_PASSWORD";
        public static final String DB_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_SFTP_UPLOAD_LOCATION";
        public static final String DB_SFTP_USERNAME = "PDDA_BAIS_SFTP_USERNAME";
        public static final String DB_CP_SFTP_USERNAME = "PDDA_BAIS_CP_SFTP_USERNAME";
        public static final String DB_CP_SFTP_PASSWORD = "PDDA_BAIS_CP_SFTP_PASSWORD";
        public static final String DB_CP_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION";

        // Key vault values
        public static final String KV_SFTP_HOST = "pdda.bais_sftp_hostname";
        public static final String KV_SFTP_PASSWORD = "pdda.bais_sftp_password";
        public static final String KV_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_SFTP_UPLOAD_LOCATION";
        public static final String KV_SFTP_USERNAME = "pdda.bais_sftp_username";
        public static final String KV_CP_SFTP_USERNAME = "pdda.bais_cp_sftp_username";
        public static final String KV_CP_SFTP_PASSWORD = "pdda.bais_cp_sftp_password";
        public static final String KV_CP_SFTP_UPLOAD_LOCATION = "PDDA_BAIS_CP_SFTP_UPLOAD_LOCATION";
    }

    @Test
    void testCheckForCpMessages() throws IOException {
        // Setup
        testGetBaisConfigs(null);

        List<XhbPddaMessageDao> xhbPddaMessageDaoList = new ArrayList<>();
        xhbPddaMessageDaoList.add(DummyPdNotifierUtil.getXhbPddaMessageDao());
        List<XhbCppStagingInboundDao> xhbCppStagingInboundDaoList = new ArrayList<>();
        xhbCppStagingInboundDaoList.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());

        EasyMock.expect(mockPddaMessageHelper.findUnrespondedCpMessages())
            .andReturn(xhbPddaMessageDaoList);
        EasyMock.expect(mockCppStagingInboundHelper.findUnrespondedCppMessages())
            .andReturn(xhbCppStagingInboundDaoList);

        Map<String, InputStream> filesMap = new ConcurrentHashMap<>();

        if (!xhbPddaMessageDaoList.isEmpty()) {
            EasyMock.expect(mockPddaHelper.respondToPddaMessage(xhbPddaMessageDaoList))
                .andReturn(filesMap);
        }

        if (!xhbCppStagingInboundDaoList.isEmpty()) {
            EasyMock.expect(mockPddaHelper.respondToCppStagingInbound(xhbCppStagingInboundDaoList))
                .andReturn(filesMap);
        }

        InputStream msgContents = new ByteArrayInputStream("Test Message".getBytes());
        filesMap.put("TestEntry", msgContents);

        String userDisplayName = TESTUSER;
        XhbPddaMessageDao xhbPddaMessageDao = DummyPdNotifierUtil.getXhbPddaMessageDao();
        for (XhbPddaMessageDao dao : xhbPddaMessageDaoList) {
            EasyMock.expect(mockPddaMessageHelper.updatePddaMessage(dao, userDisplayName))
                .andReturn(Optional.of(xhbPddaMessageDao));
        }

        for (XhbCppStagingInboundDao xhbCppStagingInboundDao : xhbCppStagingInboundDaoList) {
            EasyMock.expect(mockCppStagingInboundHelper
                .updateCppStagingInbound(xhbCppStagingInboundDao, userDisplayName))
                .andReturn(Optional.of(xhbCppStagingInboundDao));
        }

        EasyMock.replay(mockCppStagingInboundHelper);
        EasyMock.replay(mockPddaMessageHelper);
        EasyMock.replay(mockPddaHelper);
        EasyMock.replay(mockEnvironment);
        EasyMock.replay(mockXhbConfigPropRepository);

        // Run
        boolean result = false;
        try {
            classUnderTest.checkForCpMessages(TESTUSER);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        EasyMock.verify(mockPddaMessageHelper);
        assertTrue(result, NOT_TRUE);
    }

    @Test
    void testRespondToCppStagingInbound() throws IOException {
        // Setup
        List<XhbCppStagingInboundDao> cppStagingInboundDaoList = new ArrayList<>();
        cppStagingInboundDaoList.add(DummyPdNotifierUtil.getXhbCppStagingInboundDao());
        Map<String, InputStream> filesMap = new ConcurrentHashMap<>();
        EasyMock.expect(mockPddaHelper.respondToCppStagingInbound(cppStagingInboundDaoList))
            .andReturn(filesMap);
        EasyMock.replay(mockPddaHelper);
        // Run
        Map<String, InputStream> actualResult =
            classUnderTest.respondToCppStagingInbound(cppStagingInboundDaoList);
        // Checks
        assertNotNull(actualResult, NOT_NULL);
    }


    @Test
    void testUpdateCppStagingInboundRecords() {
        // Setup
        XhbCppStagingInboundDao xhbCppStagingInboundDao =
            DummyPdNotifierUtil.getXhbCppStagingInboundDao();
        List<XhbCppStagingInboundDao> cppStagingInboundList = new ArrayList<>();
        cppStagingInboundList.add(xhbCppStagingInboundDao);
        String userDisplayName = TESTUSER;
        for (XhbCppStagingInboundDao dao : cppStagingInboundList) {
            EasyMock
                .expect(mockCppStagingInboundHelper.updateCppStagingInbound(dao, userDisplayName))
                .andReturn(Optional.of(xhbCppStagingInboundDao));
        }
        EasyMock.replay(mockCppStagingInboundHelper);
        // Run
        boolean result = false;
        try {
            PddaMessageUtil.updateCppStagingInboundRecords(mockCppStagingInboundHelper,
                cppStagingInboundList, userDisplayName);
            result = true;
        } catch (Exception exception) {
            fail(exception);
        }
        // Checks
        EasyMock.verify(mockCppStagingInboundHelper);
        assertTrue(result, NOT_TRUE);
    }



    private void testGetBaisConfigs(String failOn) {
        // DB first then KV

        // DB values need to be set in Config Prop Repository
        // Username
        String propertyName = Config.DB_CP_SFTP_USERNAME;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        List<XhbConfigPropDao> usernameList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(usernameList).anyTimes();
        // Password
        propertyName = Config.DB_CP_SFTP_PASSWORD;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        List<XhbConfigPropDao> passwordList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(passwordList).anyTimes();
        // Location
        propertyName = Config.DB_CP_SFTP_UPLOAD_LOCATION;
        List<XhbConfigPropDao> locationList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(locationList).anyTimes();

        propertyName = Config.DB_SFTP_USERNAME;
        usernameList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(usernameList).anyTimes();
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();

        // Password
        propertyName = Config.DB_SFTP_PASSWORD;
        passwordList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(passwordList).anyTimes();
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();

        // Location
        propertyName = Config.DB_SFTP_UPLOAD_LOCATION;
        locationList = getXhbConfigPropDaoList(propertyName);
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(locationList).anyTimes();
        // Host
        propertyName = Config.DB_SFTP_HOST;
        String hostAndPort = propertyName.toLowerCase(Locale.getDefault());
        List<XhbConfigPropDao> hostList = getXhbConfigPropDaoList(propertyName);
        if (failOn == null || !propertyName.equals(failOn)) {
            hostAndPort = hostList.get(0).getPropertyValue() + ":22";
            hostList = getXhbConfigPropDaoList(hostAndPort);
        }
        EasyMock.expect(mockXhbConfigPropRepository.findByPropertyName(propertyName))
            .andReturn(hostList).anyTimes();
        EasyMock.expect(mockEnvironment.getProperty(propertyName)).andReturn(hostAndPort)
            .anyTimes();


        // KV values need to be set in environment
        // Username
        propertyName = Config.KV_CP_SFTP_USERNAME;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Password
        propertyName = Config.KV_CP_SFTP_PASSWORD;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Username
        propertyName = Config.KV_SFTP_USERNAME;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Password
        propertyName = Config.KV_SFTP_PASSWORD;
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Location
        propertyName = Config.KV_SFTP_UPLOAD_LOCATION; // Already done above
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        propertyName = Config.KV_CP_SFTP_UPLOAD_LOCATION; // Already done above
        EasyMock.expect(mockEnvironment.getProperty(propertyName))
            .andReturn(propertyName.toLowerCase(Locale.getDefault())).anyTimes();
        // Host
        propertyName = Config.KV_SFTP_HOST;
        hostAndPort = propertyName.toLowerCase(Locale.getDefault());
        if (failOn == null || !propertyName.equals(failOn)) {
            hostAndPort = hostAndPort + ":22";
        }
        EasyMock.expect(mockEnvironment.getProperty(propertyName)).andReturn(hostAndPort)
            .anyTimes();
    }

    private List<XhbConfigPropDao> getXhbConfigPropDaoList(String propertyName) {
        List<XhbConfigPropDao> result = new ArrayList<>();
        result.add(DummyServicesUtil.getXhbConfigPropDao(propertyName,
            propertyName.toLowerCase(Locale.getDefault())));
        return result;
    }
}
