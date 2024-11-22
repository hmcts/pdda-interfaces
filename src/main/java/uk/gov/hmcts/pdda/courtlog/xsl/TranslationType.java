/*
 * Created on Apr 14, 2004
 *
 * To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */

package uk.gov.hmcts.pdda.courtlog.xsl;

/**
 * Translation type.
 * @author pznwc5
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java
 *         - Code Generation - Code and Comments
 */
public final class TranslationType {
    public static final TranslationType GUI = new TranslationType(0);

    public static final TranslationType PUBLIC_DISPLAY = new TranslationType(1);

    public static final TranslationType PUBLIC_NOTCE = new TranslationType(2);

    public static final TranslationType CJSE = new TranslationType(3);

    public static final TranslationType INTERNET = new TranslationType(4);
    
    public static final String GUI_STRING = "GUI";
    
    public static final String DISPLAY_STRING = "DISPLAY";
    
    public static final String NOTCE_STRING = "NOTCE";
    
    public static final String CJSE_STRING = "CJSE";
    
    public static final String INTERNET_STRING = "INTERNET";

    private final int type;

    private TranslationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        switch (type) {
            case 0:
                return "GUI";
            case 1:
                return "DISPLAY";
            case 2:
                return "NOTCE";
            case 3:
                return "CJSE";
            case 4:
                return "INTERNET";
            default:
                throw new IllegalStateException("type: " + type);
        }
    }

    public static TranslationType valueOf(String type) {
        if (GUI_STRING.equalsIgnoreCase(type)) {
            return GUI;
        }
        if (DISPLAY_STRING.equalsIgnoreCase(type)) {
            return PUBLIC_DISPLAY;
        }
        if (NOTCE_STRING.equalsIgnoreCase(type)) {
            return PUBLIC_NOTCE;
        }
        if (CJSE_STRING.equalsIgnoreCase(type)) {
            return CJSE;
        }
        if (INTERNET_STRING.equalsIgnoreCase(type)) {
            return INTERNET;
        }
        throw new IllegalArgumentException("type: " + type);
    }

}
