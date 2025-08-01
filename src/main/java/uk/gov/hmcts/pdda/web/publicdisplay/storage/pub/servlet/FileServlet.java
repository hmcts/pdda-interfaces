package uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.AbstractUri;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.UriFactory;
import uk.gov.hmcts.pdda.common.publicdisplay.types.uri.exceptions.InvalidUriFormatException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.priv.impl.DisplayStoreControllerBean;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StoredObject;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.StorerFactory;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.ObjectDoesNotExistException;
import uk.gov.hmcts.pdda.web.publicdisplay.storage.pub.exceptions.RetrievalException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * <p/>
 * Title: The file servlet is responsible for serving up an object for a URI.

 * <p/>
 * <p/>
 * Description: The FileServlet is used by the thin client application to retrieve both
 * DisplayRotationSets and DisplayDocuments from the store and display them in their appropriate
 * frames.

 * <p/>
 * There are two main methods on the FileServlet, getLas:wq
 * <p/>

 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.11 $
 */
@WebServlet("/FileServlet")
@SuppressWarnings("PMD.LawOfDemeter")
public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(FileServlet.class);

    private final DisplayStoreControllerBean displayStoreControllerBean =
        getDisplayStoreControllerBean();

    /**
     * Initialize the servlet.

     * @throws ServletException (shouldn't be thrown).
     */
    @Override
    public void init() throws ServletException {
        super.init();
        LOG.info("Initialized the file servlet.");
    }

    /**
     * This method calculates the last modified time for If-Modified-Since requests from the
     * browser. The default service method will return a HttpServletResponse.SC_NOT_MODIFIED to the
     * browser if the value return here is less than or equal to the value held by the copy in the
     * browsers cache.
     * <p/>
     * In this case we look for the object that re

     * @param request the servlet request.
     * @return a long representation of the object the uri species modification time.
     */
    @Override
    protected final long getLastModified(HttpServletRequest request) {
        LOG.info("getLastModified()");
        try {
            AbstractUri uri = extractUriFromRequest(request);
            long lastModifiedTimeRoundedDown =
                StorerFactory.getInstance().lastModified(getDisplayStoreControllerBean(), uri);

            if (LOG.isDebugEnabled()) {
                String header = request.getHeader("If-Modified-Since");
                LOG.debug("getLastModified()");
                LOG.debug("last modified={}, if modified since request={} for url {}",
                    new Date(lastModifiedTimeRoundedDown), header, uri);
            }
            return lastModifiedTimeRoundedDown;
        } catch (InvalidUriFormatException e) {
            // This is a hack, it makes certain that the doGet() method gets
            // called so that a proper error can occur.
            LOG.warn(e.getMessage());
            return Long.MAX_VALUE;
        }

    }

    /**
     * The standard request from a browser is the only method currently supported.

     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String header = request.getHeader("If-Modified-Since");
        if (header == null) {
            LOG.warn("Returning uncached page to: {}", request.getRemoteHost());
        }

        AbstractUri uri = extractUriFromRequest(request);
        StoredObject storedObject;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving {}", uri);
        }

        try {
            storedObject =
                StorerFactory.getInstance().retrieve(getDisplayStoreControllerBean(), uri);
        } catch (ObjectDoesNotExistException | RetrievalException e) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "No object could be found for the uri '" + uri + "'.");
            } catch (IOException ioe) {
                LOG.error("Error: {} ", ioe.getMessage());
            }
            LOG.warn("Could not find object specified by the uri '{}'.{}", uri, e.getMessage());
            return;
        }

        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            out.println(storedObject.getText());
        } catch (IOException e) {
            LOG.error("Error: {}", e.getMessage());
        }
    }

    private static AbstractUri extractUriFromRequest(HttpServletRequest request) {
        String uriString = request.getParameter("uri");

        if (uriString == null) {
            throw new InvalidUriFormatException("The URI supplied to the FileServlet was null.");
        }
        return UriFactory.create(uriString);
    }

    private DisplayStoreControllerBean getDisplayStoreControllerBean() {
        if (this.displayStoreControllerBean == null) {
            return new DisplayStoreControllerBean();
        }
        return this.displayStoreControllerBean;
    }
}
