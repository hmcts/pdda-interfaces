package uk.gov.hmcts.pdda.common.publicdisplay.setup.drilldown;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Title: DrillDown.

 * <p/>
 * <p/>
 * Description:

 * <p/>
 * <p/>
 * Copyright: Copyright (c) 2003

 * <p/>
 * <p/>
 * Company: Electronic Data Systems

 * @author Neil Ellis
 * @version $Revision: 1.3 $
 */
public class DrillDown implements Serializable {

    static final long serialVersionUID = -3260495173823304712L;

    private final List<Object> arrayList;

    private String name;
    
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public DrillDown(String name) {
        arrayList = new ArrayList<>();
        setName(name);
    }

    public List<Object> getValues() {
        return arrayList;
    }

    public boolean add(Object object) {
        return arrayList.add(object);
    }
}
