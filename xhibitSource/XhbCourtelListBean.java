package uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list;

/**
 * @author Refactoring Team
 *
	* -------- custom finders --------
 *
 * @ejb.finder
 *    signature="java.util.Collection findCourtelListToSend(java.lang.Integer numOfRetries, java.lang.Integer lookupDelay, java.util.Date currentDate)"
 *    result-type-mapping="Local"
 *    method-intf="LocalHome"
 *    query="SELECT OBJECT(c) FROM XhbCourtelList c WHERE c.sentToCourtel = 'N' and c.numSendAttempts < ?1 and (c.lastAttemptDatetime is null or(c.lastAttemptDatetime + (?2/86400) < ?3))"
 *
 * @ejb.finder
 *    signature="java.util.Collection findCountOfXmlId(java.lang.Integer xmlDocumentId)"
 *    result-type-mapping="Local"
 *    method-intf="LocalHome"
 *    query="SELECT OBJECT(c) FROM XhbCourtelList c WHERE c.xmlDocumentId = ?1"
 *
 * @ejb.finder
 *    signature="java.util.Collection findCountOfXmlDocClobId(java.lang.Long xmlDocClobId)"
 *    result-type-mapping="Local"
 *    method-intf="LocalHome"
 *    query="SELECT OBJECT(c) FROM XhbCourtelList c WHERE c.xmlDocumentClobId = ?1"
 * -------- end of custom finders -------- *
 * @ejb.bean
 *    type="CMP"
 *    cmp-version="2.x"
 *    name="XhbCourtelList"
 *    local-jndi-name="Xhibit/XhbCourtelListHome"
 *    view-type="local"
 *    primkey-field="courtelListId"
 *
 * @ejb.data-object
 *    container="false"
 *    setdata="false"
 *    generate="true"
 *    name="XhbCourtelListBasicValue"
 *
 * @ejb.finder
 *    signature="java.util.Collection findAll()"
 *    result-type-mapping="Local"
 *    method-intf="LocalHome"
 *    query="SELECT OBJECT(o) FROM XhbCourtelList o"
 *
 * @ejb.persistence table-name="XHB_COURTEL_LIST"
 *
 * @weblogic.automatic-key-generation
 *    generator-type="ORACLE"
 *    generator-name="XHB_COURTEL_LIST_SEQ"
 *    key-cache-size="1"
 *
 *
 * @weblogic.data-source-name XhibitOracleTxDataSource
 */
public abstract class XhbCourtelListBean implements javax.ejb.EntityBean {

  protected javax.ejb.EntityContext ctx;

  public void setEntityContext(javax.ejb.EntityContext ctx) { this.ctx = ctx; }
  public void unsetEntityContext() { this.ctx = null; }
  public void ejbActivate() {}
  public void ejbPassivate() {}
  public void ejbLoad() {}
  public void ejbStore() {}
  public void ejbRemove() throws javax.ejb.RemoveException {}

  /**
   * Returns the courtelListId
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the courtelListId
   *
   * @ejb.pk-field
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="COURTEL_LIST_ID"
   */
  public abstract java.lang.Integer getCourtelListId();

  /**
   * Sets the courtelListId
   *
   * @param java.lang.Integer the new courtelListId value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setCourtelListId(java.lang.Integer courtelListId);

  /**
   * Returns the xmlDocumentId
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the xmlDocumentId
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="XML_DOCUMENT_ID"
   */
  public abstract java.lang.Integer getXmlDocumentId();

  /**
   * Sets the xmlDocumentId
   *
   * @param java.lang.Integer the new xmlDocumentId value
   */
  public abstract void setXmlDocumentId(java.lang.Integer xmlDocumentId);

  /**
   * Returns the blobId
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the blobId
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="BLOB_ID"
   */
  public abstract java.lang.Long getBlobId();

  /**
   * Sets the blobId
   *
   * @param java.lang.Long the new blobId value
   */
  public abstract void setBlobId(java.lang.Long blobId);

  /**
   * Returns the filename
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the filename
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="FILENAME"
   */
  public abstract java.lang.String getFilename();

  /**
   * Sets the filename
   *
   * @param java.lang.String the new filename value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setFilename(java.lang.String filename);

  /**
   * Returns the sentToCourtel
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the sentToCourtel
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="SENT_TO_COURTEL"
   */
  public abstract java.lang.String getSentToCourtel();

  /**
   * Sets the sentToCourtel
   *
   * @param java.lang.String the new sentToCourtel value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setSentToCourtel(java.lang.String sentToCourtel);

  /**
   * Returns the numServersUploadedTo
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the numServersUploadedTo
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="NUM_SERVERS_UPLOADED_TO"
   */
  public abstract java.lang.Integer getNumServersUploadedTo();

  /**
   * Sets the numServersUploadedTo
   *
   * @param java.lang.Integer the new numServersUploadedTo value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setNumServersUploadedTo(java.lang.Integer numServersUploadedTo);

  /**
   * Returns the numSendAttempts
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the numSendAttempts
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="NUM_SEND_ATTEMPTS"
   */
  public abstract java.lang.Integer getNumSendAttempts();

  /**
   * Sets the numSendAttempts
   *
   * @param java.lang.Integer the new numSendAttempts value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setNumSendAttempts(java.lang.Integer numSendAttempts);

  /**
   * Returns the lastAttemptDatetime
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the lastAttemptDatetime
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="LAST_ATTEMPT_DATETIME"
   */
  public abstract java.util.Date getLastAttemptDatetime();

  /**
   * Sets the lastAttemptDatetime
   *
   * @param java.util.Date the new lastAttemptDatetime value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setLastAttemptDatetime(java.util.Date lastAttemptDatetime);

  /**
   * Returns the courtelResponseServer1
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the courtelResponseServer1
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="COURTEL_RESPONSE_SERVER_1"
   */
  public abstract java.lang.String getCourtelResponseServer1();

  /**
   * Sets the courtelResponseServer1
   *
   * @param java.lang.String the new courtelResponseServer1 value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setCourtelResponseServer1(java.lang.String courtelResponseServer1);

  /**
   * Returns the courtelResponseServer2
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the courtelResponseServer2
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="COURTEL_RESPONSE_SERVER_2"
   */
  public abstract java.lang.String getCourtelResponseServer2();

  /**
   * Sets the courtelResponseServer2
   *
   * @param java.lang.String the new courtelResponseServer2 value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setCourtelResponseServer2(java.lang.String courtelResponseServer2);

  /**
   * Returns the messageText
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the messageText
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="MESSAGE_TEXT"
   */
  public abstract java.lang.String getMessageText();

  /**
   * Sets the messageText
   *
   * @param java.lang.String the new messageText value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setMessageText(java.lang.String messageText);

  /**
   * Returns the lastUpdateDate
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the lastUpdateDate
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="LAST_UPDATE_DATE"
   */
  public abstract java.util.Date getLastUpdateDate();

  /**
   * Sets the lastUpdateDate
   *
   * @param java.util.Date the new lastUpdateDate value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setLastUpdateDate(java.util.Date lastUpdateDate);

  /**
   * Returns the creationDate
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the creationDate
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="CREATION_DATE"
   */
  public abstract java.util.Date getCreationDate();

  /**
   * Sets the creationDate
   *
   * @param java.util.Date the new creationDate value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setCreationDate(java.util.Date creationDate);

  /**
   * Returns the createdBy
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the createdBy
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="CREATED_BY"
   */
  public abstract java.lang.String getCreatedBy();

  /**
   * Sets the createdBy
   *
   * @param java.lang.String the new createdBy value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setCreatedBy(java.lang.String createdBy);

  /**
   * Returns the lastUpdatedBy
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the lastUpdatedBy
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="LAST_UPDATED_BY"
   */
  public abstract java.lang.String getLastUpdatedBy();

  /**
   * Sets the lastUpdatedBy
   *
   * @param java.lang.String the new lastUpdatedBy value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setLastUpdatedBy(java.lang.String lastUpdatedBy);

  /**
   * Returns the version
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the version
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="VERSION"
   */
  public abstract java.lang.Integer getVersion();

  /**
   * Sets the version
   *
   * @param java.lang.Integer the new version value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setVersion(java.lang.Integer version);

  /**
   * Returns the xmlDocumentClobId
   * @todo support OracleClob,OracleBlob on WLS
   *
   * @return the xmlDocumentClobId
   *
   * @ejb.interface-method view-type="local"
   * @ejb.persistent-field
   * @ejb.persistence column-name="XML_DOCUMENT_CLOB_ID"
   */
  public abstract java.lang.Long getXmlDocumentClobId();

  /**
   * Sets the xmlDocumentClobId
   *
   * @param java.lang.Long the new xmlDocumentClobId value
   * @ejb.interface-method view-type="local"
   */
  public abstract void setXmlDocumentClobId(java.lang.Long xmlDocumentClobId);


  /**
   * This is a bi-directional one-to-many relationship CMR method
   *
   * @return the related uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlob.
   *
   * @ejb.interface-method view-type="local"
   *
   * @ejb.relation
   *    name="XHB_BLOB-cmp20-XHB_COURTEL_LIST-cmp20"
   *    role-name="XHB_COURTEL_LIST-cmp20-has-XHB_BLOB-cmp20"
   *
   * @jboss.relation-mapping style="foreign-key"
   *
   * @weblogic.column-map
   *    foreign-key-column="BLOB_ID"
   *    key-column="BLOB_ID"
   *
   * @jboss.relation
   *    fk-constraint="true"
   *    fk-column="BLOB_ID"
   *    related-pk-field="blobId"
   *
   */
  public abstract uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlob getXhbBlob();


  /**
   * Returns xhbBlob value object
   * @return uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlobBasicValue
   * @ejb.interface-method view-type="local"
   */
  public uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlobBasicValue getXhbBlobData() {
    if(getXhbBlob() == null) return null;
    return getXhbBlob().getData();
  }

  /**
   * Sets the related uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlob
   *
   * @param uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelList the related $target.variableName
   *
   * @ejb.interface-method view-type="local"
   *
   * @param xhbBlob the new CMR value
   */
  public abstract void setXhbBlob(uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlob xhbBlob);

  /**
   * This is a bi-directional one-to-many relationship CMR method
   *
   * @return the related uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocument.
   *
   * @ejb.interface-method view-type="local"
   *
   * @ejb.relation
   *    name="XHB_XML_DOCUMENT-cmp20-XHB_COURTEL_LIST-cmp20"
   *    role-name="XHB_COURTEL_LIST-cmp20-has-XHB_XML_DOCUMENT-cmp20"
   *
   * @jboss.relation-mapping style="foreign-key"
   *
   * @weblogic.column-map
   *    foreign-key-column="XML_DOCUMENT_ID"
   *    key-column="XML_DOCUMENT_ID"
   *
   * @jboss.relation
   *    fk-constraint="true"
   *    fk-column="XML_DOCUMENT_ID"
   *    related-pk-field="xmlDocumentId"
   *
   */
  public abstract uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocument getXhbXmlDocument();


  /**
   * Returns xhbXmlDocument value object
   * @return uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocumentBasicValue
   * @ejb.interface-method view-type="local"
   */
  public uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocumentBasicValue getXhbXmlDocumentData() {
    if(getXhbXmlDocument() == null) return null;
    return getXhbXmlDocument().getData();
  }

  /**
   * Sets the related uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocument
   *
   * @param uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelList the related $target.variableName
   *
   * @ejb.interface-method view-type="local"
   *
   * @param xhbXmlDocument the new CMR value
   */
  public abstract void setXhbXmlDocument(uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocument xhbXmlDocument);

   /**
    *
    * @param uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue Data object
    * @param xhbBlob CMR field
    * @param xhbXmlDocument CMR field
    * @return the primary key of the new instance
    *
    * @ejb.create-method
    * @deprecated Use the version that takes just the value object. CMRs are now set transparently.
    */
   public java.lang.Integer ejbCreate( uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue data, uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlob xhbBlob, uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocument xhbXmlDocument ) throws javax.ejb.CreateException {
    initialiseAuditInfo();
    data.setVersion( new Integer(1) );
    // initialiseVersion();
    setCmp(data);
    return null;
   }

   /**
    *
    * @param uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue Data object
    * @param xhbBlob CMR field
    * @param xhbXmlDocument CMR field
    */
   public void ejbPostCreate( uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue data, uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlob xhbBlob, uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocument xhbXmlDocument ) throws javax.ejb.CreateException {
      // Set CMR fields
      setXhbBlob(xhbBlob);
      setXhbXmlDocument(xhbXmlDocument);
   }
   /**
    *
    * @param uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue Data object
    * @return the primary key of the new instance
    *
    * @ejb.create-method
    */
   public java.lang.Integer ejbCreate( uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue data) throws javax.ejb.CreateException {
    initialiseAuditInfo();
    data.setVersion( new Integer(1) );
    setCmp(data);
    return null;
   }

   /**
    *
    * @param uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue Data object
    */
   public void ejbPostCreate( uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue data) throws javax.ejb.CreateException {
      setCmr(data);
   }

   private boolean setCmr(uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue data)
   {
      boolean modified = false;
        java.lang.Long blobIdBean = getBlobId();
        java.lang.Long blobIdData = data.getBlobId();
        if (blobIdData != null)
        {
           if (!blobIdData.equals(blobIdBean))
           {
                setXhbBlob(uk.gov.courtservice.xhibit.business.entities.xhb_blob.XhbBlobBeanHelper2.findByPrimaryKey(blobIdData));
                modified = true;
           }
        }
        else
        {
           if (blobIdBean != null)
           {
                setXhbBlob(null);
                modified = true;
           }
        }
        java.lang.Integer xmlDocumentIdBean = getXmlDocumentId();
        java.lang.Integer xmlDocumentIdData = data.getXmlDocumentId();
        if (xmlDocumentIdData != null)
        {
           if (!xmlDocumentIdData.equals(xmlDocumentIdBean))
           {
                setXhbXmlDocument(uk.gov.courtservice.xhibit.business.entities.xhb_xml_document.XhbXmlDocumentBeanHelper2.findByPrimaryKey(xmlDocumentIdData));
                modified = true;
           }
        }
        else
        {
           if (xmlDocumentIdBean != null)
           {
                setXhbXmlDocument(null);
                modified = true;
           }
        }
      return modified;
   }


  protected void initialiseAuditInfo() {
    setCreatedBy( ctx.getCallerPrincipal().getName() );
        setCreationDate(new java.sql.Timestamp(System.currentTimeMillis()));
        updateAuditInfo();
  }


  protected void initialiseVersion() {
    setVersion( new Integer(1) );
  }

  protected void updateAuditInfo() {
        setLastUpdatedBy( ctx.getCallerPrincipal().getName() );
        setLastUpdateDate(new java.sql.Timestamp(System.currentTimeMillis()));
  }



   // No c:\XHIBIT\XHIBIT_8_2\datatier\entity\config/cmp20-xhb_courtel_list-class-code.txt found.

  /**
   * Sets the data
   *
   * @param uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue
   * @ejb.interface-method view-type="local"
   */
  public void setData(XhbCourtelListBasicValue holder) {
    boolean modified = setCmp(holder);
    boolean cmrModified = setCmr(holder);
    if(modified || cmrModified) updateAuditInfo();
  }

  private boolean setCmp(XhbCourtelListBasicValue holder) {
     boolean modified = false;
         if((holder.getFilename() != null && !holder.getFilename().equals(getFilename())) ||
         (holder.getFilename() == null && getFilename() != null)) {
             setFilename(holder.getFilename());
             modified = true;
         }
         if((holder.getSentToCourtel() != null && !holder.getSentToCourtel().equals(getSentToCourtel())) ||
         (holder.getSentToCourtel() == null && getSentToCourtel() != null)) {
             setSentToCourtel(holder.getSentToCourtel());
             modified = true;
         }
         if((holder.getNumServersUploadedTo() != null && !holder.getNumServersUploadedTo().equals(getNumServersUploadedTo())) ||
         (holder.getNumServersUploadedTo() == null && getNumServersUploadedTo() != null)) {
             setNumServersUploadedTo(holder.getNumServersUploadedTo());
             modified = true;
         }
         if((holder.getNumSendAttempts() != null && !holder.getNumSendAttempts().equals(getNumSendAttempts())) ||
         (holder.getNumSendAttempts() == null && getNumSendAttempts() != null)) {
             setNumSendAttempts(holder.getNumSendAttempts());
             modified = true;
         }
         if((holder.getLastAttemptDatetime() != null && !holder.getLastAttemptDatetime().equals(getLastAttemptDatetime())) ||
         (holder.getLastAttemptDatetime() == null && getLastAttemptDatetime() != null)) {
             setLastAttemptDatetime(holder.getLastAttemptDatetime());
             modified = true;
         }
         if((holder.getCourtelResponseServer1() != null && !holder.getCourtelResponseServer1().equals(getCourtelResponseServer1())) ||
         (holder.getCourtelResponseServer1() == null && getCourtelResponseServer1() != null)) {
             setCourtelResponseServer1(holder.getCourtelResponseServer1());
             modified = true;
         }
         if((holder.getCourtelResponseServer2() != null && !holder.getCourtelResponseServer2().equals(getCourtelResponseServer2())) ||
         (holder.getCourtelResponseServer2() == null && getCourtelResponseServer2() != null)) {
             setCourtelResponseServer2(holder.getCourtelResponseServer2());
             modified = true;
         }
         if((holder.getMessageText() != null && !holder.getMessageText().equals(getMessageText())) ||
         (holder.getMessageText() == null && getMessageText() != null)) {
             setMessageText(holder.getMessageText());
             modified = true;
         }
         if((holder.getLastUpdateDate() != null && !holder.getLastUpdateDate().equals(getLastUpdateDate())) ||
         (holder.getLastUpdateDate() == null && getLastUpdateDate() != null)) {
             setLastUpdateDate(holder.getLastUpdateDate());
             modified = true;
         }
         if((holder.getCreationDate() != null && !holder.getCreationDate().equals(getCreationDate())) ||
         (holder.getCreationDate() == null && getCreationDate() != null)) {
             setCreationDate(holder.getCreationDate());
             modified = true;
         }
         if((holder.getCreatedBy() != null && !holder.getCreatedBy().equals(getCreatedBy())) ||
         (holder.getCreatedBy() == null && getCreatedBy() != null)) {
             setCreatedBy(holder.getCreatedBy());
             modified = true;
         }
         if((holder.getLastUpdatedBy() != null && !holder.getLastUpdatedBy().equals(getLastUpdatedBy())) ||
         (holder.getLastUpdatedBy() == null && getLastUpdatedBy() != null)) {
             setLastUpdatedBy(holder.getLastUpdatedBy());
             modified = true;
         }
         if((holder.getVersion() != null && !holder.getVersion().equals(getVersion())) ||
         (holder.getVersion() == null && getVersion() != null)) {
             setVersion(holder.getVersion());
             modified = true;
         }
         if((holder.getXmlDocumentClobId() != null && !holder.getXmlDocumentClobId().equals(getXmlDocumentClobId())) ||
         (holder.getXmlDocumentClobId() == null && getXmlDocumentClobId() != null)) {
             setXmlDocumentClobId(holder.getXmlDocumentClobId());
             modified = true;
         }
     return modified;
  }

  /**
   * Gets the data
   *
   * @return uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list.XhbCourtelListBasicValue
   * @ejb.interface-method view-type="local"
   */
  public XhbCourtelListBasicValue getData() {
    XhbCourtelListBasicValue holder = new XhbCourtelListBasicValue();
    holder.setCourtelListId(getCourtelListId());
    holder.setXmlDocumentId(getXmlDocumentId());
    holder.setBlobId(getBlobId());
    holder.setFilename(getFilename());
    holder.setSentToCourtel(getSentToCourtel());
    holder.setNumServersUploadedTo(getNumServersUploadedTo());
    holder.setNumSendAttempts(getNumSendAttempts());
    holder.setLastAttemptDatetime(getLastAttemptDatetime());
    holder.setCourtelResponseServer1(getCourtelResponseServer1());
    holder.setCourtelResponseServer2(getCourtelResponseServer2());
    holder.setMessageText(getMessageText());
    holder.setLastUpdateDate(getLastUpdateDate());
    holder.setCreationDate(getCreationDate());
    holder.setCreatedBy(getCreatedBy());
    holder.setLastUpdatedBy(getLastUpdatedBy());
    holder.setVersion(getVersion());
    holder.setXmlDocumentClobId(getXmlDocumentClobId());
    return holder;
  }
}
