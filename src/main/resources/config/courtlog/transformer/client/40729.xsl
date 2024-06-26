<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:include href="basic_elements.xsl"/>

<!-- Declare Global Variables -->
<xsl:variable name="Event_Header_Text">
  <xsl:value-of select="event/Defendant_Name" disable-output-escaping="yes"/>
  <xsl:value-of select="concat(' indictment/count number ', event/Crest_Charge_Seq_No, '/', event/Crest_Offence_Seq_No)" disable-output-escaping="yes"/>
  <!--<xsl:value-of select="' Verdict Guilty'" disable-output-escaping="yes"/>-->
  <xsl:value-of select="concat(' Verdict ', event/Verdict_Desc)" disable-output-escaping="yes"/>
</xsl:variable>
<xsl:template match="event">
	<xsl:call-template name="BasicElements"/>
</xsl:template>
</xsl:stylesheet>