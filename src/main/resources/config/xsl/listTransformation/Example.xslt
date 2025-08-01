<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>

    <!-- Copy everything -->
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <!-- Delete these fields -->
    <xsl:template match="cs:DocumentID/cs:DocumentName" />

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

</xsl:stylesheet>
