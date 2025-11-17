package uk.gov.hmcts.pdda.courtlog.helpers.xsl;

/**
 * TranslationType.

 * @author pznwc5
 */
public final class TranslationType {
    public static final TranslationType GUI = new TranslationType(0);
    public static final TranslationType PUBLIC_DISPLAY = new TranslationType(1);
    public static final TranslationType PUBLIC_NOTCE = new TranslationType(2);
    public static final TranslationType CJSE = new TranslationType(3);
    public static final TranslationType INTERNET = new TranslationType(4);

    private final int type;
    
    private static final String TYPE_GUI = "GUI";
    private static final String TYPE_DISPLAY = "DISPLAY";
    private static final String TYPE_NOTCE = "NOTCE";
    private static final String TYPE_CJSE = "CJSE";
    private static final String TYPE_INTERNET = "INTERNET";
    
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
                return TYPE_GUI;
            case 1:
                return TYPE_DISPLAY;
            case 2:
                return TYPE_NOTCE;
            case 3:
                return TYPE_CJSE;
            case 4:
                return TYPE_INTERNET;
            default:
                throw new IllegalStateException("type: " + type);
        }
    }

    public static TranslationType valueOf(String type) {
        if (TYPE_GUI.equalsIgnoreCase(type)) {
            return GUI;
        }
        if (TYPE_DISPLAY.equalsIgnoreCase(type)) {
            return PUBLIC_DISPLAY;
        }
        if (TYPE_NOTCE.equalsIgnoreCase(type)) {
            return PUBLIC_NOTCE;
        }
        if (TYPE_CJSE.equalsIgnoreCase(type)) {
            return CJSE;
        }
        if (TYPE_INTERNET.equalsIgnoreCase(type)) {
            return INTERNET;
        }
        throw new IllegalArgumentException("type: " + type);
    }

}