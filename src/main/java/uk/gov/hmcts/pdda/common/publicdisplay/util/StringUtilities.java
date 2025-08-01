package uk.gov.hmcts.pdda.common.publicdisplay.util;

import java.util.Locale;

/**

 * Title: StringUtilities.


 * Description:


 * Copyright: Copyright (c) 2004


 * Company: EDS

 * @author Rakesh Lakhani
 * @version $Id: StringUtilities.java,v 1.5 2006/06/05 12:28:26 bzjrnl Exp $
 */

public final class StringUtilities {
    private StringUtilities() {
    }

    /**
     * Converts a string into Sentence Case.

     * @param value String
     * @return String
     */
    public static String toSentenceCase(String value) {
        if (value != null) {
            String displayName = value.toLowerCase(Locale.getDefault()).trim();
            boolean nextLetterUpperCase = true;

            char[] tempChars = new char[displayName.length()];
            int chrNumber = 0;

            for (char displayChar : displayName.toCharArray()) {
                if (nextLetterUpperCase) {
                    tempChars[chrNumber] = Character.toUpperCase(displayChar);
                } else {
                    tempChars[chrNumber] = displayChar;
                }
                nextLetterUpperCase = !Character.isLetter(displayChar);
                chrNumber++;    
            }
            return String.copyValueOf(tempChars);
        }
        return null;
    }

}
