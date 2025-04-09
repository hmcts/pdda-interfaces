<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="https://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" omit-xml-declaration="yes" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="https://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />
    <xsl:include href="currentCourtStatus.xsl"/>
    <xsl:template match="/" xmlns="https://www.w3.org/1999/xhtml">
        <html xmlns="https://www.w3.org/1999/xhtml">
            <head>
				<xsl:variable name="crtName2">
					<xsl:call-template name="TitleCase">
						<xsl:with-param name="text" select="currentcourtstatus/court/courtname"/>
					</xsl:call-template>
				</xsl:variable>
                <title>XHIBIT: <xsl:value-of select="$crtName2" /></title>
                <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" /> 
                <xsl:apply-templates select="currentcourtstatus/datetimestamp"/>
                <link href="https://www.justice.gov.uk/css/global.css" rel="stylesheet" type="text/css"/> 
				<link href="https://www.justice.gov.uk/css/framework.css" rel="stylesheet" type="text/css"/> 
				<link href="https://www.justice.gov.uk/css/typo.css" rel="stylesheet" type="text/css"/> 
				<link href="https://www.justice.gov.uk/css/practitioner.css" rel="stylesheet" type="text/css"/> 
				<link href="https://www.justice.gov.uk/css/print-listings.css" media="print" rel="stylesheet" type="text/css" />
				<xsl:comment><!--[if IE 7]>
						<link rel="stylesheet" type="text/css" href="https://www.justice.gov.uk/css/ie7.css" />
				<![endif]--> 
				<!--[if IE 6]>
						<link rel="stylesheet" type="text/css" href="https://www.justice.gov.uk/css/ie6.css" />
				<![endif]--></xsl:comment>
				<link href="https://www.justice.gov.uk/css/xhibit.css" rel="stylesheet" type="text/css" />
            </head>
			<body id="practitioner">
				<div id="container"> 
					<div id="header"> 
						<div id="logo">
							<a accesskey="1" href="https://www.justice.gov.uk/index.htm">
								<img src="https://www.justice.gov.uk/images/logo-guidance.gif" alt="Justice homepage" />
							</a>
						</div> 
						<div id="search-box"> 
							<form action="https://sitesearch.justice.gov.uk.openobjects.com/kb5/justice/justice/results.page"> 
								<div class="search-section"><label for="sso">Search this section only</label><input type="checkbox" name="ha"  id="sso"  value="guidance" /></div> 
								<div class="search-input"> 
									<input type="text" size="49" name="qt" /> 
									<input type="submit" value="Search" onclick="" /> 
								</div> 
							</form> 
							<ul> 
								<li><a href="https://www.justice.gov.uk/global/a-z/a.htm">A-Z</a></li> 
								<li><a href="https://www.justice.gov.uk/about/justice/index.htm">About justice</a></li> 
								<li><a href="https://www.justice.gov.uk/global/contacts/">Contact</a></li> 
								<li><a href="https://www.justice.gov.uk/global/foi-requests/index.htm">Freedom of information</a></li> 
								<li><a href="https://www.justice.gov.uk/global/help/">Help</a></li> 
								<li class="list-last"><a href="https://www.justice.gov.uk/global/subscribe/index.htm">Subscribe</a></li> 
							</ul> 
						</div> 
						<div class="clear"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></div> 
						<ul class="head_nav"> 
							<li class="home-nav" title="Home"><a href="https://www.justice.gov.uk/index.htm">Home</a></li> 
							<li class="about-nav" title="Organisations"><a href="https://www.justice.gov.uk/about/">Organisations</a></li> 
							<li class="news-nav" title="News"><a href="https://www.justice.gov.uk/news/">News</a></li> 
							<li class="pub-nav" title="Reports and data"><a href="https://www.justice.gov.uk/publications/">Reports and data</a></li> 
							<li class="pra-nav" title="Guidance"><a href="https://www.justice.gov.uk/guidance/" class="top-on">Guidance</a></li> 
							<li class="forms-nav" title="Forms"><a href="https://www.justice.gov.uk/global/forms/index.htm">Forms</a></li> 
							<li class="con-nav" title="Consultations"><a href="https://www.justice.gov.uk/consultations/">Consultations</a></li> 
							<li class="jobs-nav" title="Jobs"><a href="https://www.justice.gov.uk/jobs/">Jobs</a></li> 
						</ul> 
					</div> 
					<div id="page-content"> 
						<div id="left-column"> 
							<xsl:variable name="crtName2">
								<xsl:call-template name="TitleCase">
									<xsl:with-param name="text" select="currentcourtstatus/court/courtname"/>
								</xsl:call-template>
							</xsl:variable>
							<ul class="nav">
								<li class="anc-first"><a href="https://www.justice.gov.uk/guidance/index.htm">Guidance</a></li>
								<li class="anc"><a href="https://www.justice.gov.uk/guidance/courts-and-tribunals/index.htm">Courts and Tribunals</a></li>
								<li class="anc"><a href="https://www.justice.gov.uk/guidance/courts-and-tribunals/courts/index.htm">Courts</a></li>
								<li class="anc"><a href="https://www.justice.gov.uk/guidance/courts-and-tribunals/courts/xhibit.htm">XHIBIT</a></li>
								<li class="anc"><a href="https://www.justice.gov.uk/guidance/courts-and-tribunals/courts/court_lists.htm">Court lists</a></li>
								<li class="on"><a href="#"><xsl:value-of select="$crtName2" /></a></li>
							</ul> 
						</div> 
						<div id="content-column">
							<xsl:apply-templates select="currentcourtstatus">
								<xsl:with-param name="language">cy</xsl:with-param>
							</xsl:apply-templates>
						</div>
						<div id="right-column"> 
							<xsl:variable name="pageName" select="currentcourtstatus/pagename"/>
							<a title="view English" class="welsh-switch" href="{$pageName}.htm">View English</a>
							<div class="panel nav-widget hmcts"> 
								<div class="panel-img"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></div> 
								<h3>Responsible for the administration of justice in courts and tribunals in partnership with the judiciary</h3> 
								<ul class="arrow-list"> 
									<li><a href="https://www.justice.gov.uk/global/contacts/hmcts/index.htm" title="Contact">Contact</a></li> 
									<li><a href="https://www.justice.gov.uk/about/hmcts/index.htm" title="About">About</a></li> 
								</ul> 
								<div class="clear"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></div>
							</div>
						</div>
					</div>
					<div class="clear"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></div>
					<div id="footer"> 
						<div id="footer-top"> 
							<ul id="footer-top-list"> 
								<li><a href="https://www.justice.gov.uk/global/help/accessibility.htm">Accessibility</a></li> 
								<li><a href="https://www.justice.gov.uk/global/privacy/index.htm">Privacy</a></li>
								<li><a href="https://www.justice.gov.uk/global/welsh-language-scheme/index.htm">Welsh language policy</a></li>
								<li><a href="https://www.justice.gov.uk/global/copyright/index.htm"><xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> Copyright</a></li>
								<li class="list-last"> Updated August, 2011</li>
							</ul> 
							<div class="clear"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></div>  
						</div> 
						<div id="footer-bottom">
							<ul class="footer-bottom-list">
								<li class="footer-list-title"><a href="https://www.justice.gov.uk/about/index.htm">Organisations</a></li>
								<li><a href="https://www.justice.gov.uk/about/moj/index.htm">Ministry of Justice</a></li>
								<li><a href="https://www.justice.gov.uk/about/hmcts/index.htm">HM Courts and Tribunals Service</a></li>
								<li><a href="https://www.justice.gov.uk/about/yjb/index.htm">Youth Justice Board</a></li>
								<li><a href="https://www.justice.gov.uk/about/noms/index.htm">National Offender Management Service</a></li>
								<li><a href="https://www.justice.gov.uk/about/all.htm">Full list of justice bodies</a></li>
							</ul>      
							<ul class="footer-bottom-list">
								<li class="footer-list-title"><a href="https://www.justice.gov.uk/publications/index.htm">Reports and data</a></li>
								<li><a href="https://www.justice.gov.uk/publications/statistics-and-data/index.htm">Statistics and data</a></li>
								<li><a href="https://www.justice.gov.uk/publications/corporate-reports/index.htm">Corporate reports</a></li>
								<li><a href="https://www.justice.gov.uk/publications/inspectorate-reports/index.htm">Reports of independent inspectorates</a></li>
								<li><a href="https://www.justice.gov.uk/publications/research-and-analysis/index.htm">Research and analysis</a></li>
								<li><a href="https://www.justice.gov.uk/publications/bills-and-acts/index.htm">Bills and Acts</a></li>
								<li><a href="https://www.justice.gov.uk/publications/policy/index.htm">Policy</a></li>
							</ul>
							<ul class="footer-bottom-list">
								<li class="footer-list-title"><a href="https://www.justice.gov.uk/guidance/index.htm">Guidance</a></li>
								<li><a href="https://www.justice.gov.uk/guidance/courts-and-tribunals/index.htm">Going to a court or tribunal</a></li>
								<li><a href="https://www.justice.gov.uk/guidance/prison-probation-and-rehabilitation/index.htm">Prison, probation, rehabilitation</a></li>
								<li><a href="https://www.justice.gov.uk/guidance/youth-justice/index.htm">Youth justice</a></li>
								<li><a href="https://www.justice.gov.uk/guidance/inspection-and-monitoring/index.htm">Inspection and monitoring</a></li>
								<li><a href="https://www.justice.gov.uk/guidance/making-and-reviewing-the-law/index.htm">Making and reviewing the law</a></li>
							</ul>
							<ul class="footer-bottom-list-last">
								<li class="footer-list-title"><a href="https://www.justice.gov.uk/consultations/index.htm">Consultations</a></li>
								<li><a href="https://www.justice.gov.uk/consultations/index.htm">Open</a></li>
								<li><a href="https://www.justice.gov.uk/consultations/closed-awaiting-response.htm">Closed awaiting response</a></li>
								<li><a href="https://www.justice.gov.uk/consultations/closed-with-response.htm">Closed with response</a></li>
							</ul>
							<div class="clear"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></div>
						</div>
					</div> 
					<script type="text/javascript" src="https://www.justice.gov.uk/scripts/jquery-1.4.js"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></script> 
					<script type="text/javascript" src="https://www.justice.gov.uk/scripts/general.js"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></script> 
					<script type="text/javascript" src="https://www.justice.gov.uk/scripts/icons.js"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></script> 
					<script type="text/javascript" src="https://www.justice.gov.uk/scripts/cookies.js"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></script> 
					<script type="text/javascript"> 
					 var _gaq = _gaq || [];
					 _gaq.push(['_setAccount', 'UA-7607492-6']);
					 _gaq.push(['_trackPageview']);
					 (function() {
					   var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
					   ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
					   var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
					 })();
					</script> 
				</div>
			</body>
		</html>
	</xsl:template>
    
    <!-- Template to display current date time info -->
    <xsl:template match="currentcourtstatus/datetimestamp" xmlns="https://www.w3.org/1999/xhtml">
        <xsl:param name="datePublished">today</xsl:param>
        <meta name="DC.title" content="XHIBIT Daily Court Status" /> 
		<meta name="revisit-after" content="1 days" /> 
		<meta name="Author" content="MOJ" /> 
		<meta name="eGMS.accessibility" scheme="WCAG" content="Double-A" /> 
		<meta name="keywords" content="XHIBIT, Court lists, Justice, courts, prisons, probation, criminal law, sentencing, youth justice, offender management, criminal justice reform, " /> 
		<meta name="DC.subject.keyword" content="XHIBIT, Court lists, justice, courts, prisons, probation, criminal law, sentencing, youth justice, offender management, criminal justice reform, " /> 
		<meta name="description" content="XHIBIT improves the daily business of every Crown Court in England and Wales by providing hearing information to those who need it within minutes." /> 
		<meta name="DC.description" content="XHIBIT improves the daily business of every Crown Court in England and Wales by providing hearing information to those who need it within minutes." /> 
		<meta name="DC.creator" content="MOJ" /> 
		<meta name="DC.contributor" content="MOJ" /> 
		<meta name="DC.identifier" content="/" /> 
		<meta name="DC.date.created" content="2011-04-03" /> 
		<meta name="DC.date.modified" content="2011-04-03" /> 
		<meta name="DC.publisher" content="MOJ" /> 
		<meta name="eGMS.subject.category" scheme="GCL" content="Crime, Law, Justice and Rights" /> 
		<meta name="DC.format" content="Text/HTML" /> 
		<meta name="DC.language" content="ENG" /> 
		<meta name="DC.coverage" content="England, Wales" /> 
		<meta name="DC.rights.copyright" content="Crown Copyright" /> 
    </xsl:template>
</xsl:stylesheet>