package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

/**
 * @author pznwc5
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class TranslationType {
    public static final TranslationType GUI = new TranslationType(0);

    public static final TranslationType PUBLIC_DISPLAY = new TranslationType(1);

    public static final TranslationType PUBLIC_NOTCE = new TranslationType(2);

    public static final TranslationType CJSE = new TranslationType(3);

    public static final TranslationType INTERNET = new TranslationType(4);

    private int type;

    private TranslationType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

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
        if ("GUI".equalsIgnoreCase(type)) {
            return GUI;
        }
        if ("DISPLAY".equalsIgnoreCase(type)) {
            return PUBLIC_DISPLAY;
        }
        if ("NOTCE".equalsIgnoreCase(type)) {
            return PUBLIC_NOTCE;
        }
        if ("CJSE".equalsIgnoreCase(type)) {
            return CJSE;
        }
        if ("INTERNET".equalsIgnoreCase(type)) {
            return INTERNET;
        }
        throw new IllegalArgumentException("type: " + type);
    }

}