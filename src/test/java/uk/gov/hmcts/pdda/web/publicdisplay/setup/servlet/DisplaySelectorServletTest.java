package uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.DummyCourtUtil;
import uk.gov.hmcts.DummyPublicDisplayUtil;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.entities.xhbcourtsite.XhbCourtSiteDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplay.XhbDisplayDao;
import uk.gov.hmcts.pdda.business.entities.xhbdisplaylocation.XhbDisplayLocationDao;
import uk.gov.hmcts.pdda.business.services.publicdisplay.setup.ejb.PdSetupControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown.CourtDrillDown;
import uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown.CourtSiteDrillDown;
import uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown.DisplayLocationDrillDown;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.DisplayUri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>
 * Title: DisplaySelectorServlet Test.
 * </p>
 * <p>
 * Description: Tests for the DisplaySelectorServlet class
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
class DisplaySelectorServletTest {

    @Mock
    private static PdSetupControllerBean mockPDSetupControllerBean;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private static ServletConfig config;

    @TestSubject
    private static DisplaySelectorServlet classUnderTest = new DisplaySelectorServlet(mockPDSetupControllerBean);

    @BeforeAll
    public static void setUp() { 
        try {
            config = EasyMock.createMock(ServletConfig.class);
            classUnderTest.init(config);
        } catch (ServletException ex) {
            fail(ex.getMessage());
        }
    }

    @AfterAll
    public static void tearDown() {
        // Do nothing
    }

    @Test
    void testServiceDoGet() {
        Integer courtId = 94;
        XhbCourtDao courtDao = DummyCourtUtil.getXhbCourtDao(courtId, "Test Court " + courtId);
        XhbCourtSiteDao courtSiteDao = DummyCourtUtil.getXhbCourtSiteDao();
        CourtDrillDown drilldown = getDummyCourtDrillDown(courtDao, courtSiteDao);

        EasyMock.expect(request.getMethod()).andReturn("GET");
        EasyMock.expect(request.getParameter("courtId")).andReturn(courtId.toString());
        EasyMock.expect(mockPDSetupControllerBean.getDrillDownForCourt(EasyMock.isA(Integer.class)))
            .andReturn(drilldown);
        request.setAttribute("drillDown", drilldown);
        EasyMock.expect(request.getRequestDispatcher("/setup/display_selector.jsp"))
            .andReturn(EasyMock.createMock(RequestDispatcher.class));

        EasyMock.replay(request);
        EasyMock.replay(mockPDSetupControllerBean);

        try {
            classUnderTest.service(request, response);
        } catch (ServletException | IOException e) {
            fail(e.getMessage());
        }

        EasyMock.verify(request);
        EasyMock.verify(mockPDSetupControllerBean);
    }

    @Test
    void testServiceDoPost() {
        Integer courtId = 94;
        XhbCourtDao courtDao = DummyCourtUtil.getXhbCourtDao(courtId, "Test Court " + courtId);
        XhbCourtDao[] dummyCourtArray = {courtDao, DummyCourtUtil.getXhbCourtDao(3, "Test Court 3"),
            DummyCourtUtil.getXhbCourtDao(5, "Test Court 5")};

        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.expect(request.getParameter("courtId")).andReturn(null);
        EasyMock.expect(mockPDSetupControllerBean.getAllCourts()).andReturn(dummyCourtArray);
        request.setAttribute("courts", dummyCourtArray);
        EasyMock.expect(request.getRequestDispatcher("/setup/court_selector.jsp"))
            .andReturn(EasyMock.createMock(RequestDispatcher.class));

        EasyMock.replay(request);
        EasyMock.replay(mockPDSetupControllerBean);

        try {
            classUnderTest.service(request, response);
        } catch (ServletException | IOException e) {
            fail(e.getMessage());
        }

        EasyMock.verify(request);
        EasyMock.verify(mockPDSetupControllerBean);
    }

    private CourtDrillDown getDummyCourtDrillDown(XhbCourtDao courtDao, XhbCourtSiteDao courtSiteDao) {
        final CourtDrillDown courtDrillDown = new CourtDrillDown(courtDao.getDisplayName());
        List<XhbDisplayLocationDao> xhbDisplayLocations = new ArrayList<>();
        xhbDisplayLocations.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());
        xhbDisplayLocations.add(DummyPublicDisplayUtil.getXhbDisplayLocationDao());

        CourtSiteDrillDown courtSiteDrillDown = getCourtSiteDrillDown(courtSiteDao.getDisplayName());
        for (XhbDisplayLocationDao xhbDisplayLocation : xhbDisplayLocations) {

            List<XhbDisplayDao> xhbDisplays = (ArrayList<XhbDisplayDao>) xhbDisplayLocation.getXhbDisplays();

            DisplayLocationDrillDown displayLocationDrillDown =
                getDisplayLocationDrillDownByDescriptionCode(xhbDisplayLocation);

            assertNotNull(displayLocationDrillDown.getName(), "Result is Null");

            for (XhbDisplayDao xhbDisplay : xhbDisplays) {

                DisplayUri displayUri = getDisplayUri(courtDao.getShortName().toLowerCase(Locale.getDefault()),
                    courtSiteDao.getCourtSiteCode().toLowerCase(Locale.getDefault()),
                    xhbDisplayLocation.getDescriptionCode().toLowerCase(Locale.getDefault()),
                    xhbDisplay.getDescriptionCode().toLowerCase(Locale.getDefault()));

                displayLocationDrillDown.addDisplay(displayUri);
            }
            courtSiteDrillDown.addCourtRoom(displayLocationDrillDown);
        }
        courtDrillDown.addCourtSite(courtSiteDrillDown);
        return courtDrillDown;
    }

    private DisplayLocationDrillDown getDisplayLocationDrillDownByDescriptionCode(
        XhbDisplayLocationDao xhbDisplayLocation) {
        return new DisplayLocationDrillDown(xhbDisplayLocation.getDescriptionCode());
    }

    private CourtSiteDrillDown getCourtSiteDrillDown(String courtSiteName) {
        return new CourtSiteDrillDown(courtSiteName);
    }

    private DisplayUri getDisplayUri(final String courthouseName, final String courtsiteCode, final String location,
        final String display) {
        return new DisplayUri(courthouseName, courtsiteCode, location, display);
    }
}
