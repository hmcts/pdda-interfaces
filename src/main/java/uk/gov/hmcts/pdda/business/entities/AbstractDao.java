package uk.gov.hmcts.pdda.business.entities;

public abstract class AbstractDao {

    public abstract Integer getVersion();

    public abstract void setVersion(Integer version);

    public abstract Object getPrimaryKey();

}
