package uk.gov.hmcts.pdda.business.xmlbinding.formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.exception.formatting.FormattingException;
import uk.gov.hmcts.pdda.business.vos.formatting.FormattingValue;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.XsltFileList;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.XsltTransform;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DistributionTypeType;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.DocumentTypeType;
import uk.gov.hmcts.pdda.business.xmlbinding.hmcts.pdda.types.MimeTypeType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: FormattingConfig.
 * </p>
 * <p>
 * Description: Abstraction of the XML config file. This class simplifies the use of the castor
 * generated xmlbinding.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: EDS
 * </p>
 * 
 * @author Will Fardell
 * @version 1.0
 */
@SuppressWarnings({"PMD.ExcessiveParameterList"})
public class FormattingConfig {

    public static final String CONFIG_XML = "/config/xml/xslt_config.xml";

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(FormattingConfig.class);

    private final List<XsltTransform> propertiesArray = new ArrayList<>();
    
    public static class Singleton {
        public static final FormattingConfig INSTANCE = new FormattingConfig(CONFIG_XML);
    }

    /**
     * Construct a FormattingConfig for the given resource.
     * 
     * @param name The name of the resource
     */
    FormattingConfig(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("name: null");
        }
        init(getClass().getResourceAsStream(name));
    }

    /**
     * Load the configuration from the stream.
     * 
     * @param in InputStream
     */
    private void init(final InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("in: null");
        }

        XsltTransform xsltTransform = createXsltTransform();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in));) {
            String line = br.readLine();
            // Read the config.xml
            while (line != null) {
                xsltTransform = processLine(line, xsltTransform);
                line = br.readLine();
            }
            // Add the last one
            addToProperties(xsltTransform);
        } catch (IOException e) {
            throw new FormattingException("An error occured reading formatting config.", e);
        }
    }

    private XsltTransform processLine(String line, XsltTransform xsltTransform) {
        XsltTransform xsltTransformToUse = xsltTransform;
        String value = getValue(line);
        if (line.contains("<xslttransform>")) {
            // Add the previous one and reset the next
            addToProperties(xsltTransformToUse);
            xsltTransformToUse = createXsltTransform();
        }
        if (line.contains("<distributiontype>")) {
            DistributionTypeType distributionType = DistributionTypeType.valueOf(value);
            xsltTransformToUse.setDistributionType(distributionType);
        }
        if (line.contains("<mimetype>")) {
            MimeTypeType mimeType = MimeTypeType.valueOf(value);
            xsltTransformToUse.setMimeType(mimeType);
        }
        if (line.contains("<documenttype>")) {
            DocumentTypeType documentType = DocumentTypeType.valueOf(value);
            xsltTransformToUse.setDocumentType(documentType);
        }
        if (line.contains("<xsltfilename>")) {
            xsltTransformToUse.getXsltFileList().addXsltFileName(value);
        }
        return xsltTransformToUse;
    }

    private String getValue(String line) {
        int startPos = line.indexOf('>');
        int endPos = line.lastIndexOf('<');
        if (startPos > -1 && endPos > -1 && startPos != line.lastIndexOf('>')) {
            return line.substring(startPos + 1, endPos);
        }
        return "";
    }

    private XsltTransform createXsltTransform() {
        XsltTransform xsltTransform = new XsltTransform();
        xsltTransform.setXsltFileList(new XsltFileList());
        return xsltTransform;
    }

    private void addToProperties(XsltTransform xsltTransform) {
        if (xsltTransform != null && xsltTransform.getXsltFileList().getXsltFileName().length > 0) {
            try {
                propertiesArray.add(xsltTransform);
            } catch (Exception e) {
                LOG.error("Error adding config line: {}", e.getMessage());
                throw e;
            }

        }
    }

    /**
     * Looks through the xsl properties looking for the schema that match the criteria specified in
     * value. Note nulls in the properties are treated as wild cards and match eveything.
     * 
     * @param value the value to get the transforms for.
     * @return An array containing the names of the xsl transforms to use.
     */
    public String[] getXslTransforms(final FormattingValue value) {
        String[] xslTransforms = getFilteredXslTransforms(value);
        if (LOG.isDebugEnabled()) {
            if (0 < xslTransforms.length) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(value).append(": [").append(xslTransforms[0]);
                for (int i = 1; i < xslTransforms.length; i++) {
                    buffer.append(',').append(xslTransforms[i]);
                }
                buffer.append(']');
                LOG.debug(buffer.toString());
            } else {
                LOG.debug("{}: []", value);
            }
        }
        return xslTransforms;
    }

    private int getXsltCount() {
        return propertiesArray.size();
    }

    private XsltTransform getXsltTransform(int index) {
        return propertiesArray.get(index);
    }

    // Extracted to simplify debug
    private String[] getFilteredXslTransforms(final FormattingValue value) {
        if (value == null) {
            throw new IllegalArgumentException("value: null");
        }

        List<String> xslTransformList = new ArrayList<>();

        // Get the criteria to check against
        MimeTypeType mimeTypeCriteria = MimeTypeType.valueOf(value.getMimeType());
        DocumentTypeType documentTypeCriteria = DocumentTypeType.valueOf(value.getDocumentType());
        DistributionTypeType distributionTypeCriteria =
            DistributionTypeType.valueOf(value.getDistributionType());
        Integer majorVersionCriteria = value.getMajorVersion();
        Integer minorVersionCriteria = value.getMinorVersion();

        // Check the criteria against the properties
        for (int i = 0; i < getXsltCount(); i++) {
            XsltTransform transform = getXsltTransform(i);

            MimeTypeType mimeType = transform.getMimeType();
            DocumentTypeType documentType = transform.getDocumentType();
            DistributionTypeType distributionType = transform.getDistributionType();
            Integer majorVersion = transform.hasMajorVersion() ? transform.getMajorVersion() : null;
            Integer minorVersion = transform.hasMinorVersion() ? transform.getMinorVersion() : null;


            List<String> newRecords =
                getXslTransformList(transform, mimeType, documentType, distributionType,
                    majorVersion, minorVersion, mimeTypeCriteria, documentTypeCriteria,
                    distributionTypeCriteria, majorVersionCriteria, minorVersionCriteria);
            if (!newRecords.isEmpty()) {
                xslTransformList.addAll(newRecords);
            }
        }

        // Return the transforms in an array
        return xslTransformList.toArray(new String[0]);
    }

    private List<String> getXslTransformList(XsltTransform transform, MimeTypeType mimeType,
        DocumentTypeType documentType, DistributionTypeType distributionType, Integer majorVersion,
        Integer minorVersion, MimeTypeType mimeTypeCriteria, DocumentTypeType documentTypeCriteria,
        DistributionTypeType distributionTypeCriteria, Integer majorVersionCriteria,
        Integer minorVersionCriteria) {
        List<String> xslTransformList = new ArrayList<>();

        // If we match the criteria add all the filenames in the
        // fileList to
        // the transform list
        if (isMatch(mimeType, documentType, distributionType, majorVersion, minorVersion,
            mimeTypeCriteria, documentTypeCriteria, distributionTypeCriteria, 
            majorVersionCriteria, minorVersionCriteria)) {

            XsltFileList fileList = transform.getXsltFileList();
            for (int j = 0; j < fileList.getXsltFileNameCount(); j++) {
                xslTransformList.add(fileList.getXsltFileName(j));
            }
        }
        return xslTransformList;
    }
    
    private static boolean isMatch(MimeTypeType mimeType,
        DocumentTypeType documentType, DistributionTypeType distributionType, Integer majorVersion,
        Integer minorVersion, MimeTypeType mimeTypeCriteria, DocumentTypeType documentTypeCriteria,
        DistributionTypeType distributionTypeCriteria, Integer majorVersionCriteria,
        Integer minorVersionCriteria) {
        return (mimeType == null || mimeType.equals(mimeTypeCriteria))
            && (documentType == null || documentType.equals(documentTypeCriteria))
            && (distributionType == null || distributionType.equals(distributionTypeCriteria))
            && (majorVersion == null || majorVersion.equals(majorVersionCriteria))
            && (minorVersion == null || minorVersion.equals(minorVersionCriteria));
    }
}
