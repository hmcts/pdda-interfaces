package uk.gov.hmcts.framework.business.vos;

import java.io.Serializable;

public interface CsValueObject extends Serializable {

    Integer getVersion();

    Integer getId();
}
