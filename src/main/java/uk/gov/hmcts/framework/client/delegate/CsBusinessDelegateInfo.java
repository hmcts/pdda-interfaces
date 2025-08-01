package uk.gov.hmcts.framework.client.delegate;

/**

 * Title: CsBusinessDelegateInfo.


 * Description:


 * Copyright: Copyright (c) 2002


 * Company: EDS

 * @author Framework Team
 * @version 1.0
 */
public class CsBusinessDelegateInfo {
    private final Class<?> delegateClass;

    private final Class<?> homeClass;

    public CsBusinessDelegateInfo(Class<?> delegateClass, Class<?> homeClass) {
        this.delegateClass = delegateClass;
        this.homeClass = homeClass;
    }

    public Class<?> getDelegateClass() {
        return delegateClass;
    }

    public Class<?> getHomeClass() {
        return homeClass;
    }
}
