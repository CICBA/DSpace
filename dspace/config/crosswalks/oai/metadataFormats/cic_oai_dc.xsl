<?xml version="1.0" encoding="UTF-8" ?>
<!-- 


    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/
	Developed by DSpace @ Lyncode <dspace@lyncode.com>
	
	> http://www.openarchives.org/OAI/2.0/oai_dc.xsd

 -->
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:doc="http://www.lyncode.com/xoai"
	version="1.0">
	<xsl:output omit-xml-declaration="yes" method="xml" indent="yes" />
	
	<xsl:template match="/">
		<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" 
			xmlns:dc="http://purl.org/dc/elements/1.1/" 
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
			xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
			<!-- dc.type -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element[@name='driver']/doc:field[@name='value']">
				<dc:type><xsl:value-of select="concat('info:eu-repo/semantics/',.)" /></dc:type>
			</xsl:for-each>
			<xsl:for-each select="doc:metadata/doc:element[@name='dc']/doc:element[@name='type']/doc:element[@name='snrd']/doc:field[@name='value']">
				<dc:type><xsl:value-of select="concat('info:ar-repo/semantics/',.)" /></dc:type>
			</xsl:for-each>
			<!-- cic.version -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dc']/doc:element[@name='type-version']/doc:element[@name='driver']/doc:field[@name='value']">
				<dc:type><xsl:value-of select="." /></dc:type>
			</xsl:for-each>
			<!-- dc.title -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dc']/doc:element[@name='title']/doc:element/doc:field[@name='value']">
				<dc:title><xsl:value-of select="." /></dc:title>
			</xsl:for-each>
			<!-- dcterms.alternative -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='alternative']/doc:element/doc:element/doc:field[@name='value']">
				<dc:title><xsl:value-of select="." /></dc:title>
			</xsl:for-each>
			<!-- dcterms.title.investigation -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='title']/doc:element/doc:field[@name='value']">
				<dc:title><xsl:value-of select="." /></dc:title>
			</xsl:for-each>
			<!-- dcterms.creator.* -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='creator']/doc:element/doc:element/doc:field[@name='value']">
				<dc:creator><xsl:value-of select="." /></dc:creator>
			</xsl:for-each>
			<!-- dc.contributor.director-->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='contributor']/doc:element[@name='director']/doc:element/doc:field[@name='value']">
				<dc:contributor><xsl:value-of select="." /></dc:contributor>
			</xsl:for-each>
			<!-- dcterms.subject.* excepto subject.ford -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='subject']/doc:element[@name!='ford']/doc:element/doc:field[@name='value']">
				<dc:subject><xsl:value-of select="." /></dc:subject>
			</xsl:for-each>
			<!--dcterms.subject.ford imprimo solo la uri de autoridad -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='subject']/doc:element[@name='ford']/doc:element/doc:field[@name='authority']">
				<dc:subject><xsl:value-of select="." /></dc:subject>
			</xsl:for-each>
			<!-- dcterms.abstract -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='abstract']/doc:element/doc:field[@name='value']">
				<dc:description><xsl:value-of select="." /></dc:description>
			</xsl:for-each>
			<!-- dcterms.description-->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='description']/doc:element/doc:field[@name='value']">
				<dc:description><xsl:value-of select="." /></dc:description>
			</xsl:for-each>
			<!-- cic.thesis.degree -->
			<xsl:for-each select="doc:metadata/doc:element[@name='cic']/doc:element[@name='thesis']/doc:element[@name='degree']/doc:element/doc:field[@name='value']">
				<dc:description><xsl:value-of select="." /></dc:description>
			</xsl:for-each>
			<!-- dcterms.issued -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='issued']/doc:element/doc:field[@name='value']">
				<dc:date><xsl:value-of select="." /></dc:date>
			</xsl:for-each>
			<!-- dcterms.created -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='created']/doc:element/doc:field[@name='value']">
				<dc:date><xsl:value-of select="." /></dc:date>
			</xsl:for-each>
			<!-- license.embargoEnd -->
			<xsl:for-each select="doc:metadata/doc:element[@name='others']/doc:field[@name='embargoEnd']">
				<dc:date><xsl:value-of select="." /></dc:date>
			</xsl:for-each>
			<!-- mimetype -->
            <xsl:for-each select="doc:metadata/doc:element[@name='bundles']/doc:element[@name='bundle' and doc:field[@name='name' and text()='ORIGINAL']]/doc:element[@name='bitstreams']/doc:element[@name='bitstream']/doc:field[@name='format']">
                    <dc:format><xsl:value-of select="." /></dc:format>
            </xsl:for-each>
			<!-- dcterms.extent -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='extent']/doc:element/doc:field[@name='value']">
				<dc:format><xsl:value-of select="." /></dc:format>
			</xsl:for-each>
			<!-- dcterms.format -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='format']/doc:element/doc:field[@name='value']">
				<dc:format><xsl:value-of select="." /></dc:format>
			</xsl:for-each>
			<!-- dcterms.medium -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='medium']/doc:element/doc:field[@name='value']">
				<dc:format><xsl:value-of select="." /></dc:format>
			</xsl:for-each>
            <!--  dcterms.spatial -->
            <xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='spatial']/doc:element/doc:field[@name='value']">
                <dc:coverage><xsl:value-of select="." /></dc:coverage>
            </xsl:for-each>
            <!--  dcterms.temporal -->
            <xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='temporal']/doc:element/doc:field[@name='value']">
                <dc:coverage><xsl:value-of select="." /></dc:coverage>
            </xsl:for-each>
			<!-- dcterms.publisher -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='publisher']/doc:element/doc:field[@name='value']">
				<dc:publisher><xsl:value-of select="." /></dc:publisher>
			</xsl:for-each>			
			<!-- dc.identifier.uri -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dc']/doc:element[@name='identifier']/doc:element[@name='uri']/doc:element/doc:field[@name='value']">
				<dc:identifier><xsl:value-of select="." /></dc:identifier>
			</xsl:for-each>
			<!-- dcterms.identifier.url -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='identifier']/doc:element[@name='url']/doc:element/doc:field[@name='value']">
				<dc:identifier><xsl:value-of select="." /></dc:identifier>
			</xsl:for-each>
			<!-- dcterms.language -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='language']/doc:element/doc:field[@name='value']">
				<dc:language><xsl:value-of select="." /></dc:language>
			</xsl:for-each>
			<!-- dcterms.identifier.isbn -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='identifier']/doc:element[@name='isbn']/doc:field[@name='value']">
				<dc:relation><xsl:value-of select="." /></dc:relation>
			</xsl:for-each>
			<!-- dcterms.identifier.other -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='identifier']/doc:element[@name='other']/doc:field[@name='value']">
				<dc:relation><xsl:value-of select="." /></dc:relation>
			</xsl:for-each>
<!-- 			 dc.rights.* -->
<!-- 			<xsl:for-each select="doc:metadata/doc:element[@name='others']/doc:field[@name='accessRights']"> -->
<!-- 				<dc:rights><xsl:value-of select="." /></dc:rights> -->
<!-- 			</xsl:for-each> -->
			<!-- dc.rights -->
			<!-- Como solo mostramos items OPEN, esto se harcodea -->
			<dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
			<!-- dcterms.license -->
			<xsl:for-each select="doc:metadata/doc:element[@name='dcterms']/doc:element[@name='license']/doc:element/doc:field[@name='value']">
				<dc:rights><xsl:value-of select="." /></dc:rights>
			</xsl:for-each>
		</oai_dc:dc>
	</xsl:template>
</xsl:stylesheet>
