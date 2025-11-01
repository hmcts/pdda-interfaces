
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtDetailValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JudgeName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JuryStatusDailyListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("PMD")
public final class AttributesUtils {

    private static final String EMPTY_STRING = "";

    private AttributesUtils() {
    }
    
    /**
     * getTable.

     * @param displayDocument DisplayDocument
     * @return the table from the display document data
     */
    public static Collection<?> getTable(DisplayDocument displayDocument) {
        Collection<?> table = null;
        if (displayDocument != null) {
            Data data = displayDocument.getData();
            if (data != null) {
                table = data.getTable();
            }
        }

        if (table != null) {
            return table;
        } else {
            return new ArrayList<>(0);
        }
    }

    public static String getCaseNumber(Object item) {
        if (item instanceof AllCourtStatusValue) {
            String caseNumber = ((AllCourtStatusValue) item).getCaseNumber();
            if (caseNumber != null) {
                return caseNumber;
            }
        } else if (item instanceof AllCaseStatusValue) {
            String caseNumber = ((AllCaseStatusValue) item).getCaseNumber();
            if (caseNumber != null) {
                return caseNumber;
            }
        } else if (item instanceof CourtListValue) {
            String caseNumber = ((CourtListValue) item).getCaseNumber();
            if (caseNumber != null) {
                return caseNumber;
            }
        }
        return null;
    }

    public static int getHearingProgress(Object item) {
        if (item instanceof AllCaseStatusValue) {
            return ((AllCaseStatusValue) item).getHearingProgress();
        } else if (item instanceof CourtListValue) {
            return ((CourtListValue) item).getHearingProgress();
        }
        return -1;
    }

    public static Collection<DefendantName> getDefendantNames(Object item) {
        if (item instanceof AllCourtStatusValue) {
            Collection<DefendantName> defendantNames =
                ((AllCourtStatusValue) item).getDefendantNames();
            if (defendantNames != null) {
                return defendantNames;
            }
        } else if (item instanceof CourtListValue) {
            Collection<DefendantName> defendantNames = ((CourtListValue) item).getDefendantNames();
            if (defendantNames != null) {
                return defendantNames;
            }
        }
        return new ArrayList<>(0);
    }

    public static String getCaseTitle(Object item) {
        if (item instanceof AllCaseStatusValue) {
            String caseTitle = ((AllCaseStatusValue) item).getCaseTitle();
            if (caseTitle != null) {
                return caseTitle;
            }
        } else if (item instanceof CourtListValue) {
            String caseTitle = ((CourtListValue) item).getCaseTitle();
            if (caseTitle != null) {
                return caseTitle;
            }
        }
        // Acceptable for both case title and defendants not to exist
        return EMPTY_STRING;
    }

    public static String getHearingDescription(TranslationBundle documenti18n, Object item) {
        if (item instanceof AllCaseStatusValue) {
            String description = ((AllCaseStatusValue) item).getHearingDescription();
            if (description != null) {
                return TranslationUtils.translateData(documenti18n, description);
            }
        } else if (item instanceof CourtDetailValue) {
            String description = ((CourtDetailValue) item).getHearingDescription();
            if (description != null) {
                return TranslationUtils.translateData(documenti18n, description);
            }
        } else if (item instanceof CourtListValue) {
            String description = ((CourtListValue) item).getHearingDescription();
            if (description != null) {
                return TranslationUtils.translateData(documenti18n, description);
            }
        }
        return null;
    }

    public static String getJudgeName(TranslationBundle documenti18n, Object item) {
        if (item instanceof CourtDetailValue) {
            JudgeName name = ((CourtDetailValue) item).getJudgeName();
            if ((name != null) && (name.getName() != null) && (name.getName().length() > 0)) {
                return TranslationUtils.translateData(documenti18n, name.toString());
            }
        } else if (item instanceof JuryStatusDailyListValue) {
            JudgeName name = ((JuryStatusDailyListValue) item).getJudgeName();
            if ((name != null) && (name.getName() != null) && (name.getName().length() > 0)) {
                return TranslationUtils.translateData(documenti18n, name.toString());
            }
        }
        return "";
    }

    /**
     * getNameWithSurnameFirst.

     * @param item the SummaryByNameValue to check
     * @return get the name with surname first
     */
    public static String getNameWithSurnameFirst(Object item) {
        if (item instanceof SummaryByNameValue) {
            DefendantName name = ((SummaryByNameValue) item).getDefendantName();
            if (name != null) {
                String nameWithSurnameFirst = name.getNameWithSurnameFirst();
                if (nameWithSurnameFirst != null) {
                    return nameWithSurnameFirst;
                }
            }
        }
        return null;
    }

    /**
     * getDefendantName.

     * @param item Object

     * @return the defendant name.
     */
    public static String getDefendantName(Object item) {
        if (item instanceof SummaryByNameValue) {
            DefendantName defendantName = ((SummaryByNameValue) item).getDefendantName();
            if (defendantName != null) {
                return defendantName.toString();
            }
        }
        return null;
    }
}
