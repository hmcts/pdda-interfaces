package uk.gov.hmcts.pdda.business.services.pdda;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Title: CourtelHelper.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2024
 * </p>
 * <p>
 * Company: CGI
 * </p>
 * 
 * @author Luke Gittins
 * @version 1.0
 */
public class CourtelHelper {
    
    public boolean isCourtelSendableDocument(String documentType) {
        List<String> validList = Arrays.asList("DL", "DLP", "FL", "WL");
        return validList.contains(documentType);
    }
}
