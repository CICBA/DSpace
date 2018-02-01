<!--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

-->
<!--
    Parts of the artifactbrowser which are not
    specific to a single listing or page. These will not
    frequently be adapted in a theme

    Author: art.lowel at atmire.com
    Author: lieven.droogmans at atmire.com
    Author: ben at atmire.com
    Author: Alexey Maslov

-->

<xsl:stylesheet
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    xmlns:dri="http://di.tamu.edu/DRI/1.0/"
    xmlns:mets="http://www.loc.gov/METS/"
    xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
    xmlns:xlink="http://www.w3.org/TR/xlink/"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:atom="http://www.w3.org/2005/Atom"
    xmlns:ore="http://www.openarchives.org/ore/terms/"
    xmlns:oreatom="http://www.openarchives.org/ore/atom/"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xalan="http://xml.apache.org/xalan"
    xmlns:encoder="xalan://java.net.URLEncoder"
    xmlns:confman="org.dspace.core.ConfigurationManager"
    exclude-result-prefixes="i18n dri mets dim xlink xsl xalan encoder confman">

    <xsl:output indent="yes"/>

    <xsl:template match="dri:list[contains(@n, 'community')] | dri:list[contains(@n, 'collection')]">
        <xsl:apply-templates select="dri:head"/>
	    	<ul>
	    		<xsl:apply-templates select="*[not(name()='head')]" mode="summaryList"/>
    		</ul>
    </xsl:template>

<!--     <xsl:template match="dri:list" mode="summaryList"> -->
    <xsl:template match="dri:list[contains(@n, 'community') or contains(@n,'collection')]" mode="summaryList">
        <li>
            <xsl:attribute name="class">
                <xsl:text>ds-artifact-item </xsl:text>
                <xsl:choose>
                    <xsl:when test="position() mod 2 = 0">even </xsl:when>
                    <xsl:otherwise>odd </xsl:otherwise>
                </xsl:choose>
	            <xsl:choose>
	            	<xsl:when test="substring-after(@n, ':') = 'community'">
	            		<xsl:text>community </xsl:text>
	            	</xsl:when>
	            	<xsl:when test="substring-after(@n, ':') = 'collection'">
	                	<xsl:text>collection </xsl:text>
	            	</xsl:when>
	            </xsl:choose>
                <xsl:text>sapling-item</xsl:text>
            </xsl:attribute>

           
           	<xsl:call-template name="communitySummaryList-DIM2">
           		<xsl:with-param name="handle">
           			<xsl:value-of select="substring-before(@n, ':')"/>
           		</xsl:with-param>
           		<xsl:with-param name="title">
           			<xsl:value-of select="dri:list[substring-after(@n, ':') = 'dc.title']"/>
           		</xsl:with-param>
           		<xsl:with-param name="extent">
           			<xsl:value-of select="dri:list[substring-after(@n, ':') = 'dc.format.extent']"/>
           		</xsl:with-param>
           		<xsl:with-param name="abstract">
           			<xsl:value-of select="dri:list[substring-after(@n, ':') = 'dc.description.abstract']"/>
           		</xsl:with-param>
           	</xsl:call-template>
        </li>
    </xsl:template>

    <!--
        The summaryList display type; used to generate simple surrogates for the item involved
    -->

<!--     <xsl:template match="dri:list[substring-after(./@n, ':') = 'collection']|dri:list[substring-after(./@n, ':') = 'community']" mode="summaryList"> -->
<!--     <xsl:template match="dri:list['community-result-list']//*"> -->
<!--     </xsl:template> -->
    
<!--     <xsl:template match="dri:list[contains(@n, community)]|dri:list[contains(@n, collection)]" mode="lista"> -->
<!--     	<xsl:variable name="handle"> -->
<!--     		<xsl:value-of select="substring-before(@n, ':')"/> -->
<!--     	</xsl:variable> -->
<!--     	<xsl:variable name="type"> -->
<!--     		<xsl:value-of select="substring-after(@n, ':')"/> -->
<!--     	</xsl:variable> -->

<!--         <li> -->
<!--             <xsl:attribute name="class"> -->
<!--                 <xsl:text>ds-artifact-item </xsl:text> -->
<!--                 <xsl:choose> -->
<!--                     <xsl:when test="position() mod 2 = 0">even</xsl:when> -->
<!--                     <xsl:otherwise>odd</xsl:otherwise> -->
<!--                 </xsl:choose> -->
<!--             </xsl:attribute> -->
<!--             	<xsl:call-template name="communitySummaryList-DIM2"> -->
<!--             		<xsl:with-param name="handle"> -->
<!--             			<xsl:value-of select="$handle"/> -->
<!--             		</xsl:with-param> -->
<!--             		<xsl:with-param name="title"> -->
<!--             			<xsl:value-of select="dri:list[substring-after(@n, ':') = 'dc.title']"/> -->
<!--             		</xsl:with-param> -->
<!--             		<xsl:with-param name="extent"> -->
<!--             			<xsl:value-of select="dri:list[substring-after(@n, ':') = 'dc.format.extent']"/> -->
<!--             		</xsl:with-param> -->
<!--             		<xsl:with-param name="abstract"> -->
<!--             			<xsl:value-of select="dri:list[substring-after(@n, ':') = 'dc.description.abstract']"/> -->
<!--             		</xsl:with-param> -->
<!--             	</xsl:call-template> -->
<!--         </li> -->
<!--     </xsl:template> -->

    <!-- Generate the logo, if present, from the file section -->
    <xsl:template match="mets:fileGrp[@USE='LOGO']">
        <div class="ds-logo-wrapper">
            <img src="{mets:file/mets:FLocat[@LOCTYPE='URL']/@xlink:href}" class="logo">
                <xsl:attribute name="alt">xmlui.dri2xhtml.METS-1.0.collection-logo-alt</xsl:attribute>
                <xsl:attribute name="attr" namespace="http://apache.org/cocoon/i18n/2.1">alt</xsl:attribute>
            </img>
        </div>
    </xsl:template>
    
</xsl:stylesheet>
