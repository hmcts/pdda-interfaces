package uk.gov.hmcts.pdda.web.publicdisplay.imaging;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Image;



/**
 * <p/>
 * Title: Servlet used to supply the header image at the top of each public display list.

 * <p/>
 * <p/>
 * Description:

 * <p/>
 * Pass the path to the image as the part of the url after the servlet path. <br/>
 * <p/>
 * Pass the following parameters
 * <ul>
 * <li><b>text</b> - the text to be displayed on top of the image.</li>
 * <li><b>x</b> - horizontal offset for the text.</li>
 * <li><b>y</b> - vertical offset for the textt.</li>
 * <li><b>size</b> - size of the font to use.</li>
 * <li><b>width</b> - <i>optional </i>width to use for the created image, default is to use the
 * width of the image.</li>
 * </ul>
 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.8 $
 */
public class HeaderImageServlet extends AbstractImageServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(HeaderImageServlet.class);

    /**
     * Here we take the loaded image and then write the text upon it.
     */
    @Override
    protected Image processImage(Image sourceImage, HttpServletRequest req) {

        LOG.debug("processImage() for " + req.getPathInfo() + "?" + req.getQueryString());
        LOG.info(">>>> HeaderImageServlet: processImage() called for {}", req.getPathInfo());

        int width = AUTOMATIC_WIDTH;
        String widthStr = req.getParameter("width");
        // If no width parameter
        if (widthStr != null) {
            width = Integer.parseInt(widthStr);
        }
        return writeTextOnImage(sourceImage, req.getParameter("text"),
            Integer.parseInt(req.getParameter("x")), Integer.parseInt(req.getParameter("y")),
            Integer.parseInt(req.getParameter("size")), width, new Color(0x00, 0x11, 0x33));
    }

}
