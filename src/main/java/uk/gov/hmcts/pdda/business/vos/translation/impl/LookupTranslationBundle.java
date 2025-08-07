package uk.gov.hmcts.pdda.business.vos.translation.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title: Lookup Translation Bundle.
 * </p>
 * <p>
 * Description: Stores a set of translations
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Electronic Data Systems
 * </p>
 * 
 * @author William Fardell, Xdevelopment (2004)
 * @version $Id: LookupTranslationBundle.java,v 1.3 2005/11/24 14:35:15 bzjrnl Exp $
 */
public class LookupTranslationBundle extends AbstractTranslationBundle {
    private static final long serialVersionUID = 1L;
    
    private static final int READING_LETTER_OR_DIGIT = 0;

    private static final int READING_WHITESPACE_OR_PUNCTUATION = 1; // Not Letter Or Digit

    private static final int READING_LEADING_WHITESPACE_OR_PUNCTUATION = 2; // Leading


    private final Map<String, Object> keyMap = new ConcurrentHashMap<>();

    private final Map<String, Object> ruleKeyMap = new ConcurrentHashMap<>();

    /**
     * LookupTranslationBundle.
     * 
     * @param locale the locale for the bundle.
     * @throws IllegalArgumentException if the locale is null
     */
    public LookupTranslationBundle(Locale locale) {
        super(locale);
    }

    /**
     * getTranslation.
     * 
     * @return the translation for the specified key or null if not found
     */
    @Override
    public String getTranslation(String key) {
        return getTranslation(key, null);
    }

    /**
     * getTranslation.
     * 
     * @return the translation for the specified key in the given context or null if not found
     */
    @Override
    public String getTranslation(String key, String context) {
        String translation = getTranslation(keyMap, key, context);
        if (translation != null) {
            return translation;
        }
        return getTranslation(ruleKeyMap, createRuleKey(key), context);
    }
    
    private static String getTranslation(Map<String, Object> map, String key, String context) {
        Object object = map.get(key);
        if (object instanceof TranslationValue) {
            TranslationValue translationValue = (TranslationValue) object;
            if (equalTo(translationValue.context, context)) {
                return translationValue.translation;
            }
        } else if (object instanceof TranslationValue[]) {
            TranslationValue[] translationValues = (TranslationValue[]) object;
            for (TranslationValue translationValue : translationValues) {
                if (equalTo(translationValue.context, context)) {
                    return translationValue.translation;
                }
            }
        }
        return null;
    }

    /**
     * Add the translation to the bundle.
     */
    public void addTranslation(String key, String translation, String context, boolean exactMatch) {
        if (key == null) {
            throw new IllegalArgumentException("key: null");
        }
        if (translation == null) {
            throw new IllegalArgumentException("translation: null");
        }

        TranslationValue value = new TranslationValue(translation, context);

        addTranslation(keyMap, key, value);
        if (!exactMatch) {
            addTranslation(ruleKeyMap, createRuleKey(key), value);
        }
    }
    
    private static void addTranslation(Map<String, Object> map, String key,
        TranslationValue value) {
        Object existingObject = map.get(key);
        if (existingObject instanceof TranslationValue) {
            TranslationValue[] newValues = new TranslationValue[2];
            newValues[0] = (TranslationValue) existingObject;
            newValues[1] = value;
            map.put(key, newValues);
        } else if (existingObject instanceof TranslationValue[]) {
            TranslationValue[] existingValues = (TranslationValue[]) existingObject;
            TranslationValue[] newValues = new TranslationValue[existingValues.length + 1];
            System.arraycopy(existingValues, 0, newValues, 0, existingValues.length);
            newValues[existingValues.length] = value;
            map.put(key, newValues);
        } else {
            map.put(key, value);
        }
    }

    /**
     * Append the xml for this bundle to the buffer.
     * 
     * @throws IllegalArgumentException if buffer is null
     */
    public XmlBuffer appendXml(XmlBuffer buffer, int indent) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer: null");
        }

        XmlBuffer bufferToUse = buffer;
        int indentToUse = indent;
        bufferToUse.appendIndent(indentToUse++);
        bufferToUse.append("<B");
        bufferToUse.appendAttribute("l", getLanguage());
        bufferToUse.appendAttribute("c", getCountry());
        bufferToUse.appendLine(">");

        appendXml(bufferToUse, indentToUse, "K", keyMap);
        appendXml(bufferToUse, indentToUse, "R", ruleKeyMap);

        bufferToUse.appendIndent(--indentToUse);
        bufferToUse.appendLine("</B>");
        return bufferToUse;
    }

    //
    // Mics util
    //

    private static void appendXml(XmlBuffer buffer, int indent, String keyElement,
        Map<String, Object> map) {
        List<String> keyList = new ArrayList<>();
        keyList.addAll(map.keySet());
        Collections.sort(keyList);
        Iterator<String> keys = keyList.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            appendXml(buffer, indent, keyElement, key, map.get(key));
        }
    }

    private static void appendXml(XmlBuffer buffer, int indent, String keyElement, String key,
        Object object) {
        if (object instanceof TranslationValue) {
            appendXml(buffer, indent, keyElement, key, (TranslationValue) object);
        } else if (object instanceof TranslationValue[]) {
            TranslationValue[] translationValues = (TranslationValue[]) object;
            for (TranslationValue translationValue : translationValues) {
                appendXml(buffer, indent, keyElement, key, translationValue);
            }
        }
    }

    private static void appendXml(XmlBuffer buffer, int indent, String keyElement, String key,
        TranslationValue value) {
        int indentToUse = indent;
        buffer.appendIndent(indentToUse++);
        buffer.appendLine("<V>");

        // Append key K or ruleKey R
        buffer.appendIndent(indentToUse);
        buffer.append("<");
        buffer.append(keyElement);
        buffer.append(">");
        buffer.appendXml(key);
        buffer.append("</");
        buffer.append(keyElement);
        buffer.appendLine(">");

        // Append translation T
        buffer.appendIndent(indentToUse);
        buffer.append("<T>");
        buffer.appendXml(value.translation);
        buffer.appendLine("</T>");

        // Append context C if present
        if (value.context != null) {
            buffer.appendIndent(indentToUse);
            buffer.append("<C>");
            buffer.appendXml(value.context);
            buffer.appendLine("</C>");
        }

        buffer.appendIndent(--indentToUse);
        buffer.appendLine("</V>");
    }

    // Not
    // Letter
    // Or
    // Digit

    protected static final String createRuleKey(String key) {
        if (key == null) {
            return null;
        }

        char[] source = key.toCharArray();
        char[] buffer = new char[source.length]; // Safe as must be equal
        // or
        // less

        int bufferIndex = 0;

        int state = READING_LEADING_WHITESPACE_OR_PUNCTUATION;
        for (char sourceChar : source) {
            if (state == READING_LETTER_OR_DIGIT) {
                if (Character.isLetterOrDigit(sourceChar)) {
                    buffer[bufferIndex++] = Character.toLowerCase(sourceChar);
                } else {
                    state = READING_WHITESPACE_OR_PUNCTUATION;
                }
            } else if (state == READING_WHITESPACE_OR_PUNCTUATION
                && Character.isLetterOrDigit(sourceChar)) {
                buffer[bufferIndex++] = ' ';
                buffer[bufferIndex++] = Character.toLowerCase(sourceChar);
                state = READING_LETTER_OR_DIGIT;
            } else if (state == READING_LEADING_WHITESPACE_OR_PUNCTUATION
                && Character.isLetterOrDigit(sourceChar)) {
                buffer[bufferIndex++] = Character.toLowerCase(sourceChar);
                state = READING_LETTER_OR_DIGIT;
            }
        }
        return new String(buffer, 0, bufferIndex);
    }

    private static boolean equalTo(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    //
    // Misc Util Class
    //

    private static final class TranslationValue implements Serializable {
        private static final long serialVersionUID = 1L;

        public final String translation;

        public final String context;

        public TranslationValue(String translation, String context) {
            this.translation = translation;
            this.context = context;
        }
    }

}
