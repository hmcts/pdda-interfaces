package uk.gov.hmcts.pdda.business.services.pdda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropDao;
import uk.gov.hmcts.pdda.business.entities.xhbconfigprop.XhbConfigPropRepository;

import java.util.List;

/**

 * Title: Config Property Maintainer.


 * Description:


 * Copyright: Copyright (c) 2023


 * Company: CGI

 * @author Mark Harris
 * @version 1.0
 */
public class ConfigPropMaintainer {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigPropMaintainer.class);

    private final XhbConfigPropRepository xhbConfigPropRepository;
    
    public ConfigPropMaintainer(XhbConfigPropRepository xhbConfigPropRepository) {
        this.xhbConfigPropRepository = xhbConfigPropRepository;
    }

    public XhbConfigPropDao getConfigPropBasicValue(String propertyName) {
        LOG.debug("getConfigPropBasicValue()");
        List<XhbConfigPropDao> properties =
            xhbConfigPropRepository.findByPropertyNameSafe(propertyName);
        if (properties != null && !properties.isEmpty()) {
            return properties.get(0);
        }
        return null;
    }

    public String getPropertyValue(String propertyName) {
        XhbConfigPropDao basicValue = getConfigPropBasicValue(propertyName);
        if (basicValue != null) {
            return basicValue.getPropertyValue();
        }
        return null;
    }
}
