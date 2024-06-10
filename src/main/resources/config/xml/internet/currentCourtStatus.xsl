<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <!-- Import data file which hold the hard coded text in the different languages -->
    <xsl:variable name="data" select="document('translation.xml')"/>
    <!-- Include the Transform Template -->
    <xsl:include href="..\documentdistribution\translation.xsl"/>
    <!-- Default Language -->
    <xsl:variable name="DefaultLang">en</xsl:variable>
    <xsl:template match="/">
        <xsl:param name="language"/>
        <h1>
            <xsl:call-template name="getValue">
                <xsl:with-param name="language" select="$language"/>
                <xsl:with-param name="key" select="'Daily Court Status'"/>
            </xsl:call-template>
        </h1>
        <h2>
            <xsl:variable name="crtName">
                <xsl:call-template name="TitleCase">
                    <xsl:with-param name="text" select="court/courtname"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:call-template name="getValue">
                <xsl:with-param name="language" select="$language"/>
                <xsl:with-param name="key" select="$crtName"/>
            </xsl:call-template>
        </h2>
        <xsl:variable name="MainCourt">
            <xsl:value-of select="court/courtname"/>
        </xsl:variable>
        <p>
            <xsl:call-template name="getValue">
                <xsl:with-param name="language" select="$language"/>
                <xsl:with-param name="key" select="datetimestamp/dayofweek"/>
            </xsl:call-template>
            <xsl:text> </xsl:text>
            <xsl:value-of select="datetimestamp/date"/>
            <xsl:text> </xsl:text>
            <xsl:call-template name="getValue">
                <xsl:with-param name="language" select="$language"/>
                <xsl:with-param name="key" select="datetimestamp/month"/>
            </xsl:call-template>
            <xsl:text> </xsl:text>
            <xsl:value-of select="datetimestamp/year"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="datetimestamp/hour"/>
            <xsl:text>:</xsl:text>
            <xsl:value-of select="datetimestamp/min"/>
        </p>
        <p>
            <xsl:call-template name="getValue">
                <xsl:with-param name="language" select="$language"/>
                <xsl:with-param name="key" select="'The Daily Court Status can be seen here everyday from 10:00 am. When events occur in Court this page will be updated.'"/>
            </xsl:call-template>
        </p>
        <p>
            <xsl:call-template name="getValue">
                <xsl:with-param name="language" select="$language"/>
                <xsl:with-param name="key" select="'To view the Daily Court Status of other Crown Court Centres that have XHIBIT return to the'"/>
            </xsl:call-template>
            <xsl:text> </xsl:text>
            <xsl:variable name="court_list">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'court_lists.htm'"/>
                </xsl:call-template>
            </xsl:variable>
            <a href="{$court_list}">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Court List'"/>
                </xsl:call-template>
                <xsl:text>.</xsl:text>
            </a>
        </p>
        <xsl:for-each select="court">
            <xsl:for-each select="courtsites/courtsite">
                <!-- Only re-display the court site name if more than one court site to display-->
                <xsl:if test="courtsitename != $MainCourt">
                    <h2>
                        <xsl:variable name="crtName">
                            <xsl:call-template name="TitleCase">
                                <xsl:with-param name="text" select="courtsitename"/>
                            </xsl:call-template>
                        </xsl:variable>
                        <xsl:call-template name="getValue">
                            <xsl:with-param name="language" select="$language"/>
                            <xsl:with-param name="key" select="$crtName"/>
                        </xsl:call-template>
                    </h2>
                </xsl:if>
                <table class="border" cellspacing="0">
                    <tr>
                        <th align="center">
                            <xsl:call-template name="getValue">
                                <xsl:with-param name="language" select="$language"/>
                                <xsl:with-param name="key" select="'Court Number'"/>
                            </xsl:call-template>
                        </th>
                        <th align="center">
                            <xsl:call-template name="getValue">
                                <xsl:with-param name="language" select="$language"/>
                                <xsl:with-param name="key" select="'Case Number'"/>
                            </xsl:call-template>
                        </th>
                        <th align="center">
                            <xsl:call-template name="getValue">
                                <xsl:with-param name="language" select="$language"/>
                                <xsl:with-param name="key" select="'Name'"/>
                            </xsl:call-template>
                        </th>
                        <th align="center">
                            <xsl:call-template name="getValue">
                                <xsl:with-param name="language" select="$language"/>
                                <xsl:with-param name="key" select="'Current Status'"/>
                            </xsl:call-template>
                        </th>
                    </tr>
                    <xsl:for-each select="courtrooms/courtroom">
						<xsl:sort select="substring-after(./courtroomname, 'Court ')" data-type="number" order="ascending"/>
                        <tr>
                            <td align="center">
                                <xsl:choose>
                                    <xsl:when test="courtroomname != 'null'">
                                        <xsl:choose>
                                            <xsl:when test="starts-with(courtroomname,'Court')">
                                                <xsl:call-template name="getValue">
                                                    <xsl:with-param name="language" select="$language"/>
                                                    <xsl:with-param name="key" select="'Court'"/>
                                                </xsl:call-template>
                                                <xsl:text> </xsl:text>
                                                <xsl:value-of select="substring-after(courtroomname, 'Court ')"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="courtroomname"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <br/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td align="center">
                                <xsl:choose>
                                    <xsl:when test="cases != 'null'">
                                        <xsl:for-each select="cases/caseDetails">
										<xsl:choose>
										<xsl:when test="cppurn and cppurn != ''" >
											<!-- CPP Case Number -->
											<xsl:value-of select="cppurn"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:if test="casenumber and casenumber != ''">
												<!-- Xhibit Case Number -->
												<xsl:value-of select="casetype"/>
												<xsl:value-of select="casenumber"/>
											</xsl:if>
										</xsl:otherwise>
										</xsl:choose>
                                            <br/>
                                        </xsl:for-each>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <br/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td align="center">
                                <xsl:choose>
                                    <xsl:when test="count(defendants) > 0">
                                        <xsl:for-each select="defendants/defendant">
                                            <xsl:value-of select="firstname"/>
                                            <xsl:text> </xsl:text>
                                            <xsl:value-of select="middlename"/>
                                            <xsl:text> </xsl:text>
                                            <xsl:value-of select="lastname"/>
                                            <br/>
                                        </xsl:for-each>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <br/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td align="center">
                                <xsl:choose>
                                    <!-- Check the current status to see if it contains an event -->
                                    <xsl:when test="string-length(currentstatus)>0">
                                        <xsl:call-template name="getValue">
                                            <xsl:with-param name="language" select="$language"/>
                                            <xsl:with-param name="key" select="cases/caseDetails[1]/hearingtype"/>
                                        </xsl:call-template>
                                        <xsl:text> - </xsl:text>
                                        <xsl:variable name="status_page">
                                            <xsl:call-template name="getValue">
                                                <xsl:with-param name="language" select="$language"/>
                                                <xsl:with-param name="key" select="'statusdescription.html'"/>
                                            </xsl:call-template>
                                        </xsl:variable>
										 <xsl:variable name="eventType">
											<xsl:value-of select="currentstatus/event/type"/>
										</xsl:variable>
										<xsl:choose>
											<xsl:when test="$eventType='CPP' or $eventType='cpp'">
											<b>
                                                <xsl:call-template name="DisplayCurrentStatus">
                                                    <xsl:with-param name="language" select="$language"/>
                                                </xsl:call-template>
                                            </b>
											</xsl:when>
											<xsl:otherwise>
											<a href="{$status_page}#{currentstatus/event/type}">
											<b>
                                                <xsl:call-template name="DisplayCurrentStatus">
                                                    <xsl:with-param name="language" select="$language"/>
                                                </xsl:call-template>
                                            </b>
                                            </a>
											</xsl:otherwise>
										</xsl:choose>
                                        <xsl:text> - </xsl:text>
                                        <xsl:value-of select="timestatusset"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:text>-  </xsl:text>
                                        <xsl:call-template name="getValue">
                                            <xsl:with-param name="language" select="$language"/>
                                            <xsl:with-param name="key" select="'No Information To Display'"/>
                                        </xsl:call-template>
                                        <xsl:text> -</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
                <br/>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
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
    <xsl:template name="DisplayCurrentStatus">
        <xsl:param name="language"/>
        <xsl:variable name="eventType">
            <xsl:value-of select="currentstatus/event/type"/>
        </xsl:variable>
        <xsl:choose>
            <!-- Check to see if there is an event to display -->
            <xsl:when test="string-length($eventType)>0">
                <xsl:choose>
                    <xsl:when test="$eventType='10100'">
                        <xsl:call-template name="DisplayEvent_10100">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='10500'">
                        <xsl:call-template name="DisplayEvent_10500">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20502'">
                        <xsl:call-template name="DisplayEvent_20502">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20602'">
                        <xsl:call-template name="DisplayEvent_20602">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20603'">
                        <xsl:call-template name="DisplayEvent_20603">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20604'">
                        <xsl:call-template name="DisplayEvent_20604">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20605'">
                        <xsl:call-template name="DisplayEvent_20605">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20606'">
                        <xsl:call-template name="DisplayEvent_20606">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20607'">
                        <xsl:call-template name="DisplayEvent_20607">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20608'">
                        <xsl:call-template name="DisplayEvent_20608">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20609'">
                        <xsl:call-template name="DisplayEvent_20609">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20610'">
                        <xsl:call-template name="DisplayEvent_20610">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20611'">
                        <xsl:call-template name="DisplayEvent_20611">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20612'">
                        <xsl:call-template name="DisplayEvent_20612">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20613'">
                        <xsl:call-template name="DisplayEvent_20613">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20901'">
                        <xsl:call-template name="DisplayEvent_20901">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20902'">
                        <xsl:call-template name="DisplayEvent_20902">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20903'">
                        <xsl:call-template name="DisplayEvent_20903">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20904'">
                        <xsl:call-template name="DisplayEvent_20904">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20905'">
                        <xsl:call-template name="DisplayEvent_20905">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20906'">
                        <xsl:call-template name="DisplayEvent_20906">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20907'">
                        <xsl:call-template name="DisplayEvent_20907">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20908'">
                        <xsl:call-template name="DisplayEvent_20908">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20909'">
                        <xsl:call-template name="DisplayEvent_20909">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20910'">
                        <xsl:call-template name="DisplayEvent_20910">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20911'">
                        <xsl:call-template name="DisplayEvent_20911">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20912'">
                        <xsl:call-template name="DisplayEvent_20912">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20914'">
                        <xsl:call-template name="DisplayEvent_20914">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20916'">
                        <xsl:call-template name="DisplayEvent_20916">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20917'">
                        <xsl:call-template name="DisplayEvent_20917">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20918'">
                        <xsl:call-template name="DisplayEvent_20918">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20919'">
                        <xsl:call-template name="DisplayEvent_20919">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20920'">
                        <xsl:call-template name="DisplayEvent_20920">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='20935'">
                        <xsl:call-template name="DisplayEvent_20935">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='21100'">
                        <xsl:call-template name="DisplayEvent_21100">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='21200'">
                        <xsl:call-template name="DisplayEvent_21200">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='21201'">
                        <xsl:call-template name="DisplayEvent_21201">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='30100'">
                        <xsl:call-template name="DisplayEvent_30100">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='30200'">
                        <xsl:call-template name="DisplayEvent_30200">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='30300'">
                        <xsl:call-template name="DisplayEvent_30300">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='30400'">
                        <xsl:call-template name="DisplayEvent_30400">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='30500'">
                        <xsl:call-template name="DisplayEvent_30500">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='30600'">
                        <xsl:call-template name="DisplayEvent_30600">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$eventType='40601'">
                        <xsl:call-template name="DisplayEvent_40601">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
					<xsl:when test="$eventType='CPP' or $eventType='cpp'">
                        <xsl:call-template name="DisplayEvent_CPP">
                            <xsl:with-param name="language" select="$language"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>No Event</xsl:otherwise>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="FormatDate">
        <xsl:param name="date"/>
        <xsl:param name="language"/>
        <!-- Day Value -->
        <xsl:variable name="day">
            <xsl:value-of select="substring-before($date, '-')"/>
        </xsl:variable>
        <xsl:variable name="month">
            <xsl:value-of select="translate($date,'0123456789-','')"/>
        </xsl:variable>
        <xsl:variable name="year">
            <xsl:value-of select="substring-after($date,concat($month,'-'))"/>
        </xsl:variable>
        <!-- Month Value -->
        <xsl:value-of select="$day"/>
        <xsl:text>-</xsl:text>
        <xsl:variable name="upperMonth">
            <xsl:call-template name="toUpper">
                <xsl:with-param name="content" select="$month"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="MonthValue">
            <xsl:call-template name="getValue">
                <xsl:with-param name="language" select="$language"/>
                <xsl:with-param name="key" select="$upperMonth"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:call-template name="TitleCase">
            <xsl:with-param name="text" select="$MonthValue"/>
        </xsl:call-template>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="$year"/>
    </xsl:template>
    <!-- Template to convert a string to upper case -->
    <xsl:template name="toUpper">
        <xsl:param name="content"/>
        <xsl:value-of select="translate($content,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    </xsl:template>
    <xsl:template name="DisplayEvent_10100">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Case Started'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_10500">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Resume'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20502">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Pleas and Directions Hearing on'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:call-template name="FormatDate">
            <xsl:with-param name="date" select="currentstatus/event/E20502_P_And_D_Hearing_On_Date"/>
            <xsl:with-param name="language" select="$language"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20602">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Respondent Case Opened'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20603">
        <xsl:param name="language"/>
        <xsl:choose>
            <xsl:when test="currentstatus/event/E20603_Witness_Sworn_Options/E20603_WS_List[.='E20603_Appellant_Sworn']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Appellant Sworn'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="currentstatus/event/E20603_Witness_Sworn_Options/E20603_WS_List[.='E20603_Interpreter_Sworn']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Interpreter Sworn'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="not(currentstatus/event/E20603_Witness_Sworn_Options/E20603_Witness_No[.=''])">
                        <xsl:call-template name="getValue">
                            <xsl:with-param name="language" select="$language"/>
                            <xsl:with-param name="key" select="'Witness Number'"/>
                        </xsl:call-template>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="currentstatus/event/E20603_Witness_Sworn_Options/E20603_Witness_No"/>
                        <xsl:text> </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="getValue">
                            <xsl:with-param name="language" select="$language"/>
                            <xsl:with-param name="key" select="'Witness'"/>
                        </xsl:call-template>
                        <xsl:text> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Sworn'"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="DisplayEvent_20604">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Witness evidence concluded'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20605">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Respondent Case Closed'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20606">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Appellant'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:choose>
            <xsl:when test="currentstatus/event/defendant_masked_flag = 'Y'">
                <xsl:choose>
                    <xsl:when test="currentstatus/event/defendant_masked_name != '' ">
                        <xsl:value-of select="currentstatus/event/defendant_masked_name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>*****</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="currentstatus/event/E20606_Appellant_CO_Name"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text> </xsl:text>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Case Opened'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20607">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Appellant Submissions'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20608">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Appellant Case Closed'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20609">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Bench Retire to consider Judgment'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20610">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Judgment'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20611">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Legal Submissions'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20612">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Interpreter Sworn'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20613">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Witness Number'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:value-of select="currentstatus/event/E20613_Witness_Number"/>
        <xsl:text> </xsl:text>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Continues'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20901">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Trial Time Estimate'"/>
        </xsl:call-template>
        <xsl:text>: </xsl:text>
        <xsl:value-of select="currentstatus/event/E20901_Time_Estimate_Options/E20901_TEO_time"/>
        <xsl:text> </xsl:text>
        <xsl:choose>
            <xsl:when test="currentstatus/event/E20901_Time_Estimate_Options/E20901_TEO_units='E20901_days'">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Day(s)'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="currentstatus/event/E20901_Time_Estimate_Options/E20901_TEO_units='E20901_weeks'">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Week(s)'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="currentstatus/event/E20901_Time_Estimate_Options/E20901_TEO_units='E20901_months'">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Month(s)'"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="DisplayEvent_20902">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Jury Sworn In'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20903">
        <xsl:param name="language"/>
        <xsl:choose>
            <xsl:when test="currentstatus/event/E20903_Prosecution_Case_Options/E20903_PCO_Type[.='E20903_Prosecution_Opening']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Prosecution Opening'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Prosecution Case'"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="DisplayEvent_20904">
        <xsl:param name="language"/>
        <xsl:choose>
            <xsl:when test="currentstatus/event/E20904_Witness_Sworn_Options/E20904_WSO_Type[.='E20904_Defendant_sworn']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Defendant Sworn'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="currentstatus/event/E20904_Witness_Sworn_Options/E20904_WSO_Type[.='E20904_Interpreter_sworn']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Interpreter Sworn'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="not(currentstatus/event/E20904_Witness_Sworn_Options/E20904_WSO_Number[.=''])">
                        <xsl:call-template name="getValue">
                            <xsl:with-param name="language" select="$language"/>
                            <xsl:with-param name="key" select="'Witness Number'"/>
                        </xsl:call-template>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="currentstatus/event/E20904_Witness_Sworn_Options/E20904_WSO_Number"/>
                        <xsl:text> </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="getValue">
                            <xsl:with-param name="language" select="$language"/>
                            <xsl:with-param name="key" select="'Witness'"/>
                        </xsl:call-template>
                        <xsl:text> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Sworn'"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="DisplayEvent_20905">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Witness evidence concluded'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20906">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Defence'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:choose>
            <xsl:when test="currentstatus/event/defendant_masked_flag = 'Y'">
                <xsl:choose>
                    <xsl:when test="currentstatus/event/defendant_masked_name != '' ">
                        <xsl:value-of select="currentstatus/event/defendant_masked_name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>*****</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="currentstatus/event/E20906_Defence_CO_Name"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text> </xsl:text>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Case Opened'"/>
            <xsl:with-param name="context" select="'Defence'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20907">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Prosecution Closing Speech'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20908">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Prosecution Case Closed'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20909">
        <xsl:param name="language"/>
        <xsl:choose>
            <xsl:when test="currentstatus/event/defendant_masked_flag = 'Y'">
                <xsl:choose>
                    <xsl:when test="currentstatus/event/defendant_masked_name != '' ">
                        <xsl:value-of select="currentstatus/event/defendant_masked_name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>*****</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="currentstatus/event/defendant_name"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>; </xsl:text>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Defence Closing Speech'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20910">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Defence'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:choose>
            <xsl:when test="currentstatus/event/defendant_masked_flag = 'Y'">
                <xsl:choose>
                    <xsl:when test="currentstatus/event/defendant_masked_name != '' ">
                        <xsl:value-of select="currentstatus/event/defendant_masked_name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>*****</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="currentstatus/event/E20910_Defence_CC_Name"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text> </xsl:text>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Case Closed'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20911">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Summing Up'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20912">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Legal Submissions'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20914">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Jury retire to consider verdict'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20916">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Legal Submissions'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20917">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Interpreter Sworn'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20918">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Trial Ineffective'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20919">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Verdict to be taken'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20920">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Witness Number'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:value-of select="currentstatus/event/E20920_Witness_Number"/>
        <xsl:text> </xsl:text>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Continues'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_20935">
        <xsl:param name="language"/>
        <xsl:choose>
            <xsl:when test="currentstatus/event/E20935_Witness_Read_Options/E20935_WR_Type[.='E20935_Defendant_read']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Defendant Read'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="currentstatus/event/E20935_Witness_Read_Options/E20935_WR_Type[.='E20935_Interpreter_read']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Interpreter Read'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="not(currentstatus/event/E20935_Witness_Sworn_Options/E20935_WR_Number[.=''])">
                        <xsl:call-template name="getValue">
                            <xsl:with-param name="language" select="$language"/>
                            <xsl:with-param name="key" select="'Witness Number'"/>
                        </xsl:call-template>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="currentstatus/event/E20935_Witness_Sworn_Options/E20935_WR_Number"/>
                        <xsl:text> </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="getValue">
                            <xsl:with-param name="language" select="$language"/>
                            <xsl:with-param name="key" select="'Witness'"/>
                        </xsl:call-template>
                        <xsl:text> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Read'"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="DisplayEvent_21100">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Legal Submissions'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_21200">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Reporting Restrictions. For details please contact the Court Manager'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_21201">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Reporting Restrictions Lifted'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_30100">
        <xsl:param name="language"/>
        <xsl:choose>
            <xsl:when test="currentstatus/event/E30100_Short_Adjourn_Options/E30100_SAO_Type[.='E30100_Case_released_until']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Case released until'"/>
                </xsl:call-template>
                <xsl:text> </xsl:text>
                <xsl:value-of select="currentstatus/event/E30100_Short_Adjourn_Options/E30100_SAO_Time"/>
            </xsl:when>
            <xsl:when test="currentstatus/event/E30100_Short_Adjourn_Options/E30100_SAO_Type[.='E30100_Case_adjourned_until']">
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Case adjourned until'"/>
                </xsl:call-template>
                <xsl:text> </xsl:text>
                <xsl:value-of select="currentstatus/event/E30100_Short_Adjourn_Options/E30100_SAO_Time"/>
                <xsl:text/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="getValue">
                    <xsl:with-param name="language" select="$language"/>
                    <xsl:with-param name="key" select="'Case Adjourned'"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="DisplayEvent_30200">
        <xsl:param name="language"/>
	<xsl:choose>
		<xsl:when test="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Type[.='E30200_Case_to_be_listed_in_week_commencing']">
                    <xsl:call-template name="getValue">
                        <xsl:with-param name="language" select="$language"/>
                        <xsl:with-param name="key" select="'Case to be listed in week commencing'"/>
                    </xsl:call-template>
                    <xsl:text> </xsl:text>
                    <xsl:call-template name="FormatDate">
                        <xsl:with-param name="date" select="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Date"/>
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>
		</xsl:when>
		<xsl:when test="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Type[.='E30200_Case_to_be_listed_on']">
                    <xsl:call-template name="getValue">
                        <xsl:with-param name="language" select="$language"/>
                        <xsl:with-param name="key" select="'Case to be listed on'"/>
                    </xsl:call-template>
                    <xsl:text> </xsl:text>
                    <xsl:call-template name="FormatDate">
                        <xsl:with-param name="date" select="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Date"/>
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>
		</xsl:when>
		<xsl:when test="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Type[.='E30200_Case_to_be_listed_on_date_to_be_fixed']">
                    <xsl:call-template name="getValue">
                        <xsl:with-param name="language" select="$language"/>
                        <xsl:with-param name="key" select="'Case to be listed on date to be fixed'"/>
                    </xsl:call-template>
		</xsl:when>
		<xsl:when test="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Type[.='E30200_Case_to_be_listed_for_Sentence']">
                    <xsl:call-template name="getValue">
                        <xsl:with-param name="language" select="$language"/>
                        <xsl:with-param name="key" select="'Case to be listed for Sentence on'"/>
                    </xsl:call-template>
                    <xsl:text> </xsl:text>
                    <xsl:call-template name="FormatDate">
                        <xsl:with-param name="date" select="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Date"/>
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>
		</xsl:when>
		<xsl:when test="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Type[.='E30200_Case_to_be_listed_for_Further_Mention/PAD']">
                    <xsl:call-template name="getValue">
                        <xsl:with-param name="language" select="$language"/>
                        <xsl:with-param name="key" select="'Case to be listed for Further Mention/PAD on'"/>
                    </xsl:call-template>
                    <xsl:text> </xsl:text>
                    <xsl:call-template name="FormatDate">
                        <xsl:with-param name="date" select="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Date"/>
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>
		</xsl:when>
		<xsl:when test="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Type[.='E30200_Case_to_be_listed_for_trial']">
                    <xsl:call-template name="getValue">
                        <xsl:with-param name="language" select="$language"/>
                        <xsl:with-param name="key" select="'Case to be listed for Trial on'"/>
                    </xsl:call-template>
                    <xsl:text> </xsl:text>
                    <xsl:call-template name="FormatDate">
                        <xsl:with-param name="date" select="currentstatus/event/E30200_Long_Adjourn_Options/E30200_LAO_Date"/>
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
                    <xsl:call-template name="getValue">
                        <xsl:with-param name="language" select="$language"/>
                        <xsl:with-param name="key" select="'Adjourned'"/>
                    </xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
    </xsl:template>
    <xsl:template name="DisplayEvent_30300">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Case Closed'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_30400">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Court Closed'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_30500">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Hearing finished'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DisplayEvent_30600">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Hearing finished for'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:choose>
            <xsl:when test="currentstatus/event/defendant_masked_flag = 'Y'">
                <xsl:choose>
                    <xsl:when test="currentstatus/event/defendant_masked_name != '' ">
                        <xsl:value-of select="currentstatus/event/defendant_masked_name"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>*****</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="currentstatus/event/defendant_name"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="DisplayEvent_40601">
        <xsl:param name="language"/>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'Judge'"/>
        </xsl:call-template>
        <xsl:text>'</xsl:text>
        <xsl:call-template name="getValue">
            <xsl:with-param name="language" select="$language"/>
            <xsl:with-param name="key" select="'s directions'"/>
        </xsl:call-template>
    </xsl:template>
	<xsl:template name="DisplayEvent_CPP">
        <xsl:param name="language"/>
        <xsl:value-of select="currentstatus/event/free_text"/>
    </xsl:template>
</xsl:stylesheet>
