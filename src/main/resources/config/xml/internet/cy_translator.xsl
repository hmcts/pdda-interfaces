<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  <xsl:param name="mapPath" select="'translations.xml'"/>
  <xsl:variable name="mapdoc" select="document($mapPath)"/>

  <!-- identity -->
  <xsl:template match="@*|node()">
    <xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy>
  </xsl:template>

  <xsl:template match="script|style">
    <xsl:copy><xsl:apply-templates select="@*|node()" mode="passthru"/></xsl:copy>
  </xsl:template>
  <xsl:template match="@*|node()" mode="passthru">
    <xsl:copy><xsl:apply-templates select="@*|node()" mode="passthru"/></xsl:copy>
  </xsl:template>


<!-- Added key for fast lookups -->
<xsl:key name="trans" match="t" use="@en"/>

<xsl:template match="text()">
  <xsl:variable name="raw" select="."/>
  <xsl:variable name="norm" select="normalize-space($raw)"/>
  <xsl:variable name="whole" select="$mapdoc//t[@en=$norm]"/>
  <xsl:choose>
    <xsl:when test="$whole">
      <xsl:value-of select="string($whole)"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="process-seq"><xsl:with-param name="text" select="$raw"/></xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="process-seq">
  <xsl:param name="text"/>
  <xsl:if test="string-length(normalize-space($text)) &gt; 0">
    <xsl:variable name="firstToken" select="substring-before(concat($text, ' '), ' ')"/>
    <xsl:variable name="afterFirst" select="substring-after($text, ' ')"/>
    <xsl:variable name="secondToken" select="substring-before(concat($afterFirst, ' '), ' ')"/>
    <xsl:variable name="afterSecond" select="substring-after($afterFirst, ' ')"/>
    <xsl:variable name="thirdToken" select="substring-before(concat($afterSecond, ' '), ' ')"/>

    <xsl:variable name="clean1">
      <xsl:call-template name="strip-punct"><xsl:with-param name="s" select="$firstToken"/></xsl:call-template>
    </xsl:variable>
    <xsl:variable name="clean2">
      <xsl:call-template name="strip-punct"><xsl:with-param name="s" select="$secondToken"/></xsl:call-template>
    </xsl:variable>
    <xsl:variable name="clean3">
      <xsl:call-template name="strip-punct"><xsl:with-param name="s" select="$thirdToken"/></xsl:call-template>
    </xsl:variable>

    <xsl:variable name="try3" select="normalize-space(concat($clean1, ' ', $clean2, ' ', $clean3))"/>
    <xsl:variable name="try2" select="normalize-space(concat($clean1, ' ', $clean2))"/>
    <xsl:variable name="try1" select="normalize-space($clean1)"/>

    <xsl:variable name="m3" select="$mapdoc//t[@en=$try3]"/>
    <xsl:variable name="m2" select="$mapdoc//t[@en=$try2]"/>
    <xsl:variable name="m1" select="$mapdoc//t[@en=$try1]"/>

    <xsl:choose>
      <xsl:when test="$m3 and string-length($clean2) &gt; 0 and string-length($clean3) &gt; 0">
        <xsl:value-of select="string($m3)"/>
        <xsl:variable name="restAfter3">
          <xsl:choose>
            <xsl:when test="string-length(normalize-space($afterSecond)) = 0">''</xsl:when>
            <xsl:otherwise><xsl:value-of select="$afterSecond"/></xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:if test="string-length(normalize-space($restAfter3)) &gt; 0">
          <xsl:text> </xsl:text>
          <xsl:call-template name="process-seq"><xsl:with-param name="text" select="$restAfter3"/></xsl:call-template>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$m2 and string-length($clean2) &gt; 0">
        <xsl:value-of select="string($m2)"/>
        <xsl:variable name="restAfter2" select="$afterFirst"/>
        <xsl:if test="string-length(normalize-space($restAfter2)) &gt; 0">
          <xsl:text> </xsl:text>
          <xsl:call-template name="process-seq"><xsl:with-param name="text" select="$restAfter2"/></xsl:call-template>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$m1">
        <xsl:variable name="lead">
          <xsl:call-template name="leading-punct"><xsl:with-param name="s" select="$firstToken"/></xsl:call-template>
        </xsl:variable>
        <xsl:variable name="trail">
          <xsl:call-template name="trailing-punct"><xsl:with-param name="s" select="$firstToken"/></xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$lead"/>
        <xsl:value-of select="string($m1)"/>
        <xsl:value-of select="$trail"/>

        <xsl:if test="string-length(normalize-space($afterFirst)) &gt; 0">
          <xsl:text> </xsl:text>
          <xsl:call-template name="process-seq"><xsl:with-param name="text" select="$afterFirst"/></xsl:call-template>
        </xsl:if>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select="$firstToken"/>
        <xsl:if test="string-length(normalize-space($afterFirst)) &gt; 0">
          <xsl:text> </xsl:text>
          <xsl:call-template name="process-seq"><xsl:with-param name="text" select="$afterFirst"/></xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:template>

<xsl:template name="strip-punct">
  <xsl:param name="s"/>
  <xsl:variable name="first" select="substring($s,1,1)"/>
  <xsl:variable name="punct"><xsl:text>,.;:!?()[]{}&lt;&gt;"'</xsl:text></xsl:variable>
  <xsl:choose>
    <xsl:when test="string-length($s)=0"><xsl:value-of select="''"/></xsl:when>
    <xsl:when test="contains($punct, $first)">
      <xsl:call-template name="strip-punct"><xsl:with-param name="s" select="substring($s,2)"/></xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="strip-trail"><xsl:with-param name="s" select="$s"/></xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="strip-trail">
  <xsl:param name="s"/>
  <xsl:variable name="last" select="substring($s, string-length($s), 1)"/>
  <xsl:variable name="punct"><xsl:text>,.;:!?()[]{}&lt;&gt;"'</xsl:text></xsl:variable>
  <xsl:choose>
    <xsl:when test="string-length($s)=0"><xsl:value-of select="''"/></xsl:when>
    <xsl:when test="contains($punct, $last)">
      <xsl:call-template name="strip-trail"><xsl:with-param name="s" select="substring($s, 1, string-length($s)-1)"/></xsl:call-template>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="$s"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="leading-punct">
  <xsl:param name="s"/>
  <xsl:variable name="first" select="substring($s,1,1)"/>
  <xsl:variable name="punct"><xsl:text>,.;:!?()[]{}&lt;&gt;"'</xsl:text></xsl:variable>
  <xsl:choose>
    <xsl:when test="string-length($s)=0"><xsl:value-of select="''"/></xsl:when>
    <xsl:when test="contains($punct, $first)">
      <xsl:value-of select="$first"/>
      <xsl:call-template name="leading-punct"><xsl:with-param name="s" select="substring($s,2)"/></xsl:call-template>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="trailing-punct">
  <xsl:param name="s"/>
  <xsl:variable name="lastpos" select="string-length($s)"/>
  <xsl:variable name="last" select="substring($s, $lastpos, 1)"/>
  <xsl:variable name="punct"><xsl:text>,.;:!?()[]{}&lt;&gt;"'</xsl:text></xsl:variable>
  <xsl:choose>
    <xsl:when test="string-length($s)=0"><xsl:value-of select="''"/></xsl:when>
    <xsl:when test="contains($punct, $last)">
      <xsl:call-template name="trailing-punct"><xsl:with-param name="s" select="substring($s, 1, $lastpos - 1)"/></xsl:call-template>
      <xsl:value-of select="$last"/>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
