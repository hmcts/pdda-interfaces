<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cs="http://www.courtservice.gov.uk/schemas/courtservice"
    xmlns:apd="http://www.govtalk.gov.uk/people/AddressAndPersonalDetails"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:translation="http://www.courtservice.gov.uk/schemas/courtservice/xhibit/translation"
    version="1.0">

    <!-- Get Not Found Character -->
    <xsl:variable name="NOT_FOUND_CHAR">
        <xsl:value-of select="'**'"/>
    </xsl:variable>
    
    <xsl:template name="NOT_FOUND_FOOTER">
        <!-- [English Translation:] ** No translation found for this string -->
        <xsl:value-of select="'** Dim cyfieithiad ar gael ar gyfer y llinyn hwn'"/>
    </xsl:template>
        
    <!-- Template to look up values from data file -->
    <xsl:template name="getValue">
        <xsl:param name="key"/>
        <xsl:param name="context"/>
        <xsl:param name="language"/>
        <!-- Get the value from the lookup xml depending on the language -->
        <xsl:choose>
            <!-- If the language is the same as the default then return the key as this is the Default Language Text -->
            <xsl:when test="$language = $DefaultLang or string-length(normalize-space($key)) = 0">
                <xsl:value-of select="$key"/>
            </xsl:when>
            <!-- Look up the language then try the key, if not found try rule. If not found return key plus marker-->
            <xsl:otherwise>
                <xsl:variable name="translation">
                    <xsl:for-each select="$data/translation:D/translation:B[@l = $language]"><!-- Country is currently not used -->
                        <xsl:if test="position() = 1">
                            <xsl:variable name="translationFromKey">                                
                                <xsl:for-each select="./translation:V[(./translation:K = $key) and ((not($context)and not(./translation:C)) or (./translation:C = $context))]">
                                    <xsl:if test="position() = 1">
                                        <xsl:value-of select="./translation:T"/>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:variable>
                            <xsl:choose>
                                <xsl:when test="string-length($translationFromKey) > 0">
                                    <xsl:value-of select="$translationFromKey"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:variable name="ruleKey">
                                        <xsl:call-template name="createRuleKey">
                                            <xsl:with-param name="key" select="$key"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <xsl:variable name="translationFromRuleKey">
                                        <xsl:for-each select="./translation:V[(./translation:R = $ruleKey) and ((not($context) and not(translation:C)) or (translation:C=$context))]">
                                            <xsl:if test="position() = 1">
                                                <xsl:value-of select="./translation:T"/>
                                            </xsl:if>
                                        </xsl:for-each>
                                    </xsl:variable>
                                    <xsl:choose>
                                        <xsl:when test="string-length($translationFromRuleKey) > 0">
                                            <xsl:value-of select="$translationFromRuleKey"/>
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="string-length($translation) > 0">
                        <xsl:value-of select="$translation"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$key"/>
                        <xsl:value-of select="$NOT_FOUND_CHAR"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
   
    <!-- template to return rule based search key -->
    <xsl:template name="createRuleKey">
        <xsl:param name="key"/>
        <xsl:variable name="lowerText">
            <xsl:call-template name="toLowerCase">
                <xsl:with-param name="text" select="$key"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="removeCharsText">
            <xsl:call-template name="removeSpecialChars">
                <xsl:with-param name="text" select="$lowerText"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="normalizedText">
            <xsl:call-template name="normalize">
                <xsl:with-param name="text" select="$removeCharsText"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="$normalizedText"/>
    </xsl:template>
    
    <!-- template used to convert a string to Lower Case -->
    <xsl:template name="toLowerCase">
        <xsl:param name="text"/>
        <xsl:value-of select="translate($text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
    </xsl:template>
    
    <!-- template used to strip out special characters (convert to spaces) -->
    <xsl:template name="removeSpecialChars">
        <xsl:param name="text"/>
        <!-- Variable to hold just special characters -->
        <xsl:variable name="specialChars">
            <xsl:value-of select="translate($text,'abcdefghijklmnopqrstuvwxyz0123456789 ','')"/>
        </xsl:variable>
        <xsl:value-of select="translate($text,$specialChars,' ')"/>
    </xsl:template>
    
    <!-- Template to normalize string (leading, trailing, replace sequence of spaces with single space)-->
    <xsl:template name="normalize">
        <xsl:param name="text"/>
        <xsl:value-of select="normalize-space($text)"/>
    </xsl:template>
</xsl:stylesheet>