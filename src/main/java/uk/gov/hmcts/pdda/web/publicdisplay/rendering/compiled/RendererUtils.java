
package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import uk.gov.hmcts.pdda.common.publicdisplay.data.Data;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCourtStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.CourtListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.JuryStatusDailyListValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.PublicDisplayValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.SummaryByNameValue;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Collection;

public final class RendererUtils {

    private static final int MAXDEFENDANTS = 16;

    private RendererUtils() {
    }

    /**
     * isEmpty.
     * 
     * @return true if the document contains no data.
     */
    public static boolean isEmpty(DisplayDocument document) {
        if (document != null) {
            Data data = document.getData();
            if (data != null && !data.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasEvent(Object item) {
        return item instanceof PublicDisplayValue publicDisplayValue && publicDisplayValue.getEvent() != null;
    }

    public static boolean hasDefendants(Object item) {
        return item instanceof CourtListValue courtListValue && courtListValue.hasDefendants();
    }

    /**
     * getMaxDefendantsWithoutOverspill.
     * 
     * @return the number of defendants than can be rendered without overspill.
     */
    private static int getMaxDefendantsWithoutOverspill() {
        return MAXDEFENDANTS;
    }

    public static boolean isDefendantNamesShouldOverspill(
        Collection<DefendantName> nameCollection) {
        return displayableDefendants(nameCollection) > getMaxDefendantsWithoutOverspill();
    }

    private static int displayableDefendants(Collection<DefendantName> nameCollection) {
        int displayableDefendants = 0;
        for (DefendantName defendantName : nameCollection) {
            if (!defendantName.isHideInPublicDisplay()) {
                displayableDefendants++;
            }
        }
        return displayableDefendants;
    }

    /**
     * isReportingRestricted.
     * 
     * @param item the SummaryByNameValue to check
     * @return true if reporting is restricted for this item
     */
    public static boolean isReportingRestricted(Object item) {
        if (item instanceof SummaryByNameValue summaryByNameValue) {
            return summaryByNameValue.isReportingRestricted();
        } else if (item instanceof CourtListValue courtListValue) {
            return courtListValue.isReportingRestricted();
        } else if (item instanceof AllCourtStatusValue allCourtStatusValue) {
            return allCourtStatusValue.isReportingRestricted();
        }
        return false;
    }

    /**
     * isFloating.
     * 
     * @param item the SummaryByNameValue to check
     * @return true if this item is floating
     */
    public static boolean isFloating(Object item) {
        if (item instanceof SummaryByNameValue summaryByNameValue) {
            return summaryByNameValue.isFloating();
        } else if (item instanceof JuryStatusDailyListValue juryStatusDailyListValue) {
            return juryStatusDailyListValue.isFloating();
        }
        return false;
    }

    public static boolean isHideInPublicDisplay(Object item) {
        if (item instanceof SummaryByNameValue summaryByNameValue) {
            DefendantName defendantName = summaryByNameValue.getDefendantName();
            if (defendantName != null) {
                return defendantName.isHideInPublicDisplay();
            }
        } else if (item instanceof DefendantName defendantName) {
            return defendantName.isHideInPublicDisplay();
        }

        return false;
    }

    public static boolean isHideInPublicDisplay(Integer defOnCaseId,
        Collection<DefendantName> names) {
        if (names != null && defOnCaseId != null) {
            for (DefendantName name : names) {
                if (defOnCaseId == name.getDefendantOnCaseId()) {
                    return name.isHideInPublicDisplay();
                }
            }
        }
        return false;
    }
}