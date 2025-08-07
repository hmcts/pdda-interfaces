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

  	<!-- Going straight from cs:Fixture as cs:WithFixedDate and cs:WithoutFixedDate use the same structure -->
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:CaseArrivedFrom/cs:OriginatingCourt/cs:CourtHouseCodeType"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:CaseArrivedFrom/cs:OriginatingCourt/cs:CourtHouseAddress"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:CaseArrivedFrom/cs:OriginatingCourt/cs:CourtHouseDX"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:CaseArrivedFrom/cs:OriginatingCourt/cs:CourtHouseTelephone"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:CaseArrivedFrom/cs:OriginatingCourt/cs:CourtHouseFax"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:CaseArrivedFrom/cs:OriginatingCourt/cs:Description"/>

  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:ContactDetails"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:ASNs"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:CRESTdefendantID"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:PNCnumber"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:CROnumber"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:MagistratesCourtRefNumber"/>

  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Counsel/cs:Solicitor/cs:Party/cs:Person"/>
    <xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Counsel/cs:Solicitor/cs:StartDate"/>
    <xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Counsel/cs:Solicitor/cs:EndDate"/>
    <xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Counsel/cs:Advocate"/>

  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:CRN"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:CRESTchargeID"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:CaseNumber"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:OffenceLocation"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ArrestingPoliceForceCode"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ComittedOnBail"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:OffenceStartDateTime"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:OffenceEndDateTime"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ArraignmentDate"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ConvictionDate"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:OffenceParticulars"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:CRESToffenceNumber"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:Plea"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:Verdict"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:Disposals"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:SentenceTerm"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:TermType"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ForLife"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:ChargeType"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:IndictmentNumber"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:PNCoffencecode"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:HOoffencecode"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:Life"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/cs:Charge/cs:BreachMultiple"/>

  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:Charges/@NumberOfCharges"/>

  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:OriginalCharges"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:AdditionalNotes"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:DeportationReason"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:HateCrime"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Defendants/cs:Defendant/cs:DefendantNumber"/>

  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Prosecution/cs:Advocate"/>

  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Prosecution/cs:ProsecutingReference"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationCode"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationAddress"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationDX"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:Prosecution/cs:ProsecutingOrganisation/cs:ContactDetails"/>

  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:AppealCaseDescription"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:MethodOfInstigation"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:DateOfInstigation"/>
  	<xsl:template match="cs:Fixture/cs:Cases/cs:Case/cs:CaseClassNumber"/>

</xsl:stylesheet>
