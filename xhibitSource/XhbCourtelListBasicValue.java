/*
 * Generated by XDoclet - Do not edit!
 */
package uk.gov.courtservice.xhibit.business.entities.xhb_courtel_list;

/**
 * Data object for XhbCourtelList.
 */
public class XhbCourtelListBasicValue
   extends java.lang.Object
   implements java.io.Serializable
{
   private java.lang.Integer courtelListId;
   private java.lang.Integer xmlDocumentId;
   private java.lang.Long blobId;
   private java.lang.String filename;
   private java.lang.String sentToCourtel;
   private java.lang.Integer numServersUploadedTo;
   private java.lang.Integer numSendAttempts;
   private java.util.Date lastAttemptDatetime;
   private java.lang.String courtelResponseServer1;
   private java.lang.String courtelResponseServer2;
   private java.lang.String messageText;
   private java.util.Date lastUpdateDate;
   private java.util.Date creationDate;
   private java.lang.String createdBy;
   private java.lang.String lastUpdatedBy;
   private java.lang.Integer version;
   private java.lang.Long xmlDocumentClobId;

   public XhbCourtelListBasicValue()
   {
   }

   public XhbCourtelListBasicValue( java.lang.Integer courtelListId,java.lang.Integer xmlDocumentId,java.lang.Long blobId,java.lang.String filename,java.lang.String sentToCourtel,java.lang.Integer numServersUploadedTo,java.lang.Integer numSendAttempts,java.util.Date lastAttemptDatetime,java.lang.String courtelResponseServer1,java.lang.String courtelResponseServer2,java.lang.String messageText,java.util.Date lastUpdateDate,java.util.Date creationDate,java.lang.String createdBy,java.lang.String lastUpdatedBy,java.lang.Integer version,java.lang.Long xmlDocumentClobId )
   {
      setCourtelListId(courtelListId);
      setXmlDocumentId(xmlDocumentId);
      setBlobId(blobId);
      setFilename(filename);
      setSentToCourtel(sentToCourtel);
      setNumServersUploadedTo(numServersUploadedTo);
      setNumSendAttempts(numSendAttempts);
      setLastAttemptDatetime(lastAttemptDatetime);
      setCourtelResponseServer1(courtelResponseServer1);
      setCourtelResponseServer2(courtelResponseServer2);
      setMessageText(messageText);
      setLastUpdateDate(lastUpdateDate);
      setCreationDate(creationDate);
      setCreatedBy(createdBy);
      setLastUpdatedBy(lastUpdatedBy);
      setVersion(version);
      setXmlDocumentClobId(xmlDocumentClobId);
   }

   public XhbCourtelListBasicValue( XhbCourtelListBasicValue otherData )
   {
      setCourtelListId(otherData.getCourtelListId());
      setXmlDocumentId(otherData.getXmlDocumentId());
      setBlobId(otherData.getBlobId());
      setFilename(otherData.getFilename());
      setSentToCourtel(otherData.getSentToCourtel());
      setNumServersUploadedTo(otherData.getNumServersUploadedTo());
      setNumSendAttempts(otherData.getNumSendAttempts());
      setLastAttemptDatetime(otherData.getLastAttemptDatetime());
      setCourtelResponseServer1(otherData.getCourtelResponseServer1());
      setCourtelResponseServer2(otherData.getCourtelResponseServer2());
      setMessageText(otherData.getMessageText());
      setLastUpdateDate(otherData.getLastUpdateDate());
      setCreationDate(otherData.getCreationDate());
      setCreatedBy(otherData.getCreatedBy());
      setLastUpdatedBy(otherData.getLastUpdatedBy());
      setVersion(otherData.getVersion());
      setXmlDocumentClobId(otherData.getXmlDocumentClobId());

   }

   public java.lang.Integer getPrimaryKey() {
     return  getCourtelListId();
   }

   public java.lang.Integer getCourtelListId()
   {
      return this.courtelListId;
   }
   public void setCourtelListId( java.lang.Integer courtelListId )
   {
      this.courtelListId = courtelListId;
   }

   public java.lang.Integer getXmlDocumentId()
   {
      return this.xmlDocumentId;
   }
   public void setXmlDocumentId( java.lang.Integer xmlDocumentId )
   {
      this.xmlDocumentId = xmlDocumentId;
   }

   public java.lang.Long getBlobId()
   {
      return this.blobId;
   }
   public void setBlobId( java.lang.Long blobId )
   {
      this.blobId = blobId;
   }

   public java.lang.String getFilename()
   {
      return this.filename;
   }
   public void setFilename( java.lang.String filename )
   {
      this.filename = filename;
   }

   public java.lang.String getSentToCourtel()
   {
      return this.sentToCourtel;
   }
   public void setSentToCourtel( java.lang.String sentToCourtel )
   {
      this.sentToCourtel = sentToCourtel;
   }

   public java.lang.Integer getNumServersUploadedTo()
   {
      return this.numServersUploadedTo;
   }
   public void setNumServersUploadedTo( java.lang.Integer numServersUploadedTo )
   {
      this.numServersUploadedTo = numServersUploadedTo;
   }

   public java.lang.Integer getNumSendAttempts()
   {
      return this.numSendAttempts;
   }
   public void setNumSendAttempts( java.lang.Integer numSendAttempts )
   {
      this.numSendAttempts = numSendAttempts;
   }

   public java.util.Date getLastAttemptDatetime()
   {
      return this.lastAttemptDatetime;
   }
   public void setLastAttemptDatetime( java.util.Date lastAttemptDatetime )
   {
      this.lastAttemptDatetime = lastAttemptDatetime;
   }

   public java.lang.String getCourtelResponseServer1()
   {
      return this.courtelResponseServer1;
   }
   public void setCourtelResponseServer1( java.lang.String courtelResponseServer1 )
   {
      this.courtelResponseServer1 = courtelResponseServer1;
   }

   public java.lang.String getCourtelResponseServer2()
   {
      return this.courtelResponseServer2;
   }
   public void setCourtelResponseServer2( java.lang.String courtelResponseServer2 )
   {
      this.courtelResponseServer2 = courtelResponseServer2;
   }

   public java.lang.String getMessageText()
   {
      return this.messageText;
   }
   public void setMessageText( java.lang.String messageText )
   {
      this.messageText = messageText;
   }

   public java.util.Date getLastUpdateDate()
   {
      return this.lastUpdateDate;
   }
   public void setLastUpdateDate( java.util.Date lastUpdateDate )
   {
      this.lastUpdateDate = lastUpdateDate;
   }

   public java.util.Date getCreationDate()
   {
      return this.creationDate;
   }
   public void setCreationDate( java.util.Date creationDate )
   {
      this.creationDate = creationDate;
   }

   public java.lang.String getCreatedBy()
   {
      return this.createdBy;
   }
   public void setCreatedBy( java.lang.String createdBy )
   {
      this.createdBy = createdBy;
   }

   public java.lang.String getLastUpdatedBy()
   {
      return this.lastUpdatedBy;
   }
   public void setLastUpdatedBy( java.lang.String lastUpdatedBy )
   {
      this.lastUpdatedBy = lastUpdatedBy;
   }

   public java.lang.Integer getVersion()
   {
      return this.version;
   }
   public void setVersion( java.lang.Integer version )
   {
      this.version = version;
   }

   public java.lang.Long getXmlDocumentClobId()
   {
      return this.xmlDocumentClobId;
   }
   public void setXmlDocumentClobId( java.lang.Long xmlDocumentClobId )
   {
      this.xmlDocumentClobId = xmlDocumentClobId;
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer("{");

      str.append("courtelListId=" + getCourtelListId() + " " + "xmlDocumentId=" + getXmlDocumentId() + " " + "blobId=" + getBlobId() + " " + "filename=" + getFilename() + " " + "sentToCourtel=" + getSentToCourtel() + " " + "numServersUploadedTo=" + getNumServersUploadedTo() + " " + "numSendAttempts=" + getNumSendAttempts() + " " + "lastAttemptDatetime=" + getLastAttemptDatetime() + " " + "courtelResponseServer1=" + getCourtelResponseServer1() + " " + "courtelResponseServer2=" + getCourtelResponseServer2() + " " + "messageText=" + getMessageText() + " " + "lastUpdateDate=" + getLastUpdateDate() + " " + "creationDate=" + getCreationDate() + " " + "createdBy=" + getCreatedBy() + " " + "lastUpdatedBy=" + getLastUpdatedBy() + " " + "version=" + getVersion() + " " + "xmlDocumentClobId=" + getXmlDocumentClobId());
      str.append('}');

      return(str.toString());
   }

   public boolean equals( Object pOther )
   {
      if( pOther instanceof XhbCourtelListBasicValue )
      {
         XhbCourtelListBasicValue lTest = (XhbCourtelListBasicValue) pOther;
         boolean lEquals = true;

         if( this.courtelListId == null )
         {
            lEquals = lEquals && ( lTest.courtelListId == null );
         }
         else
         {
            lEquals = lEquals && this.courtelListId.equals( lTest.courtelListId );
         }
         if( this.xmlDocumentId == null )
         {
            lEquals = lEquals && ( lTest.xmlDocumentId == null );
         }
         else
         {
            lEquals = lEquals && this.xmlDocumentId.equals( lTest.xmlDocumentId );
         }
         if( this.blobId == null )
         {
            lEquals = lEquals && ( lTest.blobId == null );
         }
         else
         {
            lEquals = lEquals && this.blobId.equals( lTest.blobId );
         }
         if( this.filename == null )
         {
            lEquals = lEquals && ( lTest.filename == null );
         }
         else
         {
            lEquals = lEquals && this.filename.equals( lTest.filename );
         }
         if( this.sentToCourtel == null )
         {
            lEquals = lEquals && ( lTest.sentToCourtel == null );
         }
         else
         {
            lEquals = lEquals && this.sentToCourtel.equals( lTest.sentToCourtel );
         }
         if( this.numServersUploadedTo == null )
         {
            lEquals = lEquals && ( lTest.numServersUploadedTo == null );
         }
         else
         {
            lEquals = lEquals && this.numServersUploadedTo.equals( lTest.numServersUploadedTo );
         }
         if( this.numSendAttempts == null )
         {
            lEquals = lEquals && ( lTest.numSendAttempts == null );
         }
         else
         {
            lEquals = lEquals && this.numSendAttempts.equals( lTest.numSendAttempts );
         }
         if( this.lastAttemptDatetime == null )
         {
            lEquals = lEquals && ( lTest.lastAttemptDatetime == null );
         }
         else
         {
            lEquals = lEquals && this.lastAttemptDatetime.equals( lTest.lastAttemptDatetime );
         }
         if( this.courtelResponseServer1 == null )
         {
            lEquals = lEquals && ( lTest.courtelResponseServer1 == null );
         }
         else
         {
            lEquals = lEquals && this.courtelResponseServer1.equals( lTest.courtelResponseServer1 );
         }
         if( this.courtelResponseServer2 == null )
         {
            lEquals = lEquals && ( lTest.courtelResponseServer2 == null );
         }
         else
         {
            lEquals = lEquals && this.courtelResponseServer2.equals( lTest.courtelResponseServer2 );
         }
         if( this.messageText == null )
         {
            lEquals = lEquals && ( lTest.messageText == null );
         }
         else
         {
            lEquals = lEquals && this.messageText.equals( lTest.messageText );
         }
         if( this.lastUpdateDate == null )
         {
            lEquals = lEquals && ( lTest.lastUpdateDate == null );
         }
         else
         {
            lEquals = lEquals && this.lastUpdateDate.equals( lTest.lastUpdateDate );
         }
         if( this.creationDate == null )
         {
            lEquals = lEquals && ( lTest.creationDate == null );
         }
         else
         {
            lEquals = lEquals && this.creationDate.equals( lTest.creationDate );
         }
         if( this.createdBy == null )
         {
            lEquals = lEquals && ( lTest.createdBy == null );
         }
         else
         {
            lEquals = lEquals && this.createdBy.equals( lTest.createdBy );
         }
         if( this.lastUpdatedBy == null )
         {
            lEquals = lEquals && ( lTest.lastUpdatedBy == null );
         }
         else
         {
            lEquals = lEquals && this.lastUpdatedBy.equals( lTest.lastUpdatedBy );
         }
         if( this.version == null )
         {
            lEquals = lEquals && ( lTest.version == null );
         }
         else
         {
            lEquals = lEquals && this.version.equals( lTest.version );
         }
         if( this.xmlDocumentClobId == null )
         {
            lEquals = lEquals && ( lTest.xmlDocumentClobId == null );
         }
         else
         {
            lEquals = lEquals && this.xmlDocumentClobId.equals( lTest.xmlDocumentClobId );
         }

         return lEquals;
      }
      else
      {
         return false;
      }
   }

   public int hashCode()
   {
      int result = 17;

      result = 37*result + ((this.courtelListId != null) ? this.courtelListId.hashCode() : 0);

      result = 37*result + ((this.xmlDocumentId != null) ? this.xmlDocumentId.hashCode() : 0);

      result = 37*result + ((this.blobId != null) ? this.blobId.hashCode() : 0);

      result = 37*result + ((this.filename != null) ? this.filename.hashCode() : 0);

      result = 37*result + ((this.sentToCourtel != null) ? this.sentToCourtel.hashCode() : 0);

      result = 37*result + ((this.numServersUploadedTo != null) ? this.numServersUploadedTo.hashCode() : 0);

      result = 37*result + ((this.numSendAttempts != null) ? this.numSendAttempts.hashCode() : 0);

      result = 37*result + ((this.lastAttemptDatetime != null) ? this.lastAttemptDatetime.hashCode() : 0);

      result = 37*result + ((this.courtelResponseServer1 != null) ? this.courtelResponseServer1.hashCode() : 0);

      result = 37*result + ((this.courtelResponseServer2 != null) ? this.courtelResponseServer2.hashCode() : 0);

      result = 37*result + ((this.messageText != null) ? this.messageText.hashCode() : 0);

      result = 37*result + ((this.lastUpdateDate != null) ? this.lastUpdateDate.hashCode() : 0);

      result = 37*result + ((this.creationDate != null) ? this.creationDate.hashCode() : 0);

      result = 37*result + ((this.createdBy != null) ? this.createdBy.hashCode() : 0);

      result = 37*result + ((this.lastUpdatedBy != null) ? this.lastUpdatedBy.hashCode() : 0);

      result = 37*result + ((this.version != null) ? this.version.hashCode() : 0);

      result = 37*result + ((this.xmlDocumentClobId != null) ? this.xmlDocumentClobId.hashCode() : 0);

      return result;
   }

}