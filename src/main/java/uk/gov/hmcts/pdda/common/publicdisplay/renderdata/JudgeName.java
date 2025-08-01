package uk.gov.hmcts.pdda.common.publicdisplay.renderdata;

import uk.gov.hmcts.framework.jdbc.core.PddaRow;

import java.io.Serializable;

/**
 * JudgeName.

 * @author pznwc5 The class represents a judge name.
 */
public class JudgeName implements Serializable {

    static final long serialVersionUID = 8035508514989899210L;

    /**
     * Column name for judge first name.

     * private static final String JUDGE_FIRST_NAME = "JUDGE_FIRST_NAME";

     * /** Column name for judge middle name

     * private static final String JUDGE_MIDDLE_NAME = "JUDGE_MIDDLE_NAME";
     */

    /**
     * Column name for judge surname.
     */
    private static final String JUDGE_SURNAME = "JUDGE_SURNAME";

    /**
     * Column name for full list title column 1. The one to be used when available for listing the
     * case.
     */
    private static final String FULL_LIST_TITLE1 = "FULL_LIST_TITLE1";

    /**
     * Judge name.
     */
    private String name = "";

    /**
     * Judge name.

     * @param row Current row
     */
    public JudgeName(PddaRow row) {
        this(row.getString(FULL_LIST_TITLE1), row.getString(JUDGE_SURNAME));
    }

    /**
     * Judge name.

     * @param fullListTitle1 String
     * @param judgeSurname String
     */
    public JudgeName(String fullListTitle1, String judgeSurname) {
        setName(fullListTitle1);
        if (name == null || name.length() == 0) {
            setName(judgeSurname);
            if (name == null) {
                setName("");
            }
        }
    }

    /**
     * Sets up a JudgeName object from a String value.

     * @param judgeName String
     */
    public JudgeName(String judgeName) {
        setName(judgeName);
        if (judgeName == null) {
            setName("");
        }
    }

    /**
     * Returns the name.

     * @return String
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name.

     * @param name String
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name.
     */
    @Override
    public String toString() {
        return getName();
    }
}
