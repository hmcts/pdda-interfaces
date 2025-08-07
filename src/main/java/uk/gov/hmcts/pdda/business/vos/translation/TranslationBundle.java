package uk.gov.hmcts.pdda.business.vos.translation;

import java.io.Serializable;
import java.util.Locale;

/**
 * <p>
 * Title: Translation Bundle.
 * </p>
 * <p>
 * Description: A translation
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author William Fardell, Xdevelopment (2004)
 * @version $Id: TranslationBundle.java,v 1.3 2006/06/05 12:28:29 bzjrnl Exp $
 */
public interface TranslationBundle extends Serializable {
    /**
     * getLocale.
     * @return the locale for the translation bundle.
     */
    Locale getLocale();

    /**
     * getTranslation.
     * @return the translation for the specified key or null if not found.
     */
    String getTranslation(String key);

    /**
     * getTranslation.
     * @return the translation for the specified key in the given context or null if not found
     */
    String getTranslation(String key, String context);

}
