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

</xsl:stylesheet>
