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
    xmlns:util="org.dspace.app.xmlui.utils.XSLUtils"
    xmlns:confman="org.dspace.core.ConfigurationManager"
    exclude-result-prefixes="i18n dri mets dim xlink xsl xalan encoder confman">

    <xsl:output indent="yes"/>

    <xsl:template match="dri:list[@n = 'community-result-list']" priority="2">
        <xsl:apply-templates select="dri:head"/>
                <ul>
                    <xsl:apply-templates select="*[not(name()='head')]" mode="summaryList"/>
                </ul>
    </xsl:template>

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
		                <xsl:text>sapling-item</xsl:text>
	            	</xsl:when>
	            	<xsl:when test="substring-after(@n, ':') = 'collection'">
	                	<xsl:text>collection </xsl:text>
	            	</xsl:when>
	            </xsl:choose>
            </xsl:attribute>
           
           	<xsl:call-template name="renderSummaryList">
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
	        	<xsl:if test="dri:list[contains(@n, 'sub-collections')]">
			        <ul>
				       	<xsl:apply-templates select="*[contains(@n, 'sub-collections')]/*" mode="summaryList"/>
				    </ul>
	            </xsl:if>
	        	<xsl:if test="dri:list[contains(@n, 'sub-communities')]">
			        <ul>
				       	<xsl:apply-templates select="*[contains(@n, 'sub-communities')]/*" mode="summaryList"/>
				    </ul>
	            </xsl:if>


        </li>
    </xsl:template>

    <xsl:template name="renderSummaryList">
            <xsl:param name="handle"/>
            <xsl:param name="title"/>
            <xsl:param name="extent"/>
            <xsl:param name="abstract"/>
            <div class="artifact-description">
                <div class="artifact-title">
                    <a href="{$handle}">
                        <span class="Z3988">
                            <xsl:choose>
                                <xsl:when test="string-length($title) &gt; 0">
                                    <xsl:value-of select="$title"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <i18n:text>xmlui.dri2xhtml.METS-1.0.no-title</i18n:text>
                                </xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </a>
<!-- 		                    Display community strengths (item counts) if they exist -->
                    <xsl:if test="string-length($extent) &gt; 0">
                        <xsl:text> [</xsl:text>
                        <xsl:value-of select="$extent"/>
                        <xsl:text>]</xsl:text>
                    </xsl:if>
                </div>
                <xsl:if test="string-length($abstract) &gt; 0">
                    <div class="artifact-info">
                        <span class="short-description">
                            <xsl:value-of select="util:shortenString($abstract, 220, 10)"/>
                        </span>
                    </div>
                </xsl:if>   
            </div>
	</xsl:template>

    
</xsl:stylesheet>
