<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice"
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
    <xsl:template match="cs:DocumentID/cs:DocumentName" />
    <xsl:template match="cs:DocumentID/cs:TimeStamp" />
    <xsl:template match="cs:DocumentID/cs:Version" />
    <xsl:template match="cs:DocumentID/cs:SecurityClassification" />
    <xsl:template match="cs:DocumentID/cs:XSLstylesheetURL" />
    <xsl:template match="cs:DocumentID/cs:WelshTranslation" />
    <xsl:template match="cs:DocumentID/cs:DocumentInformation" />
    
    <xsl:template match="cs:ListHeader/cs:ListCategory" />
    <xsl:template match="cs:ListHeader/cs:CRESTprintRef" />
    <xsl:template match="cs:ListHeader/cs:CRESTlistID" />

	<xsl:template match="cs:CrownCourt/cs:CourtHouseCodeType" />
	<xsl:template match="cs:CrownCourt/cs:CourtHouseCode/@CourtHouseShortName" />
	<xsl:template match="cs:CrownCourt/cs:CourtHouseDX" />
	<xsl:template match="cs:CrownCourt/cs:CourtHouseFax" />
	<xsl:template match="cs:CrownCourt/cs:Description" />
	
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:CourtHouseCodeType" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:CourtHouseDX" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:CourtHouseFax" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:Description" />
	
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Judiciary/cs:Judge/cs:CRESTjudgeID" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Judiciary/cs:Justice/cs:CRESTjudgeID" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Judiciary/cs:StartDate" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Judiciary/cs:EndDate" />
	
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:HearingDetails/cs:HearingDate" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:HearingDetails/cs:HearingEndDate" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:HearingDetails/cs:ListNote" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:CRESThearingID" />
	
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:Advocate" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationCode" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationAddress" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationDX" />
	<xsl:template match="cs:CourtLists/cs:CourtList/cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:Prosecution/cs:ProsecutingOrganisation/cs:ContactDetails" />
	
</xsl:stylesheet>
