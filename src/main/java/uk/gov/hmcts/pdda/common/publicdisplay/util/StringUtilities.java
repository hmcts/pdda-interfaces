package uk.gov.hmcts.pdda.common.publicdisplay.util;

import java.util.Locale;

/**
 * <p>
 * Title: StringUtilities.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Rakesh Lakhani
 * @version $Id: StringUtilities.java,v 1.5 2006/06/05 12:28:26 bzjrnl Exp $
 */

public final class StringUtilities {
    private StringUtilities() {
    }

    /**
     * Converts a string into Sentence Case.
     * 
     * @param value String
     * @return String
     */
    public static String toSentenceCase(String value) {
        if (value != null) {
            String displayName = value.toLowerCase(Locale.getDefault()).trim();
            boolean nextLetterUpperCase = true;

            char[] displayChars = displayName.toCharArray();
            char[] tempChars = displayName.toCharArray();

            for (int i = 0; i < displayName.length(); i++) {
                if (nextLetterUpperCase) {
                    tempChars[i] = Character.toUpperCase(displayChars[i]);
                } else {
                    tempChars[i] = displayChars[i];
                }
                nextLetterUpperCase = !Character.isLetter(displayChars[i]);
            }
            return String.copyValueOf(tempChars);
        }
        return null;
    }

}
