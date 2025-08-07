package uk.gov.hmcts.pdda.business.services.formatting;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;

public abstract class AbstractListXmlMergeUtils extends AbstractXmlMergeUtils {

    private static final String MERGE_TYPE = "LISTS";

    private static final String DATE_FORMAT_MASK = "yy-MM-dd";

    protected AbstractListXmlMergeUtils(String... rootNodes) throws XPathExpressionException {
        super(rootNodes);
    }

    @Override
    public String getMergeType() {
        return MERGE_TYPE;
    }

    @Override
    public String[] getNodeMatchArray() {
        return new String[] {Tag.COURTHOUSE_CODE.value, Tag.COURTHOUSE_NAME.value};
    }

    @Override
    public String[] getNodePositionArray() {
        return new String[] {Tag.COURTROOM_NUMBER.value, Tag.SITTING_AT.value};
    }

    public Date getListStartDateFromDocument(final Document document) {
        LOG.debug("getListStartDateFromDocument({})", document);
        Date result = null;
        if (document != null) {
            NodeList listHeaderNodes = document.getElementsByTagName(Tag.LIST_HEADER.value);
            if (listHeaderNodes != null && listHeaderNodes.getLength() > 0) {
                String[] args = {Tag.START_DATE.value};
                List<String> nodes = Arrays.asList(args);
                Map<String, String> nodeMap = MergeNodeUtils.getNodeMapValues(nodes, listHeaderNodes.item(0));
                String dateAsString = nodeMap.get(Tag.START_DATE.value);
                result = parseDate(dateAsString);
            }
        }
        return result;
    }

    public Date getListEndDateFromDocument(final Document document) {
        LOG.debug("getListEndDateFromDocument({})", document);
        Date result = null;
        if (document != null) {
            NodeList listHeaderNodes = document.getElementsByTagName(Tag.LIST_HEADER.value);
            if (listHeaderNodes != null && listHeaderNodes.getLength() > 0) {
                String[] args = {Tag.END_DATE.value};
                List<String> nodes = Arrays.asList(args);
                Map<String, String> nodeMap = MergeNodeUtils.getNodeMapValues(nodes, listHeaderNodes.item(0));
                String dateAsString = nodeMap.get(Tag.END_DATE.value);
                result = parseDate(dateAsString);
            }
        }
        return result;
    }

    public String getCourtHouseCodeFromDocument(final Document document) {
        LOG.debug("getCourtHouseCodeFromDocument({})", document);
        String result = null;
        if (document != null) {
            NodeList listHeaderNodes = document.getElementsByTagName(Tag.CROWN_COURT.value);
            if (listHeaderNodes != null && listHeaderNodes.getLength() > 0) {
                String[] args = {Tag.COURTHOUSE_CODE.value};
                List<String> nodes = Arrays.asList(args);
                Map<String, String> nodeMap = MergeNodeUtils.getNodeMapValues(nodes, listHeaderNodes.item(0));
                result = nodeMap.get(Tag.COURTHOUSE_CODE.value);

            }
        }
        return result;
    }


    private Date parseDate(String dateAsString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MASK, Locale.getDefault());
            return dateFormat.parse(dateAsString);
        } catch (ParseException e) {
            return null;
        }
    }
}
