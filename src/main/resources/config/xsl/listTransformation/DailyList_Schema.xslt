<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    version="1.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>

    <!-- Copy everything -->
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <!-- Remove these fields -->
    <xsl:template match="cs:DocumentID/cs:DocumentName"/>
    <xsl:template match="cs:DocumentID/cs:TimeStamp"/>
    <xsl:template match="cs:DocumentID/cs:Version"/>
    <xsl:template match="cs:DocumentID/cs:SecurityClassification"/>
    <xsl:template match="cs:DocumentID/cs:XSLstylesheetURL"/>
    <xsl:template match="cs:DocumentID/cs:WelshTranslation"/>
    <xsl:template match="cs:DocumentID/cs:DocumentInformation"/>

    <xsl:template match="cs:ListHeader/cs:ListCategory"/>
    <xsl:template match="cs:ListHeader/cs:CRESTprintRef"/>
    <xsl:template match="cs:ListHeader/cs:CRESTlistID"/>

  	<xsl:template match="cs:CrownCourt/cs:CourtHouseCodeType"/>
  	<xsl:template match="cs:CrownCourt/cs:CourtHouseCode/@CourtHouseShortName"/>
  	<xsl:template match="cs:CrownCourt/cs:CourtHouseDX"/>
  	<xsl:template match="cs:CrownCourt/cs:CourtHouseFax"/>
  	<xsl:template match="cs:CrownCourt/cs:Description"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:CourtHouseCodeType"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:CourtHouseDX"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:CourtHouseFax"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:Description"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Judiciary/cs:Judge/cs:CRESTjudgeID"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Judiciary/cs:Justice/cs:CRESTjudgeID"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Judiciary/cs:StartDate"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Judiciary/cs:EndDate"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:HearingDetails/cs:HearingDate"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:HearingDetails/cs:HearingEndDate"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:HearingDetails/cs:ListNote"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:CRESThearingID"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:Advocate"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationCode"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationAddress"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationDX"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:ProsecutingOrganisation/cs:ContactDetails"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:CommittingCourt/cs:CourtHouseCodeType"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:CommittingCourt/cs:CourtHouseAddress"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:CommittingCourt/cs:CourtHouseDX"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:CommittingCourt/cs:CourtHouseTelephone"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:CommittingCourt/cs:CourtHouseFax"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:CommittingCourt/cs:Description"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:NumberOfDefendants"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:ContactDetails"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:ASNs"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:CRESTdefendantID"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:PNCnumber"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:CROnumber"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:MagistratesCourtRefNumber"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:CustodyStatus"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Counsel"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:CRN"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:CRESTchargeID"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:CaseNumber"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:OffenceLocation"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ArrestingPoliceForceCode"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ComittedOnBail"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:OffenceStartDateTime"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:OffenceEndDateTime"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ArraignmentDate"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ConvictionDate"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:OffenceParticulars"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:CRESToffenceNumber"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:Plea"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:Verdict"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:Disposals"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:SentenceTerm"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:TermType"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ForLife"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ChargeType"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:IndictmentNumber"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:PNCoffencecode"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:HOoffencecode"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:Life"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:BreachMultiple"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:Charges/@NumberOfCharges"/>

  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:OriginalCharges"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:AdditionalNotes"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:DeportationReason"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:HateCrime"/>
  	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Defendants/cs:Defendant/cs:DefendantNumber"/>

    <!--  Insert the new CaseNumberCaTH field -->
    <xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing">
      <xsl:copy>
      	<!-- Copy the existing fields in the Hearing section -->
      	<xsl:apply-templates select="@*|node()"/>
        <!-- If CPP Case then use <cs:URN> value, else if XHIBIT case then use <cs:CaseNumber> field -->
        <xsl:choose>
          <xsl:when test="cs:CaseNumber = 'CPP'">
            <CaseNumberCaTH>
              <xsl:value-of select="cs:Defendants/cs:Defendant/cs:URN"/>
            </CaseNumberCaTH>
          </xsl:when>
          <xsl:otherwise>
            <CaseNumberCaTH>
              <xsl:value-of select="cs:CaseNumber"/>
            </CaseNumberCaTH>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:copy>
    </xsl:template>
    
    <!-- Check if cs:IsMasked is no, otherwise blank out cs:PersonalDetails fields -->
    <xsl:template match="cs:Defendant/cs:PersonalDetails">
      <xsl:copy>
        <xsl:choose>
          <xsl:when test="cs:IsMasked = 'no'">
            <!-- Copy the content in cs:PersonalDetails and display as normal -->
            <xsl:apply-templates select="@* | node()"/>
          </xsl:when>
          <xsl:otherwise>
            <!-- Apply templates in "mask" mode to blank all fields except cs:IsMasked -->
            <xsl:apply-templates select="@* | node()" mode="mask"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:copy>
    </xsl:template>
    
	<!-- Copy elements and attributes in "mask" mode -->
	<xsl:template match="@* | node()" mode="mask">
	  <xsl:copy>
	    <xsl:apply-templates select="@* | node()" mode="mask"/>
	  </xsl:copy>
	</xsl:template>
	
	<!-- Blank out text only in "mask" mode -->
	<xsl:template match="text()" mode="mask">
	  <xsl:text></xsl:text>
	</xsl:template>
	
	<!-- EXCEPTION: retain the value of cs:IsMasked -->
    <xsl:template match="cs:IsMasked" mode="mask">
      <xsl:copy>
        <!-- Use default copy on the cs:IsMasked field every time here -->
        <xsl:apply-templates select="@* | node()"/>
      </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>