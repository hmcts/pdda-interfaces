package uk.gov.hmcts.pdda.common.publicdisplay.util.comparables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * <p>
 * Title: Terminating Digit Aware String Comparable.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * This class is a wrapper class intended to allow the sorting of strings to take into account their
 * terminating digits. So for example Test 2 will come before Test 12 and Test will come before Test
 * 1.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Bob Boothby
 * @version $Id: DigitAwareStringComparable.java,v 1.1 2004/01/30 16:18:18 sz0t7n Exp $
 */

public class DigitAwareStringComparable implements Comparable<Object>, Serializable {

    static final long serialVersionUID = 2685758332281373392L;
    
    private static final Logger LOG = LoggerFactory.getLogger(DigitAwareStringComparable.class);

    private static final String DIGITS = "0123456789";

    private final String text;

    private final String body;

    private final String digitsString;

    private final int digitsAsNumber;

    /**
     * Create an instance of the class from a String.
     * 
     * @param text The String to make into a DigitAwareStringComparable.
     */
    public DigitAwareStringComparable(String text) {
        // We absolutely want to do all this calculation here so it is done once
        // only. The Constructor should be called only once, the compare method
        // will be called many times and so would be the worst place to do it.
        this.text = text;

        // start at end.
        int index = text.length() - 1;

        // Step through from the end looking for the terminal digits.
        while (DIGITS.indexOf(text.charAt(index)) > -1 && index > -1) {
            index--;
        }
        // Index currently points at the character before the first terminal
        // digit,
        // so add one.
        index++;

        // Extract the separate components.
        this.body = text.substring(0, index);
        this.digitsString = text.substring(index, text.length());

        // Sort out the numeric value of the digits.
        if (digitsString.length() > 0) {
            this.digitsAsNumber = Integer.parseInt(digitsString);
        } else {
            this.digitsAsNumber = 0;
        }
    }

    /**
     * Compare the values represented by two instances of DigitAwareStringComparable.
     * 
     * @param object The DigitAwareStringComparable to compare this one with.
     * @return an int value representing the absolute ordering of this object wrt the one passed in.
     * @throws ClassCastException if the object passed in is not an instance of
     *         DigitAwareStringComparable;
     */
    @Override
    public int compareTo(Object object) {
        // Note that the most likely scenarios are dispatched quickest and that
        // direct access to the fields is used for performance.
        DigitAwareStringComparable compared = (DigitAwareStringComparable) object;

        int returnValue = body.compareTo(compared.body);
        if (returnValue == 0) { // The bodies are the same.
            // Now compare the digits.
            returnValue = digitsAsNumber - compared.digitsAsNumber;
            if (returnValue == 0) { // the value of the digits are the same
                // Sort on the textual representation of the digits.
                returnValue = digitsString.compareTo(compared.digitsString);
            }
        }
        return returnValue;
    }

    /**
     * Checks whether an object is equal to this one.
     * 
     * @param object The object to check for equality.
     * @return true if the object is equivalent to this one.
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof DigitAwareStringComparable) {
            DigitAwareStringComparable compared = (DigitAwareStringComparable) object;
            return text.equals(compared.text);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        LOG.debug("hashCode()");
        return super.hashCode();
    }

    @Override
    public String toString() {
        return text;
    }
}
