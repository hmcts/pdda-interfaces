package uk.gov.hmcts.pdda.web.publicdisplay.imaging;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * <p/>
 * Title: Servlet used to generate titles for lists.
 * </p>
 * <p/>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * Pass the following parameters
 * <ul>
 * <li><b>text</b> - the text to be displayed on top of the image.</li>
 * <li><b>width</b> - initial sizing for the image, it will get cropped.</li>
 * <li><b>height</b> - height for the image</li>
 * <li><b>fontColor</b> - <i>optional</i>the color of the font.</li>
 * <li><b>x</b> - horizontal offset for the text.</li>
 * <li><b>y</b> - vertical offset for the textt.</li>
 * <li><b>fontSize</b> - size of the font to use.</li>
 * </ul>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * <p/>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author Neil Ellis
 * @version $Revision: 1.10 $
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class HeadingServlet extends AbstractGraphicsServlet {
    private static final long serialVersionUID = 1L;
    private static final int SHADOW_OFFSET = 2;

    private static final Logger LOG = LoggerFactory.getLogger(HeadingServlet.class);

    @Override
    public void doImage(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        try {
            LOG.debug("doImage({},{})", req, res);
            final int width = parseInt(req, "width");
            final int height = parseInt(req, "height");
            final int xposition = parseInt(req, "x");
            final int yposition = parseInt(req, "y");
            final int fontSize = parseInt(req, "fontSize");
            final String text = req.getParameter("text");
            BufferedImage sourceImage =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = sourceImage.createGraphics();
            Font font = new Font("Arial", Font.BOLD, fontSize);
            graphics.setFont(font);
            ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Colors.TRANSLUCENT_GREY.colour);
            graphics.drawString(text, xposition - SHADOW_OFFSET, yposition + SHADOW_OFFSET);
            graphics.setColor(getFontColor(req));
            graphics.drawString(text, xposition, yposition);
            graphics.dispose();
            res.setContentType("image/jpeg");
            ImageIO.write(
                sourceImage.getSubimage(0, 0,
                    (int) font.getStringBounds(text, ((Graphics2D) graphics).getFontRenderContext())
                        .getWidth() + SHADOW_OFFSET * 2,
                    height - 1),
                "jpeg", res.getOutputStream());
        } catch (IOException e) {
            LOG.error("Error: {}", e.getMessage());
        }
    }

    private int parseInt(HttpServletRequest req, String param) {
        return Integer.parseInt(req.getParameter(param));
    }

    private Color getFontColor(HttpServletRequest req) {
        String colorStr = req.getParameter("fontColor");
        Color fontColor;
        if (colorStr != null) {
            fontColor = new Color(Integer.parseInt(colorStr, 16));
        } else {
            fontColor = Colors.TEXT_FOREGROUND.colour;
        }
        return fontColor;
    }

}
