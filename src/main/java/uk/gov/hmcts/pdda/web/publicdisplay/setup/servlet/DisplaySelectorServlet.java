package uk.gov.hmcts.pdda.web.publicdisplay.setup.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.framework.business.exceptions.CourtNotFoundException;
import uk.gov.hmcts.pdda.business.entities.xhbcourt.XhbCourtDao;
import uk.gov.hmcts.pdda.business.services.publicdisplay.setup.ejb.PdSetupControllerBean;
import uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown.CourtDrillDown;

import java.io.IOException;

/**
 * <p/>
 * Title: DisplaySelectorServlet.
 * </p>
 * <p/>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p/>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.5 $
 */
@WebServlet("/DisplaySelectorServlet")
public class DisplaySelectorServlet extends HttpServlet {

    private static final long serialVersionUID = -4477899905926363637L;
    private static Logger log = LoggerFactory.getLogger(DisplaySelectorServlet.class);

    @jakarta.ejb.EJB
    @Inject
    private PdSetupControllerBean pdSetupControllerBean;

    protected DisplaySelectorServlet() {
        super();
    }

    protected DisplaySelectorServlet(PdSetupControllerBean pdSetupControllerBean) {
        this();
        this.pdSetupControllerBean = pdSetupControllerBean;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doRequest(request, response);
    }

    private void doRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.debug("doRequest({},{})", request, response);
            String courtIdStr = request.getParameter("courtId");
            if (courtIdStr == null) {
                XhbCourtDao[] allCourts = getPdSetupControllerBean().getAllCourts();
                request.setAttribute("courts", allCourts);
                RequestDispatcher requestDispatcher =
                    request.getRequestDispatcher("/setup/court_selector.jsp");
                requestDispatcher.include(request, response);
                return;
            }

            Integer courtId = getCourtId(courtIdStr);
            CourtDrillDown drillDownForCourt = getCourtDrillDown(courtId);
            request.setAttribute("drillDown", drillDownForCourt);
            RequestDispatcher requestDispatcher =
                request.getRequestDispatcher("/setup/display_selector.jsp");
            requestDispatcher.include(request, response);
        } catch (ServletException | IOException e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private Integer getCourtId(String courtIdStr) {
        try {
            return Integer.valueOf(courtIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private CourtDrillDown getCourtDrillDown(Integer courtId) {
        try {
            return getPdSetupControllerBean().getDrillDownForCourt(courtId);
        } catch (CourtNotFoundException e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doRequest(request, response);
    }

    private PdSetupControllerBean getPdSetupControllerBean() {
        if (pdSetupControllerBean == null) {
            pdSetupControllerBean = new PdSetupControllerBean();
        }
        return pdSetupControllerBean;
    }

}
