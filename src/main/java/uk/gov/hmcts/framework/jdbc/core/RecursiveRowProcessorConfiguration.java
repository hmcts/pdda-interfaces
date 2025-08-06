package uk.gov.hmcts.framework.jdbc.core;

import java.util.ArrayList;
import java.util.List;

public class RecursiveRowProcessorConfiguration {

    private String groupByParentFieldName;
    private String childFieldName;
    private Class<?> childClassType;
    private List<RecursiveRowProcessorConfiguration> childRowProcessorConfigurations;

    public RecursiveRowProcessorConfiguration(String groupByParentFieldName, String childFieldName,
        Class<?> childClassType,
        RecursiveRowProcessorConfiguration childRowProcessorConfiguration) {
        this(groupByParentFieldName, childFieldName, childClassType);
        List<RecursiveRowProcessorConfiguration> childGroupingConfigurations = new ArrayList<>();
        childGroupingConfigurations.add(childRowProcessorConfiguration);
        setChildRowProcessorConfiguration(childGroupingConfigurations);
    }

    public RecursiveRowProcessorConfiguration(String groupByParentFieldName, String childFieldName,
        Class<?> childClassType,
        List<RecursiveRowProcessorConfiguration> childRowProcessorConfiguration) {
        this(groupByParentFieldName, childFieldName, childClassType);
        setChildRowProcessorConfiguration(childRowProcessorConfiguration);
    }

    public RecursiveRowProcessorConfiguration(String groupByParentFieldName, String childFieldName,
        Class<?> childClassType) {
        setGroupByFieldName(groupByParentFieldName);
        setChildFieldName(childFieldName);
        setChildClassType(childClassType);
    }

    public String getGroupingFieldName() {
        return groupByParentFieldName;
    }

    private void setGroupByFieldName(String groupingFieldName) {
        this.groupByParentFieldName = groupingFieldName;
    }

    public String getChildFieldName() {
        return childFieldName;
    }

    private void setChildFieldName(String groupedFieldName) {
        this.childFieldName = groupedFieldName;
    }

    public Class<?> getChildClassType() {
        return this.childClassType;
    }

    private void setChildClassType(Class<?> classType) {
        this.childClassType = classType;

    }

    public List<RecursiveRowProcessorConfiguration> getChildRowProcessorConfiguration() {
        return this.childRowProcessorConfigurations;
    }

    private void setChildRowProcessorConfiguration(List<RecursiveRowProcessorConfiguration> gc) {
        this.childRowProcessorConfigurations = gc;

    }
}
