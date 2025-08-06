package uk.gov.hmcts.pdda.business.services.translation;

import uk.gov.hmcts.framework.services.CsServices;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;

import java.util.Locale;

/**

 * Title: TranslationBundlesFactory.


 * Description: Concrete instances of this class are found using the discovery pattern.


 * Copyright: Copyright (c) 2003


 * Company: Electronic Data Systems

 * @author William Fardell, Xdevelopment (2004)
 * @version $Id: TranslationBundlesFactory.java,v 1.1 2005/12/01 15:19:56 bzjrnl Exp $
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public abstract class TranslationBundlesFactory {
    private static TranslationBundlesFactory instance;

    /**
     * Get the singleton using the discovery pattern.

     * @return the singleton instance.
     */
    public static TranslationBundlesFactory getInstance() {
        synchronized (TranslationBundlesFactory.class) {
            if (instance == null) {
                instance = createTranslationBundlesFactory();
            }
            return instance;
        }
    }

    /**
     * Create a translation bundles instance for the specified default locale.
     */
    public abstract TranslationBundles createTranslationBundles(Locale defaultLocale);

    private static TranslationBundlesFactory createTranslationBundlesFactory() {
        TranslationBundlesFactory translationBundlesFactory = (TranslationBundlesFactory) CsServices
            .getDiscoveryServices().createInstance(TranslationBundlesFactory.class);
        if (translationBundlesFactory != null) {
            return translationBundlesFactory;
        }
        return null;
    }
}
