package uk.gov.hmcts.pdda.business.services.pdda.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtroom.XhbCourtRoomRepository;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.TooManyMethods")
class CreationHelperTest {

    private static final String NOTNULL = "Result is Null";

    @Mock
    private RepositoryHelper mockRepositoryHelper;

    @Mock
    private XhbCourtSiteRepository mockXhbCourtSiteRepository;
    
    @Mock
    private XhbCourtRoomRepository mockXhbCourtRoomRepository;

    @InjectMocks
    private final CreationHelper classUnderTest = new CreationHelper(mockRepositoryHelper);

    @Test
    void testDefaultConstructor() {
        CreationHelper localClassUnderTest = new CreationHelper() {
            @Override
            public RepositoryHelper getRepositoryHelper() {
                return super.getRepositoryHelper();
            }
        };
        RepositoryHelper result = localClassUnderTest.getRepositoryHelper();
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateCourtSite() {
        Mockito.when(mockRepositoryHelper.getXhbCourtSiteRepository())
            .thenReturn(mockXhbCourtSiteRepository);

        XhbCourtSiteDao dao = DummyCourtUtil.getXhbCourtSiteDao();
        Optional<XhbCourtSiteDao> result = classUnderTest.createCourtSite(dao.getCourtId(),
            dao.getCourtSiteName(), dao.getCourtSiteCode());
        assertNotNull(result, NOTNULL);
    }

    @Test
    void testCreateCourtRoom() {
        Mockito.when(mockRepositoryHelper.getXhbCourtRoomRepository())
            .thenReturn(mockXhbCourtRoomRepository);

        XhbCourtRoomDao dao = DummyCourtUtil.getXhbCourtRoomDao();
        Optional<XhbCourtRoomDao> result = classUnderTest.createCourtRoom(dao.getCourtSiteId(),
            dao.getCourtRoomName(), dao.getDescription(), dao.getCrestCourtRoomNo());
        assertNotNull(result, NOTNULL);
    }

}
