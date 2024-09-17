package uk.gov.hmcts.pdda.web.publicdisplay.rendering.compiled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.vos.translation.TranslationBundle;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.AllCaseStatusValue;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.DefendantName;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.BranchEventXmlNode;
import uk.gov.hmcts.pdda.common.publicdisplay.renderdata.nodes.LeafEventXmlNode;
import uk.gov.hmcts.pdda.web.publicdisplay.types.document.DisplayDocument;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class AllCaseStatusCompiledRendererDelegate extends DisplayDocumentCompiledRendererDelegate {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG =
        LoggerFactory.getLogger(AllCaseStatusCompiledRendererDelegate.class);

    private static final String E30200_CASE_TO_BE_LISTED_IN_WEEK_COMMENCING =
        "E30200_Case_to_be_listed_in_week_commencing";
    private static final String E30200_CASE_TO_BE_LISTED_ON = "E30200_Case_to_be_listed_on";
    private static final String E30200_CASE_TO_BE_LISTED_ON_DATE_TO_BE_FIXED =
        "E30200_Case_to_be_listed_on_date_to_be_fixed";
    private static final String E30200_CASE_TO_BE_LISTED_FOR_SENTENCE =
        "E30200_Case_to_be_listed_for_Sentence";
    private static final String E30200_CASE_TO_BE_LISTED_FOR_FURTHER_MENTION_PAD =
        "E30200_Case_to_be_listed_for_Further_Mention/PAD";
    private static final String E30200_CASE_TO_BE_LISTED_FOR_TRIAL =
        "E30200_Case_to_be_listed_for_trial";
    private static final String TRUE = "true";

    /**
     * Append the html for the display document.
     * 
     * @param buffer the buffer to append to
     * @param displayDocument the document containing the information to render
     */
    @Override
    protected void appendDisplayDocumentHtml(StringBuilder buffer, DisplayDocument displayDocument,
        TranslationBundle documentI18n, Date date) {
        LOG.debug("appendDisplayDocumentHtml()");

        String heading = TranslationUtils.translate(documentI18n, "All_Case_Status");
        AppendFieldsUtils.appendHtmlHeader(buffer, displayDocument, documentI18n, date, "banner-dl",
            heading);

        if (RendererUtils.isEmpty(displayDocument)) {
            AppendAttributeFieldsUtils.appendNoInformation(buffer, documentI18n);
        } else {
            AppendFieldsUtils.appendTableHeaders(buffer);
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Court", "15");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Case_No", "10");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Name", "35");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Type", "17");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Not_Before", "5");
            AppendFieldsUtils.appendTableData(buffer, documentI18n, "Status", "18");
            AppendFieldsUtils.appendTableHeadersEnd(buffer);

            String rowType = DocumentUtils.getRowType(null);
            boolean restrictionsApplyToThisList = false;

            Collection<?> table = AttributesUtils.getTable(displayDocument);

            Map<String, Boolean> allDefendantsHidden = checkForCasesWithAllDefendantsHidden(table);

            Set<String> caseNumbersProcessed = new TreeSet<>();

            for (Object item : table) {

                if (isSkipThisRow(item, allDefendantsHidden, caseNumbersProcessed)) {
                    continue;
                }

                AppendUtils.append(buffer, "<tr class=\"");
                AppendUtils.append(buffer, rowType);
                AppendUtils.appendln(buffer, "\">");

                appendCourtRoomNameOrUnassignedAndMovement(buffer, documentI18n, item);
                AppendAttributeFieldsUtils.appendCaseNumberNoRestrictions(buffer, item);
                appendDefendantNameOrTitleRestrictedWidth(buffer, item);

                AppendUtils.append(buffer, "<td class=\"hearing-description\">");
                AppendUtils.appendSpace(buffer,
                    AttributesUtils.getHearingDescription(documentI18n, item));
                AppendUtils.appendln(buffer, TABLEDATAEND);

                AppendAttributeFieldsUtils.appendNotBeforeTime(buffer,
                    DateUtils.getNotBeforeTimeAsString(item));
                appendCaseOrHearingStatus(buffer, documentI18n, item);

                AppendUtils.appendln(buffer, TABLEROWEND);

                // Update loop variables
                rowType = DocumentUtils.getRowType(rowType);
                if (RendererUtils.isReportingRestricted(item)) {
                    restrictionsApplyToThisList = true;
                }
            }

            AppendUtils.appendln(buffer, TABLEBODYEND);
            AppendUtils.appendln(buffer, TABLEEND);
            AppendUtils.appendln(buffer, DIVEND);

            AppendFieldsUtils.appendShowRestrictions(buffer, documentI18n,
                restrictionsApplyToThisList);
        }

        AppendUtils.appendFooter(buffer);
    }

    private boolean isSkipThisRow(Object item, Map<String, Boolean> allDefendantsHidden,
        Set<String> caseNumbersProcessed) {

        String caseNumber = AttributesUtils.getCaseNumber(item);
        boolean newCaseNumber = !caseNumbersProcessed.contains(caseNumber);
        if (newCaseNumber) {
            caseNumbersProcessed.add(caseNumber);
        }
        if (allDefendantsHidden.containsKey(caseNumber)) {
            LOG.debug("Not all defendants are hidden on case {}", caseNumber);
        } else {
            return false;
        }
        if (newCaseNumber && allDefendantsHidden.get(caseNumber)) {
            // If all the defendants on the case are hidden then we still
            // want to show one row for the case, so dont skip this item.
            // If the case itself is hidden then no rows for the case
            // will be returned from the database so we wont get here.
            return false;
        } else if (RendererUtils.isHideInPublicDisplay(item)) {
            // otherwise skip this row if this defendant is hidden.
            return true;
        }
        return false;
    }

    /**
     * If all the defendants on a case are hidden from the public display then we we should present
     * a single row on the display for that case, and that row should have an empty defendant
     * column. To do this we need to know which cases have every defendant hidden.
     * 
     * @param table Collection
     * @return Return a map of cases where the case number maps to a boolean value. TRUE if every
     *         defendant is hidden, FALSE otherwise.
     */
    private Map<String, Boolean> checkForCasesWithAllDefendantsHidden(Collection<?> table) {
        HashMap<String, Boolean> cases = new HashMap<>();

        for (Object item : table) {
            String caseNumber = AttributesUtils.getCaseNumber(item);
            boolean hideThisDefendantInPublicDisplay = RendererUtils.isHideInPublicDisplay(item);

            if (cases.containsKey(caseNumber)) {
                if (cases.get(caseNumber) && !hideThisDefendantInPublicDisplay) {
                    cases.put(caseNumber, Boolean.FALSE);
                }
            } else {
                cases.put(caseNumber, hideThisDefendantInPublicDisplay);
            }
        }

        return cases;
    }

    private void appendCourtRoomNameOrUnassignedAndMovement(StringBuilder buffer,
        TranslationBundle documentI18n, Object item) {
        if (RendererUtils.isFloating(item)) {
            AppendUtils.append(buffer, "<td class=\"court-room-name\">");
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Unassigned"));
            AppendUtils.appendln(buffer, TABLEDATAEND);
        } else {
            AppendUtils.append(buffer, "<td class=\"court-room-name\">");
            AppendUtils.appendln(buffer,
                "<div style=\"word-wrap:break-word;overflow:auto\" class=\"court_room_name-restricted-size120 \">");
            AppendUtils.appendSpace(buffer,
                RendererCourtSiteUtils.getCourtSiteRoomName(item, documentI18n));
            String movedFromCourtSiteRoomName =
                RendererCourtSiteUtils.getMovedFromCourtSiteRoomName(item, documentI18n);
            if (movedFromCourtSiteRoomName != null) {
                AppendUtils.append(buffer, "<div class=\"moved-highlight\">");
                AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Moved_from"));
                AppendUtils.append(buffer, " ");
                AppendUtils.append(buffer, movedFromCourtSiteRoomName);
                AppendUtils.append(buffer, DIVEND);
            }
            AppendUtils.appendln(buffer, DIVEND);
            AppendUtils.appendln(buffer, TABLEDATAEND);
        }
    }

    private void appendDefendantNameOrTitleRestrictedWidth(StringBuilder buffer, Object item) {
        // Note the global velocity variable restrictionsApplyToThisList is not
        // set here, This functionality is handled by the calling method.
        AppendUtils.append(buffer, "<td class=\"defendant-name\">");
        AppendUtils.append(buffer, "<div class=\"defendant-name-restricted-size350\">");
        if (RendererUtils.isHideInPublicDisplay(item)) {
            AppendUtils.appendSpace(buffer, "");
        } else if (hasDefendant(item)) {
            if (RendererUtils.isReportingRestricted(item)) {
                AppendUtils.appendAsterix(buffer, AttributesUtils.getDefendantName(item));
            } else {
                AppendUtils.appendSpace(buffer, AttributesUtils.getDefendantName(item));
            }
        } else {
            if (RendererUtils.isReportingRestricted(item)) {
                AppendUtils.appendAsterix(buffer, AttributesUtils.getCaseTitle(item));
            } else {
                AppendUtils.appendSpace(buffer, AttributesUtils.getCaseTitle(item));
            }
        }
        AppendUtils.appendln(buffer, DIVEND + TABLEDATAEND);
    }

    private void appendCaseOrHearingStatus(StringBuilder buffer, TranslationBundle documentI18n,
        Object item) {
        if (RendererUtils.hasEvent(item)) {
            AppendUtils.append(buffer, "<td class=\"live-status\">");
            AppendUtils.append(buffer, "<div class=\"liveStatusRestrictedWidth\">");
            appendEvent(buffer, item, documentI18n);
            AppendUtils.append(buffer, " ");
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "at", "time"));
            AppendUtils.append(buffer, " ");
            AppendUtils.append(buffer, DateUtils.getEventTimeAsString(item),
                "${item.eventTimeAsString}");
            AppendUtils.append(buffer, DIVEND);
            AppendUtils.appendln(buffer, TABLEDATAEND);
        } else {
            AppendAttributeFieldsUtils.appendHearingProgress(buffer, documentI18n,
                AttributesUtils.getHearingProgress(item));
        }
    }

    /**
     * Overrides method in DisplayDocumentCompiledRendererDelegate to not display defendant name.
     */
    protected void appendEvent30200(StringBuilder buffer, BranchEventXmlNode node,
        TranslationBundle documentI18n, Collection<DefendantName> nameCollection) {
        BranchEventXmlNode laoOptions =
            (BranchEventXmlNode) node.get("E30200_Long_Adjourn_Options");
        String laoType = ((LeafEventXmlNode) laoOptions.get("E30200_LAO_Type")).getValue();

        // Add LAO Type text
        if (E30200_CASE_TO_BE_LISTED_IN_WEEK_COMMENCING.equals(laoType)) {
            AppendEventsThirtyTwoHundredLaoUtils.appendE30200LaoDate(buffer, documentI18n,
                laoOptions, "Case_to_be_listed_in_week_commencing");
        } else if (E30200_CASE_TO_BE_LISTED_ON.equals(laoType)) {
            AppendEventsThirtyTwoHundredLaoUtils.appendE30200LaoDate(buffer, documentI18n,
                laoOptions, "Case_to_be_listed_on");
        } else if (E30200_CASE_TO_BE_LISTED_ON_DATE_TO_BE_FIXED.equals(laoType)) {
            AppendUtils.append(buffer,
                TranslationUtils.translate(documentI18n, "Case_to_be_listed_on_date_to_be_fixed"));
        } else if (E30200_CASE_TO_BE_LISTED_FOR_SENTENCE.equals(laoType)) {
            AppendEventsThirtyTwoHundredLaoUtils.appendE30200LaoDate(buffer, documentI18n,
                laoOptions, "Case_to_be_listed_for_Sentence_on");
        } else if (E30200_CASE_TO_BE_LISTED_FOR_FURTHER_MENTION_PAD.equals(laoType)) {
            AppendEventsThirtyTwoHundredLaoUtils.appendE30200LaoDate(buffer, documentI18n,
                laoOptions, "Case_to_be_listed_for_Further_Mention/PAD_on");
        } else if (E30200_CASE_TO_BE_LISTED_FOR_TRIAL.equals(laoType)) {
            AppendEventsThirtyTwoHundredLaoUtils.appendE30200LaoDate(buffer, documentI18n,
                laoOptions, "Case_to_be_listed_for_Trial_on");
        } else {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Adjourned"));
            AppendUtils.append(buffer,
                "getEvent30300(node, TranslationBundle documentI18n) not created yet");
        }

        // Separate listing text from below
        AppendUtils.append(buffer, SEMI_COLON);
        AppendUtils.append(buffer, SPACE);

        appendAdditional30200(buffer, documentI18n, laoOptions);
    }

    private void appendAdditional30200(StringBuilder buffer, TranslationBundle documentI18n,
        BranchEventXmlNode laoOptions) {
        // PSR Required
        if (isPsrRequired(laoOptions)) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "PSR_Required"));
            AppendUtils.append(buffer, SEMI_COLON);
            AppendUtils.append(buffer, SPACE);
        }

        // Reserved?
        if (isNotReserved(laoOptions)) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Not_Reserved"));
            AppendUtils.append(buffer, SEMI_COLON);
            AppendUtils.append(buffer, SPACE);
        } else if (isReservedWithJudge(laoOptions)) {
            AppendUtils.append(buffer, TranslationUtils.translate(documentI18n, "Reserved_to"));
            AppendUtils.append(buffer, SPACE);
            AppendUtils.append(buffer, TranslationUtils.translateData(documentI18n,
                ((LeafEventXmlNode) laoOptions.get(E30200_LAO_JUDGE_NAME)).getValue()));
            AppendUtils.append(buffer, SEMI_COLON);
            AppendUtils.append(buffer, SPACE);
        }
    }

    private boolean isNotReserved(BranchEventXmlNode laoOptions) {
        return laoOptions.get("E30200_LAO_Not_Reserved") != null && TRUE.equalsIgnoreCase(
            ((LeafEventXmlNode) laoOptions.get("E30200_LAO_Not_Reserved")).getValue());
    }

    private boolean isReservedWithJudge(BranchEventXmlNode laoOptions) {
        return laoOptions.get(E30200_LAO_JUDGE_NAME) != null
            && !"".equals(((LeafEventXmlNode) laoOptions.get(E30200_LAO_JUDGE_NAME)).getValue());
    }

    private boolean isPsrRequired(BranchEventXmlNode laoOptions) {
        return laoOptions.get("E30200_LAO_PSR_Required") != null && TRUE.equalsIgnoreCase(
            ((LeafEventXmlNode) laoOptions.get("E30200_LAO_PSR_Required")).getValue());
    }

    protected boolean hasDefendant(Object item) {
        return item instanceof AllCaseStatusValue allCaseStatusValue
            && allCaseStatusValue.hasDefendant();
    }
}
