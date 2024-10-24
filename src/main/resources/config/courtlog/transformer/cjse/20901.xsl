<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="basic_elements.xsl"/>
	
<!-- Declare Global Variables -->
<xsl:variable name="Event_Header_Text">
	<xsl:value-of select="concat('Time Estimation for Trial - ',event/E20901_Time_Estimate_Options/E20901_TEO_time,' ',event/E20901_Time_Estimate_Options/E20901_TEO_units)"/>
</xsl:variable>
	
<xsl:template match="event">
		
	<xsl:call-template name="BasicElements"/>

</xsl:template>

</xsl:stylesheet>
