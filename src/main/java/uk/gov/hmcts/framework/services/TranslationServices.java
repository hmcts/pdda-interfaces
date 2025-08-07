package uk.gov.hmcts.framework.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundles;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationEnum;
import uk.gov.hmcts.pdda.business.vos.translation.impl.LookupTranslationBundle;
import uk.gov.hmcts.pdda.web.publicdisplay.initialization.servlet.InitializationService;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public final class TranslationServices {

    private static Logger log = LoggerFactory.getLogger(TranslationServices.class);

    /**
     * The singleton instance.
     */
    private static final TranslationServices INSTANCE = new TranslationServices();

    /**
     * Translation bundles Map.
     */
    private Map<TranslationEnum, TranslationBundles> translationBundlesMap =
        new ConcurrentHashMap<>();

    /**
     * Get the singleton.
     * 
     * @return the ResourceServices singleton instance
     */
    public static TranslationServices getInstance() {
        return INSTANCE;
    }

    /**
     * Stop external construction of the singleton.
     */
    private TranslationServices() {
    }

    public Map<TranslationEnum, TranslationBundles> getTranslationBundlesMap() {
        return this.translationBundlesMap;
    }

    public void setTranslationBundlesMap(
        Map<TranslationEnum, TranslationBundles> translationBundlesMap) {
        this.translationBundlesMap = translationBundlesMap;
    }

    public TranslationBundles getTranslationBundles(TranslationEnum translationEnum) {
        if (translationEnum != null) {
            if (!getTranslationBundlesMap().containsKey(translationEnum)) {
                getTranslationBundlesMap().put(translationEnum,
                    new TranslationBundles(InitializationService.getInstance().getDefaultLocale()));
            }
            return getTranslationBundlesMap().get(translationEnum);
        }
        return new TranslationBundles(InitializationService.getInstance().getDefaultLocale());
    }

    public TranslationBundle getTranslationBundle(TranslationEnum translationEnum, Locale locale) {
        TranslationBundles translationBundles = getTranslationBundles(translationEnum);
        TranslationBundle translationBundle = translationBundles.getTranslationBundle(locale);
        if (translationEnum != null && !(translationBundle instanceof LookupTranslationBundle)) {
            translationBundle = loadTranslationBundles(translationEnum, locale);
        }
        return translationBundle;
    }

    private TranslationBundle loadTranslationBundles(TranslationEnum translationEnum,
        Locale locale) {
        log.debug("loadTranslationBundles()");
        LookupTranslationBundle translationBundle = new LookupTranslationBundle(locale);
        try (
            // Load the properties from the resource
            InputStream inStream = ResourceServices.getInstance()
                .getResourceAsStream(translationEnum.getResourceName(), locale)) {
            Properties properties = new Properties();
            properties.clear();
            if (inStream != null) {
                properties.load(inStream);
            }
            // Add the translations to the bundle
            for (Object entry : properties.keySet()) {
                String key = (String) entry;
                String translation = properties.getProperty(key);
                translationBundle.addTranslation(key, translation, null, true);
            }
            log.debug("loaded!");
        } catch (Exception e) {
            log.error("Unable to load resource");
        }
        return translationBundle;
    }
}
