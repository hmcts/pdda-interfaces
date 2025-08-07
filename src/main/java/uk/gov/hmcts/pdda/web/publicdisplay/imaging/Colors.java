package uk.gov.hmcts.pdda.web.publicdisplay.imaging;

import java.awt.Color;

/**
 * <p/>
 * Title: Colors.
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
 * @version $Revision: 1.2 $
 */
public enum Colors {
    TRANSLUCENT_GREY(new Color(0x40, 0x40, 0x40, 0x40)),

    TRANSLUCENT_WHITE(new Color(0xFF, 0xFF, 0xFF, 0x40)),

    TEXT_FOREGROUND(new Color(0x00, 0x11, 0x22));

    Color colour;

    Colors(Color colour) {
        this.colour = colour;
    }
}
