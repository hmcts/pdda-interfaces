package uk.gov.hmcts.pdda.web.publicdisplay.imaging;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;



/**
 * <p/>
 * Title: THe abstract superclass for any servlet that modifies images.
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
 * @version $Revision: 1.7 $
 */
public abstract class AbstractImageServlet extends AbstractGraphicsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractImageServlet.class);

    @Override
    public final void doImage(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try {

            LOG.debug(getServletContext().getRealPath(request.getPathInfo()));
            URL url = this.getServletContext().getResource("/WEB-INF" + request.getPathInfo());
            LOG.debug("Resolved image URL: {}", url);
            Image sourceImage = obtainImage(url);
            if (sourceImage == null) {
                LOG.warn("Source image is null for URL: {}", url);
            } else {
                LOG.debug("Successfully obtained source image");
            }

            // Do our custim processing on the image
            Image resultImage = processImage(sourceImage, request);

            // Output the finished image straight to the response as a JPEG!
            response.setHeader("Cache-Control", "must-revalidate");
            response.setContentType("image/jpeg");
            ImageIO.write((RenderedImage) resultImage, "jpeg", response.getOutputStream());

        } catch (Exception e) {
            LOG.error("Error in servlet: {}", e.getMessage());
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e2) {
                LOG.error("IOException in sendError: {}", e2.getMessage());
            }
        }
    }

    /**
     * The business method.
     */
    protected abstract Image processImage(Image sourceImage, HttpServletRequest req);

}
