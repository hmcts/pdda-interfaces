<?xml version="1.0" encoding="UTF-8"?>
<!--
	+       &#x00A9; Crown copyright 2003. All rights reserved.                                         +
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice" xmlns:apd="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails" version="1.2">
    <xsl:output method="html" indent="yes"/>
    <xsl:template match="/">
        <html>
        	<meta  content="text/html;  charset=UTF-8"  http-equiv="Content-Type"/>
        	<!-- Add Comments to HTML to help future maintenance -->
			<xsl:comment>
				<xsl:text>Produced by : </xsl:text>
				<xsl:value-of select="$stylesheet" />
				<xsl:text> Version : </xsl:text>
				<xsl:value-of select="$majorVersion"/><xsl:text>.</xsl:text><xsl:value-of select="$minorVersion"/>
				<xsl:text>  last modified : </xsl:text>
				<xsl:value-of select="$last-modified-date"/>
			</xsl:comment>
			<xsl:comment>&#x00A9; Crown copyright 2003. All rights reserved.</xsl:comment>
			<xsl:comment>
				<xsl:text>Document Unique Id : </xsl:text>
				<xsl:value-of select="//cs:DocumentID/cs:UniqueID" />
				<xsl:text> Document version : </xsl:text>
				<xsl:value-of select="//cs:DocumentID/cs:Version" />
				<xsl:text> Document timestamp : </xsl:text>
				<xsl:value-of select="//cs:DocumentID/cs:TimeStamp" />
				<xsl:text> Document stylesheet URL : </xsl:text>
				<xsl:value-of select="//cs:DocumentID/cs:XSLstylesheetURL" />
			</xsl:comment>
			<!-- End of Add Comments to HTML to help future maintenance -->	
            <body>
                <!-- display Crown Court info -->
                <xsl:apply-templates select="cs:WarnedList/cs:CrownCourt"/>
                <!-- display fixed hearings -->
                <xsl:apply-templates select="cs:WarnedList/cs:CourtLists" mode="main"/>
                <xsl:apply-templates select="cs:WarnedList/cs:CourtLists" mode="satellite"/>
                <!-- display footer info -->
                <xsl:call-template name="listfooter"/>
                <!-- Finish with Copyright notice -->
		    	<br/>
				<xsl:text>&#x00A9; Crown copyright </xsl:text>	
				<xsl:value-of select="substring(//cs:DocumentID/cs:TimeStamp, 1, 4)"/>
				<xsl:text>. All rights reserved. Issued by HM Courts &amp; Tribunals Service.</xsl:text>	
				<!-- End Finish with Copyright notice -->
            </body>
        </html>
    </xsl:template>
    <!-- Set up global Variables -->
       	<!-- Version and name Informaiton -->
	<xsl:variable name="majorVersion" select="'2'" />
	<xsl:variable name="minorVersion" select="'0a'" />
	<xsl:variable name="stylesheet" select="'warnedListHtml.xsl'" />
	<xsl:variable name="last-modified-date" select="'2005-09-23'" />
	<!-- End Version and name Informaiton -->
    <xsl:variable name="initversion" select="//cs:ListHeader/cs:Version"/>
    <!-- remove the unnecessary character v in the version details -->
    <xsl:variable name="version">
        <xsl:choose>
            <xsl:when test="contains($initversion,'v')">
                <xsl:value-of select="substring-before($initversion,'v')"/>
                <xsl:value-of select="substring-after($initversion,'v')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$initversion"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="reportdate" select="//cs:ListHeader/cs:StartDate"/>
    <xsl:variable name="endDate" select="//cs:ListHeader/cs:EndDate"/>
    <xsl:variable name="firstCourtHouseCode">
        <xsl:value-of select="cs:WarnedList/cs:CourtLists/cs:CourtList[1]/cs:CourtHouse/cs:CourtHouseCode"/>
    </xsl:variable>
    <xsl:variable name="CourtNames">
        <xsl:call-template name="TitleCase">
            <xsl:with-param name="text" select="substring-after(cs:WarnedList/cs:CourtLists/cs:CourtList[1]/cs:CourtHouse/cs:CourtHouseName, 'at ')"/>
        </xsl:call-template>
        <xsl:for-each select="cs:WarnedList/cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:CourtHouseCode[. != $firstCourtHouseCode]">
            <xsl:if test="position() = 1">
                <xsl:text>, </xsl:text>
            </xsl:if>
            <xsl:call-template name="TitleCase">
                <xsl:with-param name="text" select="substring-after(../cs:CourtHouseName,'at ')"/>
            </xsl:call-template>
            <xsl:if test="position() != last()">
                <xsl:text>, </xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:variable>
    <!--
		 ***********************
		TEMPLATE MATCH 
		***********************
	-->
    <!-- display hearings for main court -->
    <xsl:template match="cs:CourtLists" mode="main">
        <xsl:variable name="lists">
            <xsl:value-of select="count(cs:CourtList/cs:CourtHouse/cs:CourtHouseCode[. = $firstCourtHouseCode])"/>
        </xsl:variable>
        <xsl:variable name="with">
            <xsl:value-of select="count(cs:CourtList/cs:WithFixedDate[../cs:CourtHouse/cs:CourtHouseCode = $firstCourtHouseCode])"/>
        </xsl:variable>
        <xsl:variable name="withOut">
            <xsl:value-of select="count(cs:CourtList/cs:WithoutFixedDate[../cs:CourtHouse/cs:CourtHouseCode = $firstCourtHouseCode])"/>
        </xsl:variable>
        <table width="100%"> 
            <tr>
                <td width="40%" valign="top">
                    <xsl:text>The following cases are warned for: </xsl:text>
                </td>
                <td width="60%" valign="top">
                    <xsl:for-each select="cs:CourtList/cs:CourtHouse/cs:CourtHouseCode[. = $firstCourtHouseCode]">
                        <xsl:value-of select="substring-after(../cs:CourtHouseName,'at ')"/>
                        <xsl:if test="position() != last()">
                            <br/>
                        </xsl:if>
                    </xsl:for-each>
                </td>
            </tr>
        </table>
        <hr/>
        <xsl:if test="$with > 0">
            <b>
                <u>
                    <xsl:text>Fixtures</xsl:text>
                    <br/>
                    <br/>
                </u>
            </b>
        </xsl:if>
        <xsl:for-each select="./cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $firstCourtHouseCode]/cs:WithFixedDate 
[not(self::*/@HearingType=preceding::cs:WithFixedDate/@HearingType)]">
            <xsl:variable name="hearingType" select="./@HearingType"/>
             <!-- display the case details -->
            <xsl:for-each select="//cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $firstCourtHouseCode]/cs:WithFixedDate/cs:Fixture/cs:Cases/cs:Case
									[../../../@HearingType = $hearingType]">
                <xsl:if test="position()=1">
                    <!-- Display the hearing description -->
                    <b>
                        <xsl:value-of select="cs:Hearing/cs:HearingDescription"/>
                    </b>
                </xsl:if>
                <xsl:call-template name="case"/>
            </xsl:for-each>
        </xsl:for-each>
        <xsl:if test="$with >0">
            <hr/>
        </xsl:if>
        <xsl:for-each select="./cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $firstCourtHouseCode]/cs:WithoutFixedDate[not(self::*/@HearingType=preceding::cs:WithoutFixedDate/@HearingType)]">
            <xsl:variable name="hearingType" select="./@HearingType"/>
            <!-- display the case details -->
            <xsl:for-each select="//cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $firstCourtHouseCode]/cs:WithoutFixedDate/cs:Fixture/cs:Cases/cs:Case
									[../../../@HearingType = $hearingType]">
                <xsl:if test="position()=1">
                    <!-- Display the hearing description -->
                    <b>
                        <xsl:value-of select="cs:Hearing/cs:HearingDescription"/>
                    </b>
                </xsl:if>
                <xsl:call-template name="case"/>
            </xsl:for-each>
            <hr/>
        </xsl:for-each>
    </xsl:template>
    <!-- display hearing for satellite court -->
    <xsl:template match="cs:CourtLists" mode="satellite">
        <xsl:variable name="lists">
            <xsl:value-of select="count(cs:CourtList/cs:CourtHouse/cs:CourtHouseCode[. != $firstCourtHouseCode])"/>
        </xsl:variable>
        <xsl:for-each select="cs:CourtList/cs:CourtHouse/cs:CourtHouseCode[. != $firstCourtHouseCode]">
            <xsl:variable name="crtCode">
                <xsl:value-of select="."/>
            </xsl:variable>
            <xsl:variable name="with">
                <xsl:value-of select="count(../../../cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $crtCode]/cs:WithFixedDate)"/>
            </xsl:variable>
            <xsl:variable name="withOut">
                <xsl:value-of select="count(../../../cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $crtCode]/cs:WithoutFixedDate)"/>
            </xsl:variable>
            <!-- Line breaks to display the satelitte court details -->
            <xsl:if test="$with > 0 or $withOut > 0">
                <br/>
                <br/>
                <br/>
                <br/>
                <br/>
                <br/>
                <hr/>
                <table width="100%"> 
                    <tr>
                <td width="40%" valign="top">
                            <xsl:text>The following cases are warned for: </xsl:text>
                        </td>
                        <td width="60%" valign="top">
                            <xsl:value-of select="substring-after(../cs:CourtHouseName,'at ')"/>
                        </td>
                    </tr>
                </table>
                <hr/>
            </xsl:if>
            <xsl:if test="$with > 0">
                <b>
                    <u>
                        <xsl:text>Fixtures</xsl:text>
                        <br/>
                        <br/>
                    </u>
                </b>
            </xsl:if>
            <xsl:for-each select="../../../cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $crtCode]/cs:WithFixedDate[not(self::*/@HearingType=preceding-sibling::cs:WithFixedDate/@HearingType)]">

                <xsl:variable name="hearingType" select="./@HearingType"/>
                  <!-- display the case details -->
                <xsl:for-each select="//cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $crtCode]/cs:WithFixedDate/cs:Fixture/cs:Cases/cs:Case
									[../../../@HearingType = $hearingType]">
                    <xsl:if test="position()=1">
                        <!-- Display the hearing description -->
                        <b>
                            <xsl:value-of select="cs:Hearing/cs:HearingDescription"/>
                        </b>
                    </xsl:if>
                    <xsl:call-template name="case"/>
                </xsl:for-each>
            </xsl:for-each>
            <xsl:if test="$with >0">
                <hr/>
            </xsl:if>
            <xsl:for-each select="../../../cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $crtCode]/cs:WithoutFixedDate[not(self::*/@HearingType=preceding-sibling::cs:WithoutFixedDate/@HearingType)]">

                <xsl:variable name="hearingType" select="./@HearingType"/>
                <!-- display the case details -->
                <xsl:for-each select="//cs:CourtList[cs:CourtHouse/cs:CourtHouseCode = $crtCode]/cs:WithoutFixedDate/cs:Fixture/cs:Cases/cs:Case
    									[../../../@HearingType = $hearingType]">
                    <xsl:if test="position()=1">
                        <!-- Display the hearing description -->
                        <b>
                            <xsl:value-of select="cs:Hearing/cs:HearingDescription"/>
                        </b>
                    </xsl:if>
                    <xsl:call-template name="case"/>
                </xsl:for-each>
                <hr/>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    <!-- display Crown Court details -->
    <xsl:template match="cs:CrownCourt">
        <xsl:variable name="reporttype" select="'Criminal Warned List'"/>
        <font size="5" style="font-family: Arial Narrow;">
			The <xsl:value-of select="cs:CourtHouseType"/>
            <br/>
            <font size="3" style="font-family: Arial Narrow;">
                <xsl:text>at </xsl:text>
                <xsl:call-template name="TitleCase">
                    <xsl:with-param name="text" select="substring-after(../cs:CourtLists/cs:CourtList[1]/cs:CourtHouse/cs:CourtHouseName, 'at ')"/>
                </xsl:call-template>
                <!-- Pick up all court that are not the same as the main court to construct list of court names -->
                <xsl:for-each select="../cs:CourtLists/cs:CourtList/cs:CourtHouse/cs:CourtHouseCode[. != $firstCourtHouseCode]">
                    <xsl:if test="position() = 1">
                        <xsl:text>, </xsl:text>
                    </xsl:if>
                    <xsl:call-template name="TitleCase">
                        <xsl:with-param name="text" select="substring-after(../cs:CourtHouseName,'at ')"/>
                    </xsl:call-template>
                    <xsl:if test="position() != last()">
                        <xsl:text>, </xsl:text>
                    </xsl:if>
                </xsl:for-each>
            </font>
        </font>
        <font size="5" style="font-family: Arial Narrow;">
            <center>
                <xsl:value-of select="$reporttype"/>
                <br/>
            </center>
        </font>
        <center>
            <font size="2" style="font-family: Arial Narrow;">
                <xsl:value-of select="$version"/>
            </font>
        </center>
        <xsl:variable name="WLText">
				The undermentioned cases are warned for hearing during the period
			</xsl:variable>
        <table width="100%">
            <xsl:call-template name="bulletRow">
                <xsl:with-param name="bulletText" select="$WLText"/>
            </xsl:call-template>
        </table>
        <br/>
        <b>
            <center>
                <!-- display report date -->
                <xsl:call-template name="displayDate">
                    <xsl:with-param name="input" select="$reportdate"/>
                </xsl:call-template>
                <xsl:text> to </xsl:text>
                <xsl:call-template name="displayDate">
                    <xsl:with-param name="input" select="$endDate"/>
                </xsl:call-template>
            </center>
        </b>
        <!--make this line centred and not full length-->
        <table width="100%">
            <tr>
                <td width="33.33%"/>
                <td width="33.33%">
                    <hr/>
                </td>
                <td/>
            </tr>
        </table>
        <!-- display listing instructions -->
        <table width="100%">
            <xsl:for-each select="/cs:WarnedList/cs:ListingInstructions/cs:ListingInstruction">
                <xsl:variable name="text">
                    <xsl:choose>
                        <xsl:when test="starts-with(. , 'No later than') or starts-with(. , 'no later than') or starts-with (., 'immediately')">
                            <xsl:text>Any representation about the listing of a case should be made to the Listing Officer</xsl:text>
                            <br/>
                            <strong>
                                <xsl:value-of select="."/>
                            </strong>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="."/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <!-- bullet point the text -->
                <xsl:call-template name="bulletRow">
                    <xsl:with-param name="bulletText" select="$text"/>
                </xsl:call-template>
            </xsl:for-each>
            <xsl:variable name="text2">
                <xsl:text>The prosecuting authority is the Crown Prosecution Service unless otherwise stated.</xsl:text>
            </xsl:variable>
            <xsl:call-template name="bulletRow">
                <xsl:with-param name="bulletText" select="$text2"/>
            </xsl:call-template>
            <tr>
                <td>
                    <table width="85%">
                        <tr>
                            <td align="right" style="font-size: large; font-weight: bold;">
                                <xsl:text>* </xsl:text>
                            </td>
                        </tr>
                    </table>
                </td>
                <td>
                    <xsl:text>Denotes a defendant in custody</xsl:text>
                </td>
            </tr>
        </table>
        <hr/>
    </xsl:template>
    <!--
		 ***********************
		TEMPLATE NAMES
		***********************
	-->
    <!-- template to bullet text-->
    <xsl:template name="bulletRow">
        <xsl:param name="bulletText"/>
        <xsl:param name="bulletWidth" select="5"/>
        <tr>
            <xsl:element name="td">
                <xsl:attribute name="width"><xsl:value-of select="concat($bulletWidth,'%')"/></xsl:attribute>
                <xsl:attribute name="align">right</xsl:attribute>
                <xsl:attribute name="valign">top</xsl:attribute>
                <li/>
            </xsl:element>
            <xsl:variable name="remainder" select="100-$bulletWidth"/>
            <xsl:element name="td">
                <xsl:attribute name="width"><xsl:value-of select="concat($remainder,'%')"/></xsl:attribute>
                <xsl:choose>
                    <xsl:when test="starts-with(. , 'PLEASE NOTE THIS IS')">
                        <xsl:value-of select="substring-before(.,'--------------------------------------------- ')"/>
                        <br/>
                        <xsl:value-of select="substring-after(.,'WARNED LIST')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="$bulletText"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>
        </tr>
    </xsl:template>
    <!-- display the case details -->
    <xsl:template name="case">
        <br/>
        <xsl:variable name="prosecutingref">
            <xsl:choose>
                <xsl:when test="contains(cs:Prosecution//cs:OrganisationName,'Crown Prosecution Service')">
                    <xsl:choose>
                        <xsl:when test="starts-with(cs:Prosecution/cs:ProsecutingReference , 'CPS:')">
                            <xsl:value-of select="substring-after(cs:Prosecution/cs:ProsecutingReference,'CPS:')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="cs:Prosecution/cs:ProsecutingReference"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        <!-- display the defendant details -->
        <xsl:call-template name="processdefendants">
            <xsl:with-param name="caseNumText" select="cs:CaseNumber"/>
            <xsl:with-param name="committingText" select="cs:CaseArrivedFrom/cs:OriginatingCourt/cs:CourtHouseCode/@CourtHouseShortName"/>
            <xsl:with-param name="prosecuteRefText" select="$prosecutingref"/>
        </xsl:call-template>
        <table width="100%" class="detail">
            <tr>
                <td width="10%"/>
                <td width="90%"/>
            </tr>
            <!-- display prosecuting organisation name -->
            <xsl:if test="not(contains(cs:Prosecution//cs:OrganisationName,'Crown Prosecution Service'))">
                <tr>
                    <td/>
                    <td>
                        <small>
                            <!-- display the prosecuting organisation -->
                            <xsl:if test="not(contains(cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationName, '(') or contains( cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationName, ')')) ">
                                <xsl:value-of select="cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationName"/>
                                <xsl:text>)</xsl:text>
                            </xsl:if>
                            <xsl:if test="(contains(cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationName, '(') or contains( cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationName, ')')) ">
                                <xsl:value-of select="cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationName"/>
                            </xsl:if>
                        </small>
                    </td>                        
                </tr>
            </xsl:if>
            <!-- check value against default -->
            <xsl:if test="../../cs:FixedDate[not(. = '1900-01-01')]">
                <tr>
                    <td/>
                    <td>
                        <small>
                            <b>
                                <!-- display the fixed date -->
                                <xsl:variable name="fixDate" select="../../cs:FixedDate"/>
                                <xsl:text>Fixed for </xsl:text>
                                <xsl:call-template name="displayDate_mon">
                                    <xsl:with-param name="input">
                                        <xsl:value-of select="$fixDate"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                                <xsl:text> </xsl:text>
                                <xsl:value-of select="../../../../cs:CourtHouse/cs:CourtHouseName"/>
                            </b>
                        </small>
                    </td>
                </tr>
            </xsl:if>
            <!-- Display any Notes that may exist -->
            <xsl:if test="../../cs:Notes">
                <tr>
                    <td/>
                    <td>
                        <small>
                            <b>
                                <xsl:value-of select="../../cs:Notes"/>
                            </b>
                        </small>
                    </td>
                </tr>
            </xsl:if>
        </table>
        <!-- display any linked case details - only list first 16 linked cases - new table-->
        <xsl:if test="../../cs:LinkedCases">
            <table class="detail" width="100%">
                <tr>
                    <td width="10%"/>
                    <td width="90%"/>
                </tr>
                <tr>
                    <td valign="top">
                        <small>
                            <xsl:text>(Linked with:</xsl:text>
                        </small>
                    </td>
                    <td valign="top">
                        <small>
                            <xsl:for-each select="../../cs:LinkedCases/cs:CaseNumber">
                                <xsl:if test="position() &lt;= 16">
                                    <xsl:value-of select="."/>
                                    <xsl:if test="position() != last()">
                                        <xsl:text>; </xsl:text>
                                    </xsl:if>
                                </xsl:if>
                                <xsl:if test="position() &gt;= 17">
                                    <!--display '& others)' text - not the 17th value -->
                                    <xsl:text>&amp; others</xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:text>)</xsl:text>
                        </small>
                    </td>
                </tr>
            </table>
        </xsl:if>
    </xsl:template>
    <!-- template to format date string -->
    <xsl:template name="displayDate">
        <xsl:param name="input"/>
        <xsl:variable name="month" select="substring($input,6,2)"/>
        <xsl:variable name="day" select="substring($input,9,2)"/>
        <xsl:variable name="year" select="substring($input,1,4)"/>
        <xsl:value-of select="$day"/>
        <xsl:text> </xsl:text>
        <xsl:choose>
            <xsl:when test="$month='01'">January</xsl:when>
            <xsl:when test="$month='02'">February</xsl:when>
            <xsl:when test="$month='03'">March</xsl:when>
            <xsl:when test="$month='04'">April</xsl:when>
            <xsl:when test="$month='05'">May</xsl:when>
            <xsl:when test="$month='06'">June</xsl:when>
            <xsl:when test="$month='07'">July</xsl:when>
            <xsl:when test="$month='08'">August</xsl:when>
            <xsl:when test="$month='09'">September</xsl:when>
            <xsl:when test="$month='10'">October</xsl:when>
            <xsl:when test="$month='11'">November</xsl:when>
            <xsl:when test="$month='12'">December</xsl:when>
        </xsl:choose>
        <xsl:text> </xsl:text>
        <xsl:value-of select="$year"/>
    </xsl:template>
    <!-- template to format date string : abreviated month value -->
    <xsl:template name="displayDate_mon">
        <xsl:param name="input"/>
        <xsl:variable name="month" select="substring($input,6,2)"/>
        <xsl:variable name="day" select="substring($input,9,2)"/>
        <xsl:variable name="year" select="substring($input,1,4)"/>
        <xsl:value-of select="$day"/>
        <xsl:text>-</xsl:text>
        <xsl:choose>
            <xsl:when test="$month='01'">JAN</xsl:when>
            <xsl:when test="$month='02'">FEB</xsl:when>
            <xsl:when test="$month='03'">MAR</xsl:when>
            <xsl:when test="$month='04'">APR</xsl:when>
            <xsl:when test="$month='05'">MAY</xsl:when>
            <xsl:when test="$month='06'">JUN</xsl:when>
            <xsl:when test="$month='07'">JUL</xsl:when>
            <xsl:when test="$month='08'">AUG</xsl:when>
            <xsl:when test="$month='09'">SEP</xsl:when>
            <xsl:when test="$month='10'">OCT</xsl:when>
            <xsl:when test="$month='11'">NOV</xsl:when>
            <xsl:when test="$month='12'">DEC</xsl:when>
        </xsl:choose>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="$year"/>
    </xsl:template>
    <!-- display name details -->
    <xsl:template name="formalName">
        <xsl:param name="name"/>
        <xsl:variable name="newname">
            <xsl:value-of select="$name/apd:CitizenNameTitle"/>
            <xsl:text> </xsl:text>
            <xsl:for-each select="$name/apd:CitizenNameForename">
                <xsl:value-of select="substring(.,1,1)"/>
            </xsl:for-each>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$name/apd:CitizenNameSurname"/>
            <xsl:for-each select="$name/apd:CitizenNameSuffix">
                <xsl:text> </xsl:text>
                <xsl:value-of select="."/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:copy-of select="$newname"/>
    </xsl:template>
    <!-- display the footer information -->
    <xsl:template name="listfooter">
        <!-- call template to display details -->
        <xsl:call-template name="listFooterDisplay">
            <xsl:with-param name="court" select="/cs:WarnedList/cs:CrownCourt"/>
        </xsl:call-template>
    </xsl:template>
    <!-- template to construct footer information -->
    <xsl:template name="listFooterDisplay">
        <!-- creates the footer in the output -->
        <xsl:param name="court"/>
        <br/>
        <br/>
        <br/>
        <br/>
        <br/>
        <br/>
        <hr/>
        <table class="detail" width="95%">
            <tr>
                <td align="left">
                    <small>
                        <xsl:if test="$court/cs:CourtHouseAddress">
                            <xsl:for-each select="$court/cs:CourtHouseAddress/apd:Line[position() &lt; 5 and (not (position()=2 and .='-'))and not (. = ' ')]">
                                <xsl:call-template name="TitleCase">
                                    <xsl:with-param name="text" select="."/>
                                </xsl:call-template>
                                <xsl:if test="not (position() = last())">
                                    <xsl:if test="string-length() &gt; 0">
                                        <xsl:text>, </xsl:text>
                                    </xsl:if>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:text> </xsl:text>
                            <xsl:value-of select="$court/cs:CourtHouseAddress/apd:PostCode"/>
                            <xsl:text>. </xsl:text>
                        </xsl:if>
                        <xsl:if test="$court/cs:CourtHouseDX">
                            <xsl:value-of select="$court/cs:CourtHouseDX"/>
                        </xsl:if>
                        <xsl:if test="$court/cs:CourtHouseTelephone">
                            <xsl:text> Tel: </xsl:text>
                            <xsl:value-of select="$court/cs:CourtHouseTelephone"/>
                        </xsl:if>
                        <!--<xsl:if test="$court/cs:CourtHouseFax">
							<xsl:text> Fax: </xsl:text>
							<xsl:value-of select="$court/cs:CourtHouseFax"/>
						</xsl:if>-->
                    </small>
                </td>
            </tr>
            <tr>
                <!--published date-->
                <td align="left">
                    <small>
                        <xsl:call-template name="publishDate"/>
                    </small>
                </td>
                <td align="right">
                    <small>
                        <!-- display print reference -->
                        <xsl:value-of select="cs:WarnedList/cs:ListHeader/cs:CRESTprintRef"/>
                    </small>
                </td>
            </tr>
        </table>
    </xsl:template>
    <!-- display defendant details -->
    <xsl:template name="processdefendants">
        <xsl:param name="caseNumText"/>
        <xsl:param name="committingText"/>
        <xsl:param name="prosecuteRefText"/>
        <table width="100%" class="detail">
            <xsl:for-each select="cs:Defendants/cs:Defendant/cs:PersonalDetails/cs:Name">
                <xsl:variable name="asterisk">
                    <xsl:if test="../../cs:CustodyStatus = 'In custody' or
				              ../../cs:CustodyStatus = 'On remand'">
                        <strong>
                            <xsl:text>*</xsl:text>
                        </strong>
                    </xsl:if>
                </xsl:variable>
                <!-- set up defendant name details -->
                <xsl:variable name="defendant">
                    <xsl:value-of select="apd:CitizenNameSurname"/>
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="apd:CitizenNameForename[position()=1]"/>
                    <xsl:if test="apd:CitizenNameForename[position()=2]">
                        <xsl:text> </xsl:text>
                        <xsl:variable name="init">
                            <!-- second forename element is the middle name -->
                            <xsl:value-of select="substring(apd:CitizenNameForename[position()=2],1,1)"/>
                        </xsl:variable>
                        <xsl:call-template name="toUpper">
                            <xsl:with-param name="content" select="$init"/>
                        </xsl:call-template>
                    </xsl:if>
                    <!-- display additional defendant details, only if defendant is in custody-->
                    <xsl:if test="../../cs:CustodyStatus = 'In custody' or ../../cs:CustodyStatus = 'On remand'">
                        <xsl:if test="../cs:Sex or ../cs:DateOfBirth">
                            <br/>
                            <xsl:if test="../cs:Sex">
                                <xsl:call-template name="toUpper">
                                    <xsl:with-param name="content" select="substring(../cs:Sex,1,1)"/>
                                </xsl:call-template>
                            </xsl:if>
                            <xsl:text> </xsl:text>
                            <xsl:if test="../cs:DateOfBirth">
                                <xsl:call-template name="displayDate_mon">
                                    <xsl:with-param name="input">
                                        <xsl:value-of select="../cs:DateOfBirth/apd:BirthDate"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </xsl:if>
                            <!--also display remand prison name and prisoner no if defendant is in custody-->
                            <xsl:text> </xsl:text>
                            <!--only display prison location if it is not 'Not Available'-->
                            <xsl:if test="../../cs:PrisonLocation/cs:Location!='Not Available'">
                                <xsl:value-of select="../../cs:PrisonLocation/cs:Location"/>
                                <xsl:text> </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="../../cs:PrisonerID"/>
                        </xsl:if>
                    </xsl:if>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="position()=1">
                        <tr>
                            <td width="8%" valign="top">
                                <small>
                                    <xsl:value-of select="$caseNumText"/>
                                </small>
                            </td>
                            <td width="2%" valign="top">
                                <small>
                                    <xsl:value-of select="$asterisk"/>
                                </small>
                            </td>
                            <td width="27%" valign="top">
                                <small>
                                    <xsl:copy-of select="$defendant"/>
                                </small>
                            </td>                            
                            <td width="27%" valign="top">
                                <small>
                                    <!-- display solicitor details -->
                                    <xsl:call-template name="solicitorDetails">
                                        <xsl:with-param name="party" select="../../cs:Counsel/cs:Solicitor/cs:Party"/>
                                        <xsl:with-param name="nobody" select="'NO REPRESENTATION RECORDED'"/>
                                    </xsl:call-template>
                                </small>
                            </td>
                            <td width="10%" valign="top">
								<small>
									<xsl:value-of select="../../cs:URN"/>                    
								</small>
                            </td>
                            <td width="13%" valign="top">
                                <small>
                                    <xsl:value-of select="$committingText"/>
                                </small>
                            </td>
                            <td width="13%" valign="top">
                                <small>
                                    <xsl:value-of select="$prosecuteRefText"/>
                                </small>
                            </td>
                        </tr>
                    </xsl:when>
                    <xsl:otherwise>
                        <tr>
                            <td/>
                            <td valign="top">
                                <xsl:value-of select="$asterisk"/>
                            </td>
                            <td valign="top">
                                <small>
                                    <xsl:copy-of select="$defendant"/>
                                </small>
                            </td>                            
                            <td valign="top">
                                <small>
                                    <!-- display solicitor details -->
                                    <xsl:call-template name="solicitorDetails">
                                        <xsl:with-param name="party" select="../../cs:Counsel/cs:Solicitor/cs:Party"/>
                                        <xsl:with-param name="nobody" select="'NO REPRESENTATION RECORDED'"/>
                                    </xsl:call-template>
                                </small>
                            </td>
                            <td>
								<small>
									<xsl:value-of select="../../cs:URN"/>                    
								</small>
                            </td>
                            <td/>
                            <td/>
                        </tr>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </table>
    </xsl:template>
    <!-- Display the published Details -->
    <xsl:template name="publishDate">
        <xsl:text>Published: </xsl:text>
        <xsl:variable name="pubTime" select="//cs:ListHeader/cs:PublishedTime"/>
        <xsl:call-template name="displayDate">
            <xsl:with-param name="input">
                <xsl:value-of select="substring($pubTime,1,10)"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    <!-- display the solicitor details -->
    <xsl:template name="solicitorDetails">
        <xsl:param name="party"/>
        <xsl:param name="nobody" select="'In person'"/>
        <xsl:choose>
            <xsl:when test="$party/cs:Person">
                <!-- display the name -->
                <xsl:call-template name="formalName">
                    <xsl:with-param name="name" select="$party/cs:Person/cs:PersonalDetails/cs:Name"/>
                </xsl:call-template>
                <!-- display the first line of address -->
                <xsl:text>, </xsl:text>
                <xsl:value-of select="$party/cs:Person/cs:PersonalDetails/cs:Address/apd:Line[4]"/>
            </xsl:when>
            <!-- display the organisation name -->
            <xsl:when test="$party/cs:Organisation">
                <xsl:value-of select="$party/cs:Organisation/cs:OrganisationName"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$nobody"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- template used to format string and upper case first letter of each word -->
    <xsl:template name="TitleCase">
        <xsl:param name="text"/>
        <xsl:param name="lastletter" select="' '"/>
        <xsl:if test="$text">
            <xsl:variable name="thisletter" select="substring($text,1,1)"/>
            <xsl:choose>
                <xsl:when test="$lastletter=' '">
                    <xsl:value-of select="translate($thisletter,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="translate($thisletter,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:call-template name="TitleCase">
                <xsl:with-param name="text" select="substring($text,2)"/>
                <xsl:with-param name="lastletter" select="$thisletter"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <!-- template used to convert a string to upper case -->
    <xsl:template name="toUpper">
        <xsl:param name="content"/>
        <xsl:value-of select="translate($content,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    </xsl:template>
</xsl:stylesheet>
