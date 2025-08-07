package uk.gov.hmcts.pdda.business.vos.translation;

import uk.gov.hmcts.pdda.business.vos.translation.impl.DefaultTranslationBundle;
import uk.gov.hmcts.pdda.business.vos.translation.impl.EmptyTranslationBundle;
import uk.gov.hmcts.pdda.business.vos.translation.impl.LookupTranslationBundle;
import uk.gov.hmcts.pdda.business.vos.translation.impl.XmlBuffer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title: Translation Bundles.
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
 * @version $Id: TranslationBundles.java,v 1.4 2006/06/05 12:28:29 bzjrnl Exp $
 */
@SuppressWarnings("PMD.AvoidSynchronizedStatement")
public class TranslationBundles implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Locale, TranslationBundle> translationBundleMap = new ConcurrentHashMap<>();

    private String xml;

    /**
     * Construct a new translation bundle collection.
     */
    public TranslationBundles(Locale defaultLocale) {
        if (defaultLocale == null) {
            throw new IllegalArgumentException("defaultLocale: null");
        }
        translationBundleMap.put(defaultLocale, new DefaultTranslationBundle(defaultLocale));
    }

    /**
     * Get the translation bundle for the given locale, else return null.
     * 
     * @param locale the locale to fetch
     * @return the translation bundle
     */
    public TranslationBundle getTranslationBundle(Locale locale) {
        synchronized (this) {
            if (locale == null) {
                throw new IllegalArgumentException("locale: null");
            }
    
            // Do we have a bundle!
            TranslationBundle translationBundle = translationBundleMap.get(locale);
            if (translationBundle != null) {
                return translationBundle;
            }
    
            // If not create default if locale default
            return new EmptyTranslationBundle(locale);
        }
    }

    /**
     * Add a translation to the bundles.
     * 
     * @param key String
     * @param translation String
     * @param context String
     * @param exactMatch boolean
     */
    public void addTranslation(Locale locale, String key,
        String translation, String context, boolean exactMatch) {
        synchronized (this) {
            validateTranslation(locale, key, translation);
    
            // Add translation to existing if not default, otherwise create new.
            Object translationBundle = translationBundleMap.get(locale);
            if (translationBundle instanceof LookupTranslationBundle) {
                ((LookupTranslationBundle) translationBundle).addTranslation(key, translation, context,
                    exactMatch);
            } else if (translationBundle instanceof DefaultTranslationBundle) {
                throw new IllegalStateException("Can not add translations to default locale.");
            } else {
                LookupTranslationBundle lookupTranslationBundle = new LookupTranslationBundle(locale);
                lookupTranslationBundle.addTranslation(key, translation, context, exactMatch);
                translationBundleMap.put(locale, lookupTranslationBundle);
            }
            // Clear the xml cache
            setXml(null);
        }
    }

    /**
     * Get the xml refreshing the cache if necessary.
     */
    public String toXml() {
        synchronized (this) {
            if (xml == null) {
                XmlBuffer buffer = new XmlBuffer();
    
                buffer.appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                buffer.appendLine(
                    "<D xmlns=\"http://www.courtservice.gov.uk/schemas/courtservice/xhibit/translation\">");
    
                Iterator<TranslationBundle> translationBundles =
                    translationBundleMap.values().iterator();
                while (translationBundles.hasNext()) {
                    Object translationBundle = translationBundles.next();
                    if (translationBundle instanceof LookupTranslationBundle) {
                        buffer = ((LookupTranslationBundle) translationBundle).appendXml(buffer, 1);
                    }
                }
    
                buffer.appendLine("</D>");
    
                setXml(buffer.toString());
            }
            return xml;
        }
    }
    
    private void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * Get the Xml for this object.
     */
    @Override
    public String toString() {
        return toXml();
    }

    //
    // Utils
    //
    private void validateTranslation(Locale locale, String key,
        String translation) {
        if (locale == null) {
            throw new IllegalArgumentException("locale: null");
        }
        if (key == null) {
            throw new IllegalArgumentException("key: null");
        }
        if (translation == null) {
            throw new IllegalArgumentException("translation: null");
        }
    }
}
