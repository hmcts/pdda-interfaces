package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

/**
 * NameHelper.
 * 
 * @author Bob boothby Helper class for composing names
 */
public class NameHelper {

    protected NameHelper() {
        // Protected constructor
    }

    /**
     * Utility method to construct the name of a defendant from a DefendantBasicValue.
     * 
     * @param firstName First Name
     * @param middleName Middle Name
     * @param surname Surname
     * 
     * @return A well formatted name as a string.
     */
    protected static String buildName(String firstName, String middleName, String surname) {
        StringBuilder sb = new StringBuilder();
        appendName(sb, firstName);
        appendName(sb, middleName);
        appendName(sb, surname);

        return sb.toString();
    }

    /**
     * Utility method to construct the name of a defendant from a DefendantBasicValue.
     * 
     * @param firstName First Name
     * @param middleName Middle Name
     * @param surname Surname
     * 
     * @return A well formatted name as a string.
     */
    protected static String buildNameWithSurnameFirst(String firstName, String middleName,
        String surname) {
        StringBuilder sb = new StringBuilder(surname);
        if (firstName != null || middleName != null) {
            sb.append(',');
            if (firstName != null) {
                sb.append(' ').append(firstName);
            }
            if (middleName != null) {
                sb.append(' ').append(middleName);
            }
        }
        return sb.toString();
    }

    /**
     * Slightly neater way of appending names and doing spaces so that no unnecessary spaces are
     * included... all names to be added should use this method.
     * 
     * @param sb StringBuilder to append to.
     * @param candidateName name to attempt to add, if there is something in sb and candidateName is
     *        not null then a space will be appended before the name is appended.
     */
    private static void appendName(StringBuilder sb, String candidateName) {
        if (candidateName != null && !"".equals(candidateName)) {
            if (sb.length() > 0) {
                sb.append(' ');
            }

            sb.append(candidateName);
        }
    }
}
