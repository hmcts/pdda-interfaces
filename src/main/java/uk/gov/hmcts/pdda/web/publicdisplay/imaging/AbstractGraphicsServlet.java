package uk.gov.hmcts.pdda.web.publicdisplay.imaging;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.web.publicdisplay.imaging.exceptions.ImageLoadingException;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * <p/>
 * Title: Abstract Graphics Servlet.
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
 * @version $Revision: 1.24 $
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public abstract class AbstractGraphicsServlet extends HttpServlet implements ImageObserver {

    private static final long serialVersionUID = 1L;

    static final int AUTOMATIC_WIDTH = -1;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractGraphicsServlet.class);

    private static final int IMAGE_DRAW_TIMEOUT = 500;

    private static long lastModifiedTime;

    /**
     * This method is used by the standard servlet service() method to ascertain whether the doGet()
     * method should be called or not on a GET request. If the lastModified time is more recent thn
     * the If-Modified-Since header attribute then doGet() will be called, otherwise the servlet
     * will return a 304 Not Modified response to the browser.<br/>
     * <p/>
     * In the case of our graphics our default behaviour is to return a last modified time of 0 (Jan
     * 1970) while starting up, so we avoid doing any heavy work during system initialization. If
     * however the browser does not have a cached copy then doGet() will still get called. Once the
     * system is initialized we return the current time and then always return that time for the
     * life of the JVM. This means that we only serve an image to a given browser once in the JVMs
     * lifetime. Our images are not dynamically changing so this suffices.
     * 
     * @param request the HttpServletRequest
     * 
     * @return the modified date as specifed above in milliseconds since 1970 as a long value.
     */
    @Override
    protected long getLastModified(HttpServletRequest request) {
        // This is a hack to make sure that images are not serverd before
        // the service is initialized. This is to reduce load on the servers.
        if (lastModifiedTime == 0 && InitializationService.getInstance().isInitialized()) {
            lastModifiedTime = System.currentTimeMillis();
        }

        if (LOG.isDebugEnabled()) {
            String header = request.getHeader("If-Modified-Since");
            LOG.debug("If modified since {}", header);
            LOG.debug("Returing fake last modified time of {}", new Date(lastModifiedTime));
        }

        return lastModifiedTime;
    }

    /**
     * Standard support for doGet(), we only support the GET method. This calls the abstract method
     * doImage() which does all the work, exceptions are caught and rethrown as a ServletException.
     * 
     * @see super#doGet(jakarta.servlet.http.HttpServletRequest,
     *      jakarta.servlet.http.HttpServletResponse)
     */
    @Override
    public final void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException {
        LOG.debug("doGet({},{})", req, res);
        try {
            doImage(req, res);
        } catch (ServletException | IOException t) {
            LOG.error("Exception thrown in Image Servlet.", t);
        }
    }

    /**
     * The abstract business method.
     * 
     * @param req Servlet Request
     * @param res Servlet Response
     * 
     * @throws ServletException Exception
     * @throws IOException Exception
     */
    protected abstract void doImage(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException;

    /**
     * Load an Image from the given url, waiting until the entire image is loaded.
     * 
     * @param url the URL to use to find the image.
     * 
     * @return the image at the URL
     * 
     * @throws ImageLoadingException if the image could not be loaded.
     */
    Image obtainImage(final URL url) {
        ImageLoader loader = new ImageLoader(url, true);
        if (!loader.isImageLoaded()) {
            LOG.error("Image could not be loaded, please make sure the frame buffer is running "
                + "and xhost + has been executed, ie this process can access the local x windows process.");
            throw new ImageLoadingException(url);
        }
        return loader.getImage();
    }

    /**
     * Write text on to an image.
     * 
     * @param image the image to write on top of.
     * @param text the text to write.
     * @param xposition offset from the left.
     * @param yposition offset from the top.
     * @param size the font size.
     * @param width the width of the produced image.
     * @param fontColor the color of the text.
     * 
     * @return the produced image.
     */
    BufferedImage writeTextOnImage(final Image image, final String text, final int xposition,
        final int yposition, final int size, int width, Color fontColor) {
        LOG.debug("writeTextOnImage({},{},{},{},{})", xposition, yposition, size,
            width, fontColor);
        int widthToUse = (width == AUTOMATIC_WIDTH) ? image.getWidth(null) : width;
        int height = image.getHeight(null);

        BufferedImage bufferedImage =
            new BufferedImage(widthToUse, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.createGraphics();

        // Draw Background
        ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, widthToUse, image.getHeight(null));

        // Draw Image
        drawImageWithWait(graphics, image);

        Font font = new Font("Arial", Font.PLAIN, size);
        FontMetrics metrics = graphics.getFontMetrics(font);
        graphics.setFont(font);

        // Reposition text to center if position is negative
        int sx;

        if (xposition < 0) {
            int textWidth = metrics.stringWidth(text);
            LOG.debug("writeTextOnImage textWidth: " + textWidth);
            sx = (int) ((widthToUse - textWidth) / 2.0);
            LOG.debug("writeTextOnImage sx: " + sx);
        } else {
            sx = xposition;
        }

        int sy;
        if (yposition < 0) {
            int textHeight = metrics.getAscent();
            LOG.debug("writeTextOnImage textHeight: " + textHeight);
            sy = (int) (height - ((height - textHeight) / 2.0));
            LOG.debug("writeTextOnImage sy: " + sy);
        } else {
            sy = yposition;
        }

        // Draw Text
        graphics.setColor(Colors.TRANSLUCENT_WHITE.colour);
        graphics.drawString(text, sx - 2, sy + 2);
        graphics.setColor(fontColor);
        graphics.drawString(text, sx, sy);
        graphics.dispose();

        return bufferedImage;
    }

    /**
     * Draws an Image onto a Graphics object at the top left corner and waits until the image is
     * drawn before returning.
     * 
     * @param graphics the Graphics object.
     * @param image the Image to be drawn.
     */
    protected void drawImageWithWait(Graphics graphics, final Image image) {
        for (boolean done = false; !done; done = graphics.drawImage(image, 0, 0, this)) {
            try {
                LOG.info("Waiting on an image to be drawn.");
                synchronized (this) {
                    wait(IMAGE_DRAW_TIMEOUT);
                }
            } catch (InterruptedException e) {
                LOG.warn(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Callback to let us know when an image has been completely drawn.
     */
    @Override
    public boolean imageUpdate(Image img, int infoflags, int xposition, int yposition, int width,
        int height) {
        synchronized (this) {
            if ((infoflags & ALLBITS) != 0) {
                LOG.info("Image update received and all bits are loaded.");
                notifyAll();
            }
            return true;
        }
    }
}
