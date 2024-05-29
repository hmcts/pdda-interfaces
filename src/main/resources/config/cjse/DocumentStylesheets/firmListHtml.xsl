<?xml version="1.0" encoding="UTF-8"?>
<!--
	+       &#x00A9; Crown copyright 2003. All rights reserved.                                         +
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice" xmlns:apd="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails" version="1.1">
	<xsl:output method="html" indent="yes"/>
	<!-- set up global variables -->
	<!-- Version and name Informaiton -->
	<xsl:variable name="majorVersion" select="'2'"/>
	<xsl:variable name="minorVersion" select="'0'"/>
	<xsl:variable name="stylesheet" select="'firmlist-v2.xsl'"/>
	<xsl:variable name="last-modified-date" select="'2005-09-13'"/>
	<!-- End Version and name Informaiton -->
	<xsl:variable name="reportdate" select="//cs:ListHeader/cs:StartDate"/>
	<xsl:variable name="endDate" select="//cs:ListHeader/cs:EndDate"/>
	<xsl:variable name="version" select="//cs:ListHeader/cs:Version"/>
	<xsl:variable name="pubTime" select="//cs:ListHeader/cs:PublishedTime"/>
	<xsl:variable name="NoRep" select="'NO REPRESENTATION RECORDED'"/>
	<!-- set up keys -->
	<xsl:key name="hearingByDate" match="cs:Hearing" use="//cs:Sittings/cs:Sitting/cs:Hearings/cs:Hearing/cs:HearingDetails/cs:HearingDate"/>
	<xsl:key name="courtByName" match="//cs:CourtHouse/cs:CourtHouseName" use="//cs:CourtHouse/cs:CourtHouseName"/>
	<!-- Top level  Match -->
	<xsl:template match="/">
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
			<body>
				<!-- Display Crown Court details -->
				<xsl:apply-templates select="cs:FirmList/cs:CrownCourt"/>
				<!-- Display Court List details -->
				<xsl:apply-templates select="cs:FirmList/cs:CourtLists"/>
				<!-- Display Reserved List details -->
				<xsl:apply-templates select="cs:FirmList/cs:ReserveList"/>
				<!-- Display footer info -->
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
	<!-- 		
			**************************
			TEMPLATE MATCHES 
			*************************
	-->
	<!-- Display Court List details -->
	<xsl:template match="cs:CourtLists">
		<!-- for each hearing -->
		<xsl:for-each select="cs:CourtList">
			<xsl:variable name="sittingDate">
				<xsl:call-template name="displayDayDate">
					<xsl:with-param name="input" select="./@SittingDate"/>
				</xsl:call-template>
			</xsl:variable>
			<!--date-->
			<hr/>
			<b>
				<center>
					<font size="2">
						<xsl:value-of select="$sittingDate"/>
					</font>
				</center>
				<br/>
			</b>
			<!--courtname - need courtname from courtlists - may be more than one court list-->
			<xsl:if test="count(./cs:CourtHouse/cs:CourtHouseName) = 1">
				<!-- Only display if different from previous -->
				<xsl:choose>
					<xsl:when test="not (position()=1)">
						<xsl:variable name="pos" select="position()"/>
						<xsl:if test="not (./cs:CourtHouse/cs:CourtHouseName = ../cs:CourtList[position()=$pos - 1]/cs:CourtHouse/cs:CourtHouseName)">
							<!-- Display Court house name if different from previous-->
							<font size="2">
								<b>
									<u>
										<xsl:value-of select="./cs:CourtHouse/cs:CourtHouseName"/>
									</u>
								</b>
							</font>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<!-- Display the court name if first on list-->
						<font size="2">
							<!-- display the court house name -->
							<b>
								<u>
									<xsl:value-of select="./cs:CourtHouse/cs:CourtHouseName"/>
								</u>
							</b>
						</font>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<!-- Display sitting info -->
			<xsl:for-each select="cs:Sittings/cs:Sitting/cs:SittingPriority">
				<xsl:if test=". != 'F'">
					<xsl:call-template name="DisplaySittingInfo"/>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select="cs:Sittings/cs:Sitting/cs:SittingPriority">
				<xsl:if test=". = 'F'">
					<!--<hr/>-->
					<xsl:call-template name="DisplaySittingInfo"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="DisplaySittingInfo">
		<font size="1">
			<!-- get the formatted sitting time -->
			<xsl:variable name="sittingTime">
				<xsl:choose>
					<xsl:when test="string-length(../cs:SittingAt)=8">
						<!-- call template to postfix AM or PM -->
						<xsl:call-template name="FormatTime">
							<xsl:with-param name="input">
								<xsl:value-of select="substring(../cs:SittingAt,1,5)"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="string-length(../cs:SittingAt)=7">
						<!-- call template to postfix AM or PM -->
						<xsl:call-template name="FormatTime">
							<xsl:with-param name="input">
								<xsl:value-of select="substring(../cs:SittingAt,1,4)"/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="../cs:SittingAt"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<br/>
			<xsl:if test="../cs:SittingPriority != 'F'">
				<b>Court <xsl:value-of select="../cs:CourtRoomNumber"/>
				</b>
				<xsl:if test="../cs:SittingAt">
					<xsl:text> - sitting at </xsl:text>
					<xsl:value-of select="$sittingTime"/>
				</xsl:if>
			</xsl:if>
			<!-- Call template to display judge details, check sitting priority, only display if non sitting priority of floating -->
			<xsl:if test="../cs:SittingPriority !='F'">
				<xsl:call-template name="judiciary">
					<xsl:with-param name="judiciary_NodeSet" select="../cs:Judiciary"/>
				</xsl:call-template>
			</xsl:if>
			<br/>
			<!-- Display sitting note info -->
			<b>
				<xsl:value-of select="../cs:SittingNote"/>
				<br/>
			</b>
			<br/>
			<!-- Display the hearing info -->
			<xsl:for-each select="../cs:Hearings/cs:Hearing">
				<xsl:call-template name="hearing"/>
			</xsl:for-each>
			<xsl:if test="not( position() = last())">
				<xsl:if test="../cs:SittingPriority != 'F'">
					<hr/>
				</xsl:if>
			</xsl:if>
		</font>
	</xsl:template>
	<!-- Display the Crown Court details -->
	<xsl:template match="cs:CrownCourt">
		<xsl:variable name="reporttype" select="'Criminal Firm List'"/>
		<font size="3">
			<strong>
				The <xsl:value-of select="cs:CourtHouseType"/>
			</strong>
			<br/>
			<xsl:text> at </xsl:text>
			<xsl:call-template name="TitleCase">
				<xsl:with-param name="text" select="cs:CourtHouseName"/>
			</xsl:call-template>
		</font>
		<table width="100%">
			<tr>
				<td align="center" width="100%">
					<font size="3">
						<b>
							<xsl:value-of select="$reporttype"/>
						</b>
					</font>
				</td>
			</tr>
		</table>
		<table width="100%">
			<tr>
				<td width="75%" align="center">
					<b>
						<xsl:text> for the period </xsl:text>
						<xsl:call-template name="displayDate">
							<xsl:with-param name="input" select="$reportdate"/>
						</xsl:call-template>
						<xsl:text> to </xsl:text>
						<xsl:call-template name="displayDate">
							<xsl:with-param name="input" select="$endDate"/>
						</xsl:call-template>
					</b>
				</td>
			</tr>
			<!-- Display version details -->
			<tr>
				<td width="25%" align="center">
					<small>
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
					</small>
				</td>
			</tr>
		</table>
		<!--<h2></h2>-->
	</xsl:template>
	<!--- Display reserved list info -->
	<xsl:template match="cs:ReserveList">
		<hr/>
		<h4>
			<xsl:text>Reserve List</xsl:text>
		</h4>
		<font size="2">
			<xsl:text>The following cases are liable to be brought in at short notice to fill in any gaps arising in the main list.</xsl:text>
		</font>
		<br/>
		<br/>
		<xsl:for-each select="cs:Hearing">
			<xsl:call-template name="hearing"/>
		</xsl:for-each>
	</xsl:template>
	<!-- 		
			**************************
			TEMPLATE NAMES
			*************************
	-->
	<!-- 
		from dt:date-time.xsl 
		Return number to represent the day of the week
			0	Sunday
			1	Monday
			2	Tuesday
			3	Wednesday
			4	Thursday
			5	Friday
			6	Saturday
	-->
	<xsl:template name="calculate-day-of-the-week">
		<xsl:param name="year"/>
		<xsl:param name="month"/>
		<xsl:param name="day"/>
		<xsl:variable name="a" select="floor((14 - $month) div 12)"/>
		<xsl:variable name="y" select="$year - $a"/>
		<xsl:variable name="m" select="$month + 12 * $a - 2"/>
		<xsl:value-of select="($day + $y + floor($y div 4) - floor($y div 100) + floor($y div 400) + floor((31 * $m) div 12)) mod 7"/>
	</xsl:template>
	<!-- Format the date to a specific format -->
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
	<!-- Format the date to a specific format -->
	<xsl:template name="displayDayDate">
		<xsl:param name="input"/>
		<xsl:variable name="month" select="substring($input,6,2)"/>
		<xsl:variable name="day" select="substring($input,9,2)"/>
		<xsl:variable name="year" select="substring($input,1,4)"/>
		<xsl:variable name="dayOfWeek">
			<!-- call template to return day of week value -->
			<xsl:call-template name="calculate-day-of-the-week">
				<xsl:with-param name="year" select="$year"/>
				<xsl:with-param name="month" select="$month"/>
				<xsl:with-param name="day" select="$day"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<!-- display the day of the week -->
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
	<!-- Display the Name from the info provided -->
	<xsl:template name="DisplayFormalName">
		<!-- expects PersonalDetails/Name structure as inpit parameter -->
		<!-- return name in the following format title inits surname -->
		<!--  ie Mr FJ Bloggs -->
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
	<!-- Display solicitor details -->
	<xsl:template name="DisplaySolicitorDetails">
		<xsl:param name="party"/>
		<xsl:choose>
			<xsl:when test="$party/cs:Person">
				<!-- Call template to display Solicitor name -->
				<xsl:call-template name="DisplayFormalName">
					<xsl:with-param name="name" select="$party/cs:Person/cs:PersonalDetails/cs:Name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$party/cs:Organisation">
				<xsl:value-of select="$party/cs:Organisation/cs:OrganisationName"/>
			</xsl:when>
			<xsl:when test="'Y'">
				<xsl:text>"</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$NoRep"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="DisplayListFooter">
		<!-- creates the footer in the output -->
		<xsl:param name="court"/>
		<hr/>
		<table class="detail" width="100%">
			<tr>
				<td align="left">
					<font size="1">
						<xsl:if test="$court/cs:CourtHouseAddress">
							<xsl:for-each select="$court/cs:CourtHouseAddress/apd:Line[position() &lt; 5 and (not (position()=2 and .='-'))and not (. = ' ')]">
								<xsl:if test=".!='-'">
									<xsl:call-template name="TitleCase">
										<xsl:with-param name="text" select="."/>
									</xsl:call-template>
									<xsl:if test="not (position() = last())">
										<xsl:if test="string-length() &gt; 0">
											<xsl:text>, </xsl:text>
										</xsl:if>
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
						<xsl:if test="$court/cs:CourtHouseFax">
							<xsl:text> Fax: </xsl:text>
							<xsl:value-of select="$court/cs:CourtHouseFax"/>
						</xsl:if>
					</font>
				</td>
				<td align="right">
			</td>
			</tr>
		</table>
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
	<!-- Display hearing info -->
	<xsl:template name="hearing">
		<font size="1">
			<b>
				<!-- Display time marking note -->
				<xsl:if test="cs:TimeMarkingNote">
					<xsl:value-of select="cs:TimeMarkingNote"/>
					<p/>
				</xsl:if>
				<xsl:choose>
					<!-- only display the hearing description if not same as previous or first hearing -->
					<xsl:when test="not (position()=1)">
						<xsl:variable name="pos" select="position()"/>
						<xsl:if test="not (cs:HearingDetails/@HearingType = ../cs:Hearing[position()=$pos - 1]/cs:HearingDetails/@HearingType)">
							<!-- get the description for the hearing -->
							<xsl:value-of select="cs:HearingDetails/cs:HearingDescription"/>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<!-- Display the description if first hearing -->
						<xsl:value-of select="cs:HearingDetails/cs:HearingDescription"/>
					</xsl:otherwise>
				</xsl:choose>
			</b>
			<!-- Get the prosecution info -->
			<xsl:variable name="prosecutingref">
				<xsl:choose>
					<xsl:when test="contains(cs:Prosecution/@ProsecutingAuthority,'Crown Prosecution Service')=false">
						<!-- get value of organisation name -->
						<xsl:variable name="orgName">
							<xsl:choose>
								<xsl:when test="cs:Prosecution/cs:ProsecutingOrganisation">
									<xsl:value-of select="cs:Prosecution/cs:ProsecutingOrganisation/cs:OrganisationName"/>
								</xsl:when>
								<xsl:when test="cs:Prosecution/cs:Advocate">
									<xsl:value-of select="cs:Prosecution/cs:Advocate/cs:PersonalDetails/cs:Name/apd:CitizenNameRequestedName"/>
								</xsl:when>
							</xsl:choose>
						</xsl:variable>
						<!-- Only display if there is an organisation name -->
						<xsl:if test="$orgName != ''">
							<xsl:choose>
								<!-- for appeal cases display Respondent -->
								<xsl:when test="substring(cs:CaseNumber,1,1) = 'A'">
									<xsl:text>(Respondent : </xsl:text>
									<xsl:value-of select="$orgName"/>
									<xsl:text>)</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<!-- all others display prosecutor -->
									<xsl:text>(Prosecutor : </xsl:text>
									<xsl:value-of select="$orgName"/>
									<xsl:text>)</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<!-- Display the defendants information -->
			<xsl:call-template name="processdefendants">
				<xsl:with-param name="caseNumText" select="cs:CaseNumber"/>
				<xsl:with-param name="committingText" select="cs:CommittingCourt/cs:CourtHouseCode/@CourtHouseShortName"/>
				<xsl:with-param name="prosecuteRefText" select="$prosecutingref"/>
			</xsl:call-template>
			<!-- Display list note information -->
			<xsl:if test="cs:ListNote">
				<strong>
					<xsl:value-of select="cs:ListNote"/>
				</strong>
				<br/>
			</xsl:if>
			<br/>
		</font>
	</xsl:template>
	<!-- Template to display judge's name -->
	<xsl:template name="judiciary">
		<xsl:param name="judiciary_NodeSet"/>
		<xsl:for-each select="$judiciary_NodeSet/cs:Judge">
			<xsl:variable name="judge">
				<xsl:choose>
					<xsl:when test="apd:CitizenNameRequestedName != 'N/A'">
						<xsl:value-of select="apd:CitizenNameRequestedName"/>
					</xsl:when>
					<xsl:when test="apd:CitizenNameSurname != 'N/A'">
						<xsl:value-of select="apd:CitizenNameSurname"/>
					</xsl:when>
					<!-- If requested name and surname are N/A then display following text to indicate that there is no judge allocated-->
					<xsl:otherwise>
						<xsl:text>JUDGE NOT ALLOCATED</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!-- check to ensure there is a value for the judges name -->
			<xsl:choose>
				<xsl:when test="$judge != ''">
					<center>
						<strong>
							<xsl:call-template name="toUpper">
								<xsl:with-param name="content">
									<xsl:value-of select="$judge"/>
								</xsl:with-param>
							</xsl:call-template>
						</strong>
					</center>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
		<!-- see if any Justices -->
		<xsl:variable name="justiceText">
			<xsl:choose>
				<xsl:when test="count($judiciary_NodeSet/cs:Justice) &gt; 1">
					<xsl:text>Justices:</xsl:text>
				</xsl:when>
				<xsl:when test="count($judiciary_NodeSet/cs:Justice) &gt; 0">
					<xsl:text>Justice:</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:for-each select="$judiciary_NodeSet/cs:Justice">
			<xsl:variable name="justice">
				<xsl:value-of select="apd:CitizenNameRequestedName"/>
			</xsl:variable>
			<table width="100%">
				<tr>
					<td width="10%" valign="top">
						<xsl:if test="position()=1">
							<font size="1">
								<xsl:value-of select="$justiceText"/>
							</font>
						</xsl:if>
					</td>
					<td width="90%" valign="top">
						<font size="1">
							<xsl:value-of select="$justice"/>
						</font>
					</td>
				</tr>
			</table>
		</xsl:for-each>
	</xsl:template>
	<!-- Display the footer info -->
	<xsl:template name="listfooter">
		<!-- call template with Crown Court structure -->
		<xsl:call-template name="DisplayListFooter">
			<xsl:with-param name="court" select="/cs:FirmList/cs:CrownCourt"/>
		</xsl:call-template>
		<!-- Display the published details -->
		<xsl:call-template name="publishDate"/>
	</xsl:template>
	<!-- Display the defendants information -->
	<xsl:template name="processdefendants">
		<xsl:param name="caseNumText"/>
		<xsl:param name="committingText"/>
		<xsl:param name="prosecuteRefText"/>
		<table class="detail" width="100%">
			<font size="1">
				<xsl:for-each select="cs:Defendants/cs:Defendant/cs:PersonalDetails/cs:Name">
					<!-- set up variable to hold defendant name -->
					<xsl:variable name="defendant">
						<xsl:value-of select="apd:CitizenNameSurname"/>
						<xsl:text> </xsl:text>
						<!-- display the defendant forename -->
						<xsl:value-of select="apd:CitizenNameForename[position()=1]"/>
						<!-- If there is a defendants middlename, the display the first character in upper case -->
						<xsl:if test="apd:CitizenNameForename[position()=2]">
							<xsl:text> </xsl:text>
							<xsl:call-template name="toUpper">
								<xsl:with-param name="content">
									<xsl:value-of select="substring(apd:CitizenNameForename[position()=2],1,1)"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>
					</xsl:variable>
					<xsl:choose>
						<!-- Display the defendant information -->
						<xsl:when test="position()=1">
							<tr>
								<td width="10%" valign="top">
									<font size="1">
										<xsl:value-of select="$caseNumText"/>
									</font>
								</td>
								<td width="40%" valign="top">
									<font size="1">
										<xsl:value-of select="$defendant"/>
										<xsl:if test="(position() = last())">
											<br/>
											<font size="1">
												<xsl:value-of select="$prosecuteRefText"/>
											</font>
										</xsl:if>
									</font>
								</td>
								<td width="30%" valign="top">
									<font size="1">
										<!-- If no Solicitor display default text -->
										<xsl:choose>
											<xsl:when test="../../cs:Counsel/cs:Solicitor/cs:Party">
												<!-- Call Template to display Solicitor details -->
												<xsl:call-template name="DisplaySolicitorDetails">
													<xsl:with-param name="party" select="../../cs:Counsel/cs:Solicitor/cs:Party"/>
												</xsl:call-template>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$NoRep"/>
											</xsl:otherwise>
										</xsl:choose>
									</font>
								</td>
								<td width="10%" valign="top">
									<font size="1">
										<xsl:value-of select="$committingText"/>
									</font>
								</td>
								<td width="10%" valign="top">
									<font size="1">
										<!--xsl:choose-->
											<!--xsl:when test="contains(../../../../cs:Prosecution/@ProsecutingAuthority,'Crown Prosecution Service')"-->
												<xsl:value-of select="../../cs:URN"/>
											<!--/xsl:when-->
										<!--/xsl:choose-->
									</font>
								</td>
							</tr>
						</xsl:when>
						<!-- Handling of rows after the first -->
						<xsl:otherwise>
							<tr>
								<td/>
								<td valign="top">
									<font size="1">
										<xsl:value-of select="$defendant"/>
										<xsl:if test="(position() = last())">
											<br/>
											<xsl:value-of select="$prosecuteRefText"/>
										</xsl:if>
									</font>
								</td>
								<td valign="top">
									<font size="1">
										<xsl:variable name="pos" select="position()"/>
										<xsl:choose>
											<!-- check to see if there is a solicitor -->
											<xsl:when test="../../cs:Counsel/cs:Solicitor/cs:Party/cs:Organisation or ../../cs:Counsel/cs:Solicitor/cs:Party/cs:Person">
												<!-- if solicitor is same as previous solicitor then just display quotes-->
												<xsl:variable name="lastsolicitor" select="../../../cs:Defendant[position()=$pos - 1]/cs:Counsel/cs:Solicitor/cs:Party/cs:Organisation/cs:OrganisationName"/>
												<xsl:variable name="thissolicitor" select="../../cs:Counsel/cs:Solicitor/cs:Party/cs:Organisation/cs:OrganisationName"/>
												<xsl:choose>
													<xsl:when test="$lastsolicitor=$thissolicitor">
														<!--send a Y as the paramater to indicate duplicate solicitors-->
														<xsl:call-template name="DisplaySolicitorDetails">
															<xsl:with-param name="party" select="Y"/>
														</xsl:call-template>
													</xsl:when>
													<xsl:otherwise>
														<!--send the solicitor structure as the parameter-->
														<xsl:call-template name="DisplaySolicitorDetails">
															<xsl:with-param name="party" select="../../cs:Counsel/cs:Solicitor/cs:Party"/>
														</xsl:call-template>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:when>
											<!-- If no solicitor then check previous defendant for solicitor -->
											<xsl:otherwise>
												<xsl:choose>
													<!-- if previous has no solicitor either display quote -->
													<xsl:when test="not (../../../cs:Defendant[position()=$pos - 1]/cs:Counsel/cs:Solicitor/cs:Party/cs:Organisation or ../../../cs:Defendant[position()=$pos - 1]/cs:Counsel/cs:Solicitor/cs:Party/cs:Person)">
														<xsl:text>"</xsl:text>
													</xsl:when>
													<!-- if previous solicitor has solicitor then display standard text - BW: not required-->
													<xsl:otherwise>
														<xsl:value-of select="$NoRep"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:otherwise>
										</xsl:choose>
									</font>
								</td>
								<td/>
								<!-- Not display URN ref: 55839-->
							<td valign="top">
								<font size="1">
								<xsl:value-of select="../../cs:URN"/>
								</font>
							</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</font>
		</table>
	</xsl:template>
	<!-- Display the published details -->
	<xsl:template name="publishDate">
		<table width="100%" cellpadding="0pt" cellspacing="0pt">
			<tr>
				<td width="70%">
					<font size="1">
           					Published: 
						<xsl:call-template name="displayDate">
							<xsl:with-param name="input">
								<xsl:value-of select="substring($pubTime,1,10)"/>
							</xsl:with-param>
						</xsl:call-template>
						<xsl:text> at </xsl:text>
						<xsl:value-of select="substring($pubTime,12,5)"/>
					</font>
				</td>
				<!--Display the CREST Print Ref-->
				<td width="30%" align="right">
					<font size="1">
						<xsl:value-of select="/cs:FirmList/cs:ListHeader/cs:CRESTprintRef"/>
					</font>
				</td>
			</tr>
		</table>
	</xsl:template>
	<!-- Template to convert the first character to upper case and the rest lower case -->
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
	<!-- Template to convert a string to upper case -->
	<xsl:template name="toUpper">
		<xsl:param name="content"/>
		<xsl:value-of select="translate($content,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
	</xsl:template>
</xsl:stylesheet>
