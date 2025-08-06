package uk.gov.hmcts.pdda.business.vos.translation.impl;

import java.util.Locale;

/**

 * Title: The default translation bundle assumes the keys are in the default language and therfore
 * contain the correct text.


 * Description: A translation


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author William Fardell, Xdevelopment (2004)
 * @version $Id: DefaultTranslationBundle.java,v 1.1 2005/11/23 16:13:38 bzjrnl Exp $
 */
public class DefaultTranslationBundle extends AbstractTranslationBundle {
    private static final long serialVersionUID = 1L;

    /**
     * Create a new default translation bundle.

     * @param locale Locale
     */
    public DefaultTranslationBundle(Locale locale) {
        super(locale);
    }

    /**
     * Return the key.

     * @return the translation for the specified key or null if not found
     */
    @Override
    public String getTranslation(String key) {
        return key;
    }

    /**
     * Return the key.

     * @return the translation for the specified key in the given context or null if not found
     */
    @Override
    public String getTranslation(String key, String context) {
        return key;
    }

}
