<?xml version="1.0" encoding="UTF-8"?>
<!--
	+       &#x00A9; Crown copyright 2003. All rights reserved.                                         +
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice" xmlns:apd="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails" xmlns:dt="http://xsltsl.org/date-time" version="1.1">
	<xsl:output method="html" indent="yes"/>
	<!-- 
		COMMENTS
		Transformer used by MLD to tranform XML into HTML for the Daily Prison List
		Very similar to the transfor used for the Daily List
	-->
	<!-- Global Variables -->
	<!-- Version and name Informaiton -->
	<xsl:variable name="majorVersion" select="'2'"/>
	<xsl:variable name="minorVersion" select="'0'"/>
	<xsl:variable name="stylesheet" select="'dailyPrisonlist-v2.xsl'"/>
	<xsl:variable name="last-modified-date" select="'2005-09-13'"/>
	<!-- End Version and name Informaiton -->
	<!-- Global Variable to hold the Current House House Code -->
	<xsl:variable name="CurrentCourtCode">
		<xsl:value-of select="/cs:DailyList/cs:CourtLists/cs:CourtList[1]/cs:CourtHouse/cs:CourtHouseCode"/>
	</xsl:variable>
	<!-- Pick up Parent/Satellite Court Names -->
	<xsl:variable name="CourtNames">
		<!-- display the first court name -->
		<xsl:call-template name="TitleCase">
			<xsl:with-param name="text">
				<xsl:value-of select="/cs:DailyList/cs:CourtLists/cs:CourtList[1]/cs:CourtHouse/cs:CourtHouseName"/>
			</xsl:with-param>
		</xsl:call-template>
		<!-- loop rest of court lists and pick up those which are not of the same court code as the main court (first court list) -->
		<xsl:for-each select="/cs:DailyList/cs:CourtLists/cs:CourtList[not(position() = 1)]/cs:CourtHouse/cs:CourtHouseCode[not(. = $CurrentCourtCode)]">
			<xsl:choose>
				<!-- if not last display comma and name -->
				<xsl:when test="position() != last()">
					<xsl:text>, </xsl:text>
					<xsl:call-template name="TitleCase">
						<xsl:with-param name="text">
							<xsl:value-of select="../cs:CourtHouseName"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<!-- when last display 'and ' name -->
				<xsl:otherwise>
					<xsl:text> and </xsl:text>
					<xsl:call-template name="TitleCase">
						<xsl:with-param name="text">
							<xsl:value-of select="../cs:CourtHouseName"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:variable>
	<!-- Pick up the first court house type from the Court List -->
	<xsl:variable name="CourtType">
		<xsl:for-each select="cs:DailyList/cs:CourtLists/cs:CourtList[1]/cs:CourtHouse/cs:CourtHouseType">
			<xsl:value-of select="."/>
		</xsl:for-each>
	</xsl:variable>
	<!-- top level match -->
	<xsl:template match="cs:DailyList">
		<html>
			<meta content="text/html;  charset=UTF-8" http-equiv="Content-Type"/>
			<!-- Add Comments to HTML to help future maintenance -->
			<xsl:comment>
				<xsl:text>Produced by : </xsl:text>
				<xsl:value-of select="$stylesheet"/>
				<xsl:text> Version : </xsl:text>
				<xsl:value-of select="$majorVersion"/>
				<xsl:text>.</xsl:text>
				<xsl:value-of select="$minorVersion"/>
				<xsl:text>  last modified : </xsl:text>
				<xsl:value-of select="$last-modified-date"/>
			</xsl:comment>
			<xsl:comment>&#x00A9; Crown copyright 2003. All rights reserved.</xsl:comment>
			<xsl:comment>
				<xsl:text>Document Unique Id : </xsl:text>
				<xsl:value-of select="//cs:DocumentID/cs:UniqueID"/>
				<xsl:text> Document version : </xsl:text>
				<xsl:value-of select="//cs:DocumentID/cs:Version"/>
				<xsl:text> Document timestamp : </xsl:text>
				<xsl:value-of select="//cs:DocumentID/cs:TimeStamp"/>
				<xsl:text> Document stylesheet URL : </xsl:text>
				<xsl:value-of select="//cs:DocumentID/cs:XSLstylesheetURL"/>
			</xsl:comment>
			<!-- End of Add Comments to HTML to help future maintenance -->
			<body style="font-family: Arial Narrow;">
				<!-- display the Crown Court details -->
				<xsl:apply-templates select="./cs:CrownCourt"/>
				<!-- display Court List Info -->
				<xsl:apply-templates select="./cs:CourtLists"/>
				<!-- display footer information -->
				<xsl:call-template name="ListFooter"/>
				<!-- Finish with Copyright notice -->
				<br/>
				<xsl:text>&#x00A9; Crown copyright 2003. All rights reserved. Issued by HM Courts &amp; Tribunals Service.</xsl:text>
				<!-- End Finish with Copyright notice -->
			</body>
		</html>
	</xsl:template>
	<!-- 		
			**************************
			TEMPLATE MATCHES 
			*************************
	-->
	<!-- display Court List Info -->
	<xsl:template match="cs:CourtLists">
		<xsl:for-each select="cs:CourtList">
			<xsl:if test="count(./cs:CourtHouse/cs:CourtHouseName) = 1">
				<table width="100%" style="font-size:8pt;font-weight:bold">
					<tr>
						<td>
							<!-- display the court house name -->
							<xsl:value-of select="./cs:CourtHouse/cs:CourtHouseName"/>
						</td>
					</tr>
				</table>
			</xsl:if>
			<br/>
			<xsl:for-each select="cs:Sittings/cs:Sitting">
				<!--
				'SittingPriority' logic used to determine if the case is floating
		   		F = Floating case
		   		T = Top Priority
			-->
				<xsl:choose>
					<!-- not floating display court room info -->
					<xsl:when test="not(cs:SittingPriority = 'F')">
						<table width="100%" style="font-size:8pt">
							<tr>
								<td>
									Court <xsl:value-of select="cs:CourtRoomNumber"/>
									<xsl:if test="cs:SittingAt">
								- sitting at <xsl:apply-templates select="cs:SittingAt"/>
									</xsl:if>
								</td>
							</tr>
						</table>
					</xsl:when>
					<xsl:otherwise>
						<table width="100%" style="font-size:8pt;font-weight:bold">
							<tr>
								<td>
									<!-- floating, display the following text -->
									<xsl:text>The following may be taken in any court.</xsl:text>
								</td>
							</tr>
						</table>
					</xsl:otherwise>
				</xsl:choose>
				<!-- display the judge information -->
				<xsl:call-template name="JudgeDetails">
					<xsl:with-param name="Judge" select="./cs:Judiciary"/>
				</xsl:call-template>
				<!-- display any sitting note details -->
				<table width="100%" style="font-size:8pt;font-weight:bold">
					<tr>
						<td>
							<xsl:value-of select="cs:SittingNote"/>
						</td>
					</tr>
				</table>
				<!-- display information for each hearing -->
				<!-- Hearings with sequence numbers first -->
				<xsl:for-each select="cs:Hearings/cs:Hearing">
					<xsl:if test="cs:HearingSequenceNumber != ''">
						<xsl:call-template name="hearing"/>
					</xsl:if>
				</xsl:for-each>
				<!-- Hearings with out sequence numbers -->
				<xsl:for-each select="cs:Hearings/cs:Hearing">
					<xsl:if test="cs:HearingSequenceNumber = ''">
						<xsl:call-template name="hearing"/>
					</xsl:if>
				</xsl:for-each>
				<hr/>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
	<!-- display the Crown Court details -->
	<xsl:template match="cs:CrownCourt">
		<xsl:variable name="reporttype" select="'Daily List for Prison Service '"/>
		<font size="5">
			<strong>
			The <xsl:value-of select="$CourtType"/>
			</strong>
			<br/>
			<xsl:text> at </xsl:text>
			<xsl:call-template name="TitleCase">
				<xsl:with-param name="text" select="$CourtNames"/>
			</xsl:call-template>
		</font>
		<table width="100%">
			<h2>
				<tr>
					<td width="75%" align="center">
						<font size="5pt">
							<xsl:value-of select="$reporttype"/>
							<xsl:text> for </xsl:text>
							<xsl:call-template name="displayDayDate">
								<xsl:with-param name="input">
									<xsl:value-of select="../cs:ListHeader/cs:StartDate"/>
								</xsl:with-param>
							</xsl:call-template>
						</font>
					</td>
				</tr>
				<tr>
					<td align="center">
						<font size="5pt">
							<b>
								<xsl:text>For Prison Use Only</xsl:text>
							</b>
						</font>
					</td>
				</tr>
			</h2>
			<tr>
				<td align="center">
					<xsl:variable name="initversion" select="../cs:ListHeader/cs:Version"/>
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
					<xsl:value-of select="$version"/>
				</td>
			</tr>
		</table>
		<hr/>
	</xsl:template>
	<!-- display the sitting at time -->
	<xsl:template match="cs:SittingAt">
		<xsl:choose>
			<xsl:when test="string-length(.)=8">
				<!-- call template to postfix AM or PM -->
				<xsl:call-template name="FormatTime">
					<xsl:with-param name="input">
						<xsl:value-of select="substring(.,1,5)"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="string-length(.)=7">
				<!-- call template to postfix AM or PM -->
				<xsl:call-template name="FormatTime">
					<xsl:with-param name="input">
						<xsl:value-of select="substring(.,1,4)"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- 		
			***********************
			TEMPLATE NAMES 
			**********************
	-->
	<!-- from dt:date-time.xsl -->
	<!-- Return number to represent the day of the week
			0	Sunday
			1	Monday
			2	Tuesday
			3	Wednesday
			4	Thursday
			5	Friday
			6	Saturday
	-->
	<xsl:template name="dt:calculate-day-of-the-week">
		<xsl:param name="year"/>
		<xsl:param name="month"/>
		<xsl:param name="day"/>
		<xsl:variable name="a" select="floor((14 - $month) div 12)"/>
		<xsl:variable name="y" select="$year - $a"/>
		<xsl:variable name="m" select="$month + 12 * $a - 2"/>
		<xsl:value-of select="($day + $y + floor($y div 4) - floor($y div 100) + floor($y div 400) + floor((31 * $m) div 12)) mod 7"/>
	</xsl:template>
	<!-- display the defendant details -->
	<xsl:template name="DefendantDetails">
		<xsl:param name="caseNumText"/>
		<xsl:param name="committingText"/>
		<xsl:param name="prosecuteRefText"/>
		<xsl:param name="appendText"/>
		<table class="detail" width="100%" style="font-size: 8pt">
			<!-- display all those with prison id's first - sorted -->
			<xsl:variable name="withId">withId</xsl:variable>
			<xsl:variable name="withOutID">withOutID</xsl:variable>
			<!-- display all those with prison id's first - sorted -->
			<xsl:for-each select="cs:Defendants/cs:Defendant/cs:PrisonLocation">
				<xsl:sort select="@PrisonID"/>
				<xsl:variable name="pos" select="position()"/>
				<xsl:for-each select="../cs:PersonalDetails/cs:Name">
					<xsl:call-template name="disp_defendantDetails">
						<xsl:with-param name="caseNumText" select="$caseNumText"/>
						<xsl:with-param name="committingText" select="$committingText"/>
						<xsl:with-param name="prosecuteRefText" select="$prosecuteRefText"/>
						<xsl:with-param name="appendText" select="$appendText"/>
						<xsl:with-param name="defCount" select="0"/>
						<xsl:with-param name="type" select="$withId"/>
						<xsl:with-param name="pos" select="$pos"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:for-each>
			<xsl:variable name="defCountWithId">
				<xsl:value-of select="count(cs:Defendants/cs:Defendant/cs:PrisonLocation/@PrisonID)"/>
			</xsl:variable>
			<!-- display all those with no prison id's -->
			<xsl:for-each select="cs:Defendants/cs:Defendant[not(./cs:PrisonLocation)]">
				<xsl:variable name="pos" select="position()"/>
				<xsl:for-each select="cs:PersonalDetails/cs:Name">
					<xsl:call-template name="disp_defendantDetails">
						<xsl:with-param name="caseNumText" select="$caseNumText"/>
						<xsl:with-param name="committingText" select="$committingText"/>
						<xsl:with-param name="prosecuteRefText" select="$prosecuteRefText"/>
						<xsl:with-param name="appendText" select="$appendText"/>
						<xsl:with-param name="defCount" select="$defCountWithId"/>
						<xsl:with-param name="type" select="$withOutID"/>
						<xsl:with-param name="pos" select="$pos"/>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:for-each>
		</table>
	</xsl:template>
	<!-- display defendant details -->
	<xsl:template name="disp_defendantDetails">
		<xsl:param name="caseNumText"/>
		<xsl:param name="committingText"/>
		<xsl:param name="prosecuteRefText"/>
		<xsl:param name="appendText"/>
		<xsl:param name="defCount"/>
		<xsl:param name="type"/>
		<xsl:param name="pos"/>
		<xsl:variable name="defendant">
			<xsl:value-of select="apd:CitizenNameSurname"/>
			<xsl:text> </xsl:text>
			<xsl:call-template name="TitleCase">
				<xsl:with-param name="text" select="apd:CitizenNameForename[position()=1]"/>
			</xsl:call-template>
			<!-- middle name, get initial only -->
			<xsl:if test="apd:CitizenNameForename[position()=2]">
				<xsl:text> </xsl:text>
				<xsl:value-of select="substring(apd:CitizenNameForename[position()=2],1,1)"/>
			</xsl:if>
			<xsl:value-of select="$appendText"/>
			<!-- only display if in prison, display the following details -->
			<xsl:if test="../cs:DateOfBirth or ../../cs:PrisonLocation or ../../cs:PrisonerID">
				<br/>
				<table class="info" width="75%" style="font-size: 8pt" cellpadding="0pt" cellspacing="0pt">
					<tr>
						<td width="30%">
							<xsl:if test="../cs:DateOfBirth">
								<xsl:call-template name="displayDate_mon">
									<xsl:with-param name="input" select="../cs:DateOfBirth/apd:BirthDate"/>
								</xsl:call-template>
								<xsl:text> </xsl:text>
							</xsl:if>
						</td>
						<td width="25%">
							<xsl:if test="../../cs:PrisonerID">
								<xsl:value-of select="../../cs:PrisonerID"/>
								<xsl:text> </xsl:text>
							</xsl:if>
						</td>
						<td width="45%">
							<xsl:if test="../../cs:PrisonLocation">
								<!--<xsl:value-of select="../../cs:PrisonLocation/@PrisonID"/>
								<xsl:text> / </xsl:text> -->
								<xsl:call-template name="toUpper">
									<xsl:with-param name="content" select="../../cs:PrisonLocation/cs:Location"/>
								</xsl:call-template>
							</xsl:if>
						</td>
					</tr>
				</table>
			</xsl:if>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$type='withId' and $pos=1">
				<tr>
					<td width="10%" valign="top">
						<xsl:value-of select="$caseNumText"/>
					</td>
					<td width="40%" valign="top">
						<xsl:copy-of select="$defendant"/>
					</td>
					<!-- Bichard Change - Add URN number - Tom Muir-Webb 130808 -->
					<td width="15%" valign="top">
						<xsl:value-of select="../../cs:URN"/>
					</td>
					<td width="10%" valign="top">
						<xsl:value-of select="$committingText"/>
					</td>
					<td width="25%" valign="top">
						<xsl:value-of select="$prosecuteRefText"/>
					</td>
				</tr>
			</xsl:when>
			<xsl:when test="$type='withOutID' and $pos=1 and $defCount = 0">
				<tr>
					<td width="10%" valign="top">
						<xsl:value-of select="$caseNumText"/>
					</td>
					<td width="40%" valign="top">
						<xsl:copy-of select="$defendant"/>
					</td>
					<!-- Bichard Change - Add URN number - Tom Muir-Webb 130808 -->
					<td width="15%" valign="top">
						<xsl:value-of select="../../cs:URN"/>
					</td>
					<td width="10%" valign="top">
						<xsl:value-of select="$committingText"/>
					</td>
					<td width="25%" valign="top">
						<xsl:value-of select="$prosecuteRefText"/>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<tr>
					<td/>
					<td>
						<xsl:copy-of select="$defendant"/>
					</td>
					<td>
						<xsl:value-of select="../../cs:URN"/>
					</td>
					<td/>
					<td/>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
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
	<!-- template used to format an input date string e.g. 2003-10-27 and output the date in the required format -->
	<!-- i.e. 27 October 2003 -->
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
	<!-- template used to format an input date string e.g. 2003-10-27 and output the date in the required format (including the day) -->
	<!-- i.e. Monday 27 October 2003 -->
	<xsl:template name="displayDayDate">
		<xsl:param name="input"/>
		<xsl:variable name="month" select="substring($input,6,2)"/>
		<xsl:variable name="day" select="substring($input,9,2)"/>
		<xsl:variable name="year" select="substring($input,1,4)"/>
		<xsl:variable name="dayOfWeek">
			<xsl:call-template name="dt:calculate-day-of-the-week">
				<xsl:with-param name="year" select="$year"/>
				<xsl:with-param name="month" select="$month"/>
				<xsl:with-param name="day" select="$day"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$dayOfWeek=0">Sunday</xsl:when>
			<xsl:when test="$dayOfWeek=1">Monday</xsl:when>
			<xsl:when test="$dayOfWeek=2">Tuesday</xsl:when>
			<xsl:when test="$dayOfWeek=3">Wednesday</xsl:when>
			<xsl:when test="$dayOfWeek=4">Thursday</xsl:when>
			<xsl:when test="$dayOfWeek=5">Friday</xsl:when>
			<xsl:when test="$dayOfWeek=6">Saturday</xsl:when>
		</xsl:choose>
		<xsl:text> </xsl:text>
		<xsl:call-template name="displayDate">
			<xsl:with-param name="input">
				<xsl:value-of select="$input"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<!-- template used to format the time from 24hrs to the following format HH:MM [AM/PM]-->
	<!-- display the formatted time and then post fix with AM or PM -->
	<xsl:template name="FormatTime">
		<xsl:param name="input"/>
		<xsl:choose>
			<!-- if less than 12 postfix AM -->
			<xsl:when test="substring-before($input,':') &lt; 12">
				<xsl:value-of select="$input"/>
				<xsl:text> am</xsl:text>
			</xsl:when>
			<!-- otherwise format the hours element and postfix with PM -->
			<xsl:otherwise>
				<!-- get the hours element from the string -->
				<xsl:variable name="hrs" select="substring-before($input,':')"/>
				<xsl:choose>
					<!-- if 12 just display value as no formatting required -->
					<xsl:when test="$hrs = 12">
						<xsl:value-of select="$hrs"/>
					</xsl:when>
					<xsl:otherwise>
						<!-- subtract 12 from the 24 hours element -->
						<xsl:variable name="fmtHrs" select="$hrs - 12"/>
						<!-- if single element return prefix 0 to the hours -->
						<!-- i.e. 9 become 09 -->
						<xsl:if test="string-length($fmtHrs) = 1">
							<xsl:text>0</xsl:text>
						</xsl:if>
						<!-- display the value of the formatted hours -->
						<xsl:value-of select="$fmtHrs"/>
					</xsl:otherwise>
				</xsl:choose>
				<!-- display the rest of the time -->
				<xsl:text>:</xsl:text>
				<xsl:value-of select="substring-after($input,':')"/>
				<xsl:text> pm</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- display hearing details -->
	<xsl:template name="hearing">
		<xsl:variable name="hearingDescription">
			<xsl:choose>
				<xsl:when test="not (position()=1)">
					<xsl:variable name="pos" select="position()"/>
					<!-- check to ensure that the previous description is not the same, only display if different -->
					<xsl:if test="not (cs:HearingDetails/cs:HearingDescription = ../cs:Hearing[position()=$pos - 1]/cs:HearingDetails/cs:HearingDescription)">
						<xsl:value-of select="cs:HearingDetails/cs:HearingDescription"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="cs:HearingDetails/cs:HearingDescription"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- set up bit to append to defendant if needed -->
		<xsl:variable name="appendage">
			<xsl:choose>
				<xsl:when test="starts-with($hearingDescription,'Miscellaneous ')">
					<xsl:value-of select="concat('-v-',cs:Prosecution//cs:OrganisationName)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="''"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- Get the Sitting At time -->
		<xsl:variable name="SittingAtValue">
			<xsl:call-template name="getSittingTime"/>
		</xsl:variable>
		<!-- Get the Not Before Time -->
		<xsl:variable name="NotBeforeTime">
			<xsl:call-template name="getNotBeforeTime"/>
		</xsl:variable>
		<!-- display TimeMarkingNotes -->
		<table width="100%" style="font-size:8pt;font-weight:bold">
			<tr>
				<td>
					<xsl:if test="cs:TimeMarkingNote">
						<xsl:if test="not (cs:TimeMarkingNote = ' ')">
							<xsl:variable name="upperMarkingNote">
								<xsl:call-template name="toUpper">
									<xsl:with-param name="content" select="cs:TimeMarkingNote"/>
								</xsl:call-template>
							</xsl:variable>
							<!--valid text is 'NOT BEFORE' or 'SITTING AT'-->
							<xsl:variable name="NotBefore">
								<!--If no NOT BEFORE or SITTING AT in the text then set it to NOT BEFORE -->
								<!--<xsl:if test="not(contains($upperMarkingNote,'NOT BEFORE') or contains($upperMarkingNote,'SITTING AT'))">-->
								<!--<xsl:text>NOT BEFORE </xsl:text>-->
								<!--</xsl:if>-->
							</xsl:variable>
							<xsl:choose>
								<!-- could potentially be blank, so check that it contains a colon -->
								<xsl:when test="contains(cs:TimeMarkingNote,':')">
									<!-- Only Display the Not Before Time if the value is different from the Sitting At Time -->
									<xsl:if test="$SittingAtValue != $NotBeforeTime">
										<xsl:choose>
											<!-- check to ensure that it does not already contain an AM or PM within the TimeMarkingNote -->
											<xsl:when test="not(contains(cs:TimeMarkingNote,'am') or contains(cs:TimeMarkingNote,'pm') or contains(cs:TimeMarkingNote,'AM') or contains(cs:TimeMarkingNote,'PM') or contains(cs:TimeMarkingNote,'Am') or contains(cs:TimeMarkingNote,'Pm')or contains(cs:TimeMarkingNote,'pM') or contains(cs:TimeMarkingNote,'aM'))">
												<!-- apply template to format the time -->
												<xsl:value-of select="$NotBefore"/>
												<xsl:call-template name="FormatTime">
													<xsl:with-param name="input">
														<xsl:value-of select="cs:TimeMarkingNote"/>
													</xsl:with-param>
												</xsl:call-template>
												<br/>
											</xsl:when>
											<xsl:otherwise>
												<!-- if already contain AM or PM just display the value as it is -->
												<xsl:choose>
													<xsl:when test="$NotBefore=''">
														<!--display the 'NOT BEFORE', 'SITTING AT' as required-->
														<xsl:if test="contains($upperMarkingNote,'NOT BEFORE')">
															<xsl:text>NOT BEFORE </xsl:text>
															<!--make the am or pm lowercase-->
															<xsl:call-template name="toLower">
																<xsl:with-param name="content" select="substring-after($upperMarkingNote,'NOT BEFORE')"/>
															</xsl:call-template>
														</xsl:if>
														<xsl:if test="contains($upperMarkingNote,'SITTING AT')">
															<xsl:text>SITTING AT</xsl:text>
															<!--make the am or pm lowercase-->
															<xsl:call-template name="toLower">
																<xsl:with-param name="content" select="substring-after($upperMarkingNote,'SITTING AT')"/>
															</xsl:call-template>
														</xsl:if>
														<!--if no NOT BEFORE or SITTING AT then just display time-->
														<xsl:if test="not(contains($upperMarkingNote,'NOT BEFORE') or contains($upperMarkingNote,'SITTING AT'))">
															<xsl:value-of select="cs:TimeMarkingNote"/>
														</xsl:if>
													</xsl:when>
													<xsl:otherwise>
														<!-- if already contain AM or PM make it lower case -->
														<xsl:value-of select="$NotBefore"/>
														<xsl:call-template name="toLower">
															<xsl:with-param name="content" select="cs:TimeMarkingNote"/>
														</xsl:call-template>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:if>
									<!--if Sitting At time and Not Before time are equal and it is not the first hearing for a courtroom then do not display Not before time - short ot itme before deadline hence - copy and paste approach - rework into template at some point-->
									<xsl:if test=" $SittingAtValue= $NotBeforeTime ">
										<xsl:if test="not(cs:HearingSequenceNumber=1)">
											<xsl:choose>
												<xsl:when test="$NotBefore=''">
													<!--display the 'NOT BEFORE', 'SITTING AT' as required-->
													<xsl:if test="contains($upperMarkingNote,'NOT BEFORE')">
														<xsl:text>NOT BEFORE </xsl:text>
														<!--make the am or pm lowercase-->
														<xsl:call-template name="toLower">
															<xsl:with-param name="content" select="substring-after($upperMarkingNote,'NOT BEFORE')"/>
														</xsl:call-template>
													</xsl:if>
													<xsl:if test="contains($upperMarkingNote,'SITTING AT')">
														<xsl:text>SITTING AT</xsl:text>
														<!--make the am or pm lowercase-->
														<xsl:call-template name="toLower">
															<xsl:with-param name="content" select="substring-after($upperMarkingNote,'SITTING AT')"/>
														</xsl:call-template>
													</xsl:if>
													<!--if no NOT BEFORE or SITTING AT then just display time-->
													<xsl:if test="not(contains($upperMarkingNote,'NOT BEFORE') or contains($upperMarkingNote,'SITTING AT'))">
														<xsl:value-of select="cs:TimeMarkingNote"/>
													</xsl:if>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$NotBefore"/>
													<xsl:call-template name="toLower">
														<xsl:with-param name="content" select="cs:TimeMarkingNote"/>
													</xsl:call-template>
												</xsl:otherwise>
											</xsl:choose>
											<br/>
										</xsl:if>
									</xsl:if>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="cs:TimeMarkingNote"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:if>
				</td>
			</tr>
		</table>
		<!-- display hearing description exc. Miscellaneous text -->
		<xsl:choose>
			<xsl:when test="starts-with($hearingDescription,'Miscellaneous ')">
				<table width="100%" style="font-size:8pt;font-weight:bold">
					<tr>
						<td>
							<xsl:value-of select="substring-after($hearingDescription,'Miscellaneous ')"/>
						</td>
					</tr>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<table width="100%" style="font-size:8pt;font-weight:bold">
					<tr>
						<td>
							<xsl:value-of select="$hearingDescription"/>
						</td>
					</tr>
				</table>
			</xsl:otherwise>
		</xsl:choose>
		<!-- get the prosecuting organisation name -->
		<xsl:variable name="prosecutingref">
			<!-- Pick up value from prosecuting reference -->
			<xsl:value-of select="cs:Prosecution/cs:ProsecutingReference"/>
			<!-- <xsl:choose>
				<xsl:when test="contains(cs:Prosecution//cs:OrganisationName,'Crown Prosecution Service')">
					<xsl:value-of select="cs:Prosecution/cs:ProsecutingReference"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="TitleCase">
						<xsl:with-param name="text" select="cs:Prosecution//cs:OrganisationName"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose> -->
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="cs:Defendants">
				<!-- display the defendant detail -->
				<xsl:call-template name="DefendantDetails">
					<xsl:with-param name="caseNumText" select="cs:CaseNumber"/>
					<xsl:with-param name="committingText" select="cs:CommittingCourt/cs:CourtHouseCode/@CourtHouseShortName"/>
					<xsl:with-param name="prosecuteRefText" select="$prosecutingref"/>
					<xsl:with-param name="appendText" select="$appendage"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<table width="100%" style="font-size:8pt">
					<tr>
						<td width="10%" valign="top">
							<xsl:value-of select="cs:CaseNumber"/>
						</td>
						<td width="50%" valign="top"/>
						<td width="10%" valign="top">
							<xsl:value-of select="cs:CommittingCourt/cs:CourtHouseCode/@CourtHouseShortName"/>
						</td>
						<td width="30%" valign="top">
							<xsl:value-of select="$prosecutingref"/>
						</td>
					</tr>
				</table>
			</xsl:otherwise>
		</xsl:choose>
		<!-- display any list notes -->
		<xsl:if test="cs:ListNote">
			<strong>
				<xsl:value-of select="cs:ListNote"/>
			</strong>
		</xsl:if>
		<br/>
	</xsl:template>
	<!-- display the judge details -->
	<xsl:template name="JudgeDetails">
		<xsl:param name="Judge"/>
		<table class="emphasis" width="100%" style="font-size:8pt;font-weight:bold">
			<xsl:for-each select="$Judge/cs:Judge">
				<tr>
					<td align="center">
						<strong>
							<xsl:choose>
								<xsl:when test="apd:CitizenNameRequestedName != 'N/A'">
									<xsl:value-of select="apd:CitizenNameRequestedName"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="apd:CitizenNameSurname !='N/A'">
										<xsl:value-of select="apd:CitizenNameSurname"/>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
						</strong>
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:variable name="justiceText">
			<xsl:choose>
				<xsl:when test="count($Judge/cs:Justice) &gt; 1">
					<table width="100%" style="font-size:8pt;font-weight:bold">
						<tr>
							<td>
								<xsl:text>Justices: </xsl:text>
							</td>
						</tr>
					</table>
				</xsl:when>
				<xsl:when test="count($Judge/cs:Justice) &gt; 0">
					<table width="100%" style="font-size:8pt;font-weight:bold">
						<tr>
							<td>
								<xsl:text>Justice: </xsl:text>
							</td>
						</tr>
					</table>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<table width="100%" class="emphasis" style="font-size:8pt;font-weight:bold">
			<!-- display justice information -->
			<xsl:for-each select="$Judge/cs:Justice">
				<xsl:variable name="justice">
					<xsl:value-of select="apd:CitizenNameRequestedName"/>
				</xsl:variable>
				<tr>
					<xsl:choose>
						<xsl:when test="position()=1">
							<td width="20%" align="right">
								<xsl:value-of select="$justiceText"/>
							</td>
							<td width="80%" align="left">
								<xsl:value-of select="$justice"/>
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td/>
							<td>
								<xsl:value-of select="$justice"/>
							</td>
						</xsl:otherwise>
					</xsl:choose>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	<!-- display footer information -->
	<xsl:template name="ListFooter">
		<table class="detail" width="100%" style="font-size:8pt">
			<tr>
				<!-- display published details -->
				<td align="left">
					<xsl:if test="cs:ListHeader/cs:PublishedTime">
						<xsl:text>Published: </xsl:text>
						<xsl:call-template name="displayDate">
							<xsl:with-param name="input">
								<xsl:value-of select="substring(cs:ListHeader/cs:PublishedTime,1,10)"/>
							</xsl:with-param>
						</xsl:call-template>
								at <xsl:value-of select="substring(cs:ListHeader/cs:PublishedTime,12,5)"/>
					</xsl:if>
				</td>
				<!-- display the Print Refence -->
				<td align="right">
					<xsl:value-of select="/cs:DailyList/cs:ListHeader/cs:CRESTprintRef"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<!-- template used to capitalise the first character of text and put the rest into lower case  -->
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
	<!-- convert string to upper case -->
	<xsl:template name="toUpper">
		<xsl:param name="content"/>
		<xsl:value-of select="translate($content,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
	</xsl:template>
	<!-- template used to convert a string to Lower Case -->
	<xsl:template name="toLower">
		<xsl:param name="content"/>
		<xsl:value-of select="translate($content,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
	</xsl:template>
	<!-- Template to get the Not Before Time  - this is solely used to obtain the value for the $NotBeforeTime variable that is used-->
	<!-- to compare to the Sitting at time in the ' display TimeMarkingNotes' section above-->
	<xsl:template name="getNotBeforeTime">
		<xsl:if test="contains(cs:TimeMarkingNote,':')">
			<xsl:variable name="UPPER_TMN">
				<xsl:call-template name="toUpper">
					<xsl:with-param name="content" select="cs:TimeMarkingNote"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:choose>
				<!-- deal with 'NOT BEFORE' timemarking text -->
				<xsl:when test="contains($UPPER_TMN,'NOT BEFORE')">
					<xsl:call-template name="toLower">
						<xsl:with-param name="content" select="substring-after($UPPER_TMN,'NOT BEFORE ')"/>
					</xsl:call-template>
				</xsl:when>
				<!-- deal with 'SITTING AT' timemarking text -->
				<xsl:when test="contains($UPPER_TMN,'SITTING AT')">
					<xsl:call-template name="toLower">
						<xsl:with-param name="content" select="substring-after($UPPER_TMN,'SITTING AT ')"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="not(contains(cs:TimeMarkingNote,'am') or contains(cs:TimeMarkingNote,'pm') or contains(cs:TimeMarkingNote,'AM') or contains(cs:TimeMarkingNote,'PM') or contains(cs:TimeMarkingNote,'Am') or contains(cs:TimeMarkingNote,'Pm') or contains(cs:TimeMarkingNote,'pM') or contains(cs:TimeMarkingNote,'aM'))">
							<xsl:call-template name="FormatTime">
								<xsl:with-param name="input">
									<xsl:value-of select="cs:TimeMarkingNote"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="toLower">
								<xsl:with-param name="content" select="cs:TimeMarkingNote"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<!-- Template to get the Sitting At time -->
	<xsl:template name="getSittingTime">
		<xsl:apply-templates select="../../cs:SittingAt"/>
	</xsl:template>
</xsl:stylesheet>
