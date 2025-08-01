
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

public final class RendererCourtSiteUtils {

    private RendererCourtSiteUtils() {
    }
    
    /**
     * Court Site Room Name.

     * @param object Object
     * @param documentI18n TranslationBundle
     * @return The Court room name prefixed with site if exists
     */
    public static String getCourtSiteRoomName(Object object, TranslationBundle documentI18n) {
        String courtSiteShortName = getCourtSiteShortName(object, documentI18n);
        if (courtSiteShortName != null) {
            return courtSiteShortName + " - " + getCourtRoomName(object, documentI18n);
        } else {
            return getCourtRoomName(object, documentI18n);
        }
    }

    /**
     * Court Site Room Name.

     * @param displayDocument DisplayDocument
     * @param documentI18n TranslationBundle
     * @return The Court room name prefixed with site if exists
     */
    public static String getCourtSiteRoomName(DisplayDocument displayDocument,
        TranslationBundle documentI18n) {
        String courtSiteShortName = getCourtSiteShortName(displayDocument, documentI18n);
        if (courtSiteShortName != null) {
            return courtSiteShortName + " - " + getCourtRoomName(displayDocument, documentI18n);
        } else {
            return getCourtRoomName(displayDocument, documentI18n);
        }
    }

    /**
     * Court Site Short Name.

     * @param item Object
     * @param documentI18n TranslationBundle
     * @return String
     */
    private static String getCourtSiteShortName(Object item, TranslationBundle documentI18n) {
        if (item instanceof PublicDisplayValue) {
            String courtSiteShortName = ((PublicDisplayValue) item).getCourtSiteShortName();
            if (courtSiteShortName != null) {
                return TranslationUtils.translateData(documentI18n, courtSiteShortName);
            }
        }
        return null;
    }

    /**
     * Court Site Short Name.

     * @param displayDocument DisplayDocument
     * @param documentI18n TranslationBundle
     * @return The court site short name from the display document
     */
    private static String getCourtSiteShortName(DisplayDocument displayDocument,
        TranslationBundle documentI18n) {
        if (displayDocument != null) {
            Data data = displayDocument.getData();
            if (data != null) {
                String courtSiteShortName;
                courtSiteShortName = data.getCourtSiteShortName();
                if (courtSiteShortName != null) {
                    return TranslationUtils.translateData(documentI18n, courtSiteShortName);
                }
            }
        }
        return null;
    }

    /**
     * Court Name.

     * @param displayDocument DisplayDocument
     * @return the court name from the display document date
     */
    public static String getCourtName(DisplayDocument displayDocument,
        TranslationBundle documentI18n) {
        if (displayDocument != null) {
            Data data = displayDocument.getData();
            if (data != null) {
                String courtName = data.getCourtName();
                if (courtName != null) {
                    return TranslationUtils.translateData(documentI18n, courtName);
                }
            }
        }
        return null;
    }

    /**
     * Court Room Name.

     * @param displayDocument DisplayDocument
     * @param documentI18n TranslationBundle
     * @return the court room name from the display document
     */
    private static String getCourtRoomName(DisplayDocument displayDocument,
        TranslationBundle documentI18n) {
        if (displayDocument != null) {
            Data data = displayDocument.getData();
            if (data != null) {
                String courtRoomName = data.getCourtRoomName();
                if (courtRoomName != null) {
                    return TranslationUtils.translateData(documentI18n, courtRoomName);
                }
            }
        }
        return null;
    }

    /**
     * getCourtRoomName.
     */
    private static String getCourtRoomName(Object item, TranslationBundle documentI18n) {
        if (item instanceof PublicDisplayValue) {
            String courtRoomName = ((PublicDisplayValue) item).getCourtRoomName();
            if (courtRoomName != null) {
                return TranslationUtils.translateData(documentI18n, courtRoomName);
            }
        }
        return null;
    }

    /**
     * Moved from Court Site Short Name.

     * @param item Object
     * @param documentI18n TranslationBundle
     * @return String
     */
    private static String getMovedFromCourtSiteShortName(Object item,
        TranslationBundle documentI18n) {
        if (item instanceof PublicDisplayValue) {
            String movedFromCourtSiteShortName =
                ((PublicDisplayValue) item).getMovedFromCourtSiteShortName();
            if (movedFromCourtSiteShortName != null) {
                return TranslationUtils.translateData(documentI18n, movedFromCourtSiteShortName);
            }
        }
        return null;
    }

    /**
     * Moved From Court Site Room Name.

     * @param item object
     * @param documentI18n TranslationBundle
     * @return The Court room name prefixed with site if exists
     */
    public static String getMovedFromCourtSiteRoomName(Object item,
        TranslationBundle documentI18n) {
        String movedFromCourtSiteShortName = getMovedFromCourtSiteShortName(item, documentI18n);
        if (movedFromCourtSiteShortName != null) {
            return movedFromCourtSiteShortName + " - "
                + getMovedFromCourtRoomName(item, documentI18n);
        } else {
            return getMovedFromCourtRoomName(item, documentI18n);
        }
    }

    private static String getMovedFromCourtRoomName(Object item, TranslationBundle documentI18n) {
        if (item instanceof PublicDisplayValue) {
            String movedFromCourtRoomName = ((PublicDisplayValue) item).getMovedFromCourtRoomName();
            if (movedFromCourtRoomName != null) {
                return TranslationUtils.translateData(documentI18n, movedFromCourtRoomName);
            }
        }
        return null;
    }
}
