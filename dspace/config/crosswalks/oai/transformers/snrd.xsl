<?xml version="1.0" encoding="UTF-8"?>
<!-- 


    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/
    
	Developed by DSpace @ Lyncode <dspace@lyncode.com> 
	Following Driver Guidelines 2.0:
		- http://www.driver-support.eu/managers.html#guidelines

 -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:doc="http://www.lyncode.com/xoai">
	<xsl:output indent="yes" method="xml" omit-xml-declaration="yes" />
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
	<!-- Formatting dc.type-->  
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='type']">
		<xsl:variable name="type" select="./doc:element/doc:field[@name='value']/text()"/>
		<xsl:call-template name="type-driver">
			<xsl:with-param name="theValue" select="$type"/>				
		</xsl:call-template>
		<xsl:call-template name="type-snrd">
			<xsl:with-param name="theValue" select="$type"/>
		</xsl:call-template>
		<xsl:variable name="version" select="../../doc:element[@name='cic']/doc:element[@name='version']/doc:element/doc:field/text()"/>
		<xsl:call-template name="type-driver-version">
			<xsl:with-param name="theValue" select="$version"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Formatting dc.date.issued--> 
	<xsl:template match="/doc:metadata/doc:element[@name='dcterms']/doc:element[@name='issued']/doc:element/doc:field[@name='value']/text()">
		<xsl:call-template name="formatdate">
			<xsl:with-param name="datestr" select="." />
		</xsl:call-template>
	</xsl:template>
	
	<!-- Removing dc.date.available -->
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='date']/doc:element[@name='available']/doc:field/text()"/> 
		
	<!-- Formatting dc.identifier.uri--> 
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='identifier']/doc:element[@name='uri']/doc:element/doc:field[@name='value']/text()">
		<xsl:variable name="handle" select="/doc:metadata/doc:element[@name='others']/doc:field[@name='handle']"/>
		<xsl:choose>
			<xsl:when test="not(contains(.,'http://digital.cic.gba.gob.ar/'))">
				<xsl:value-of select="concat(.,$handle)"/>	
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Formatting dcterms.identifier.isbn --> 			
	<xsl:template match="/doc:metadata/doc:element[@name='dcterms']/doc:element[@name='identifier']/doc:element[@name='isbn']/doc:field/text()">
		<xsl:call-template name="addPrefix">
			<xsl:with-param name="value" select="."/>
			<xsl:with-param name="prefix" select="'info:eu-repo/semantics/altIdentifier/isbn/'"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--  Formatting dcterms.identifier.other -->
	<xsl:template match="/doc:metadata/doc:element[@name='dcterms']/doc:element[@name='identifier']/doc:element[@name='other']/doc:field[@name='value']/text()">
		<xsl:variable name="prefix" select="'info:eu-repo/semantics/altIdentifier/'"/>
		<xsl:choose>
			<xsl:when test="contains(.,':')">
				<xsl:variable name="esquema" select="substring-before(.,':')"/>
				<xsl:variable name="identificador" select="substring-after(.,':')"/>
				<xsl:value-of select="concat($prefix,$esquema,$identificador)"/>		
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="esquema" select="substring-before(.,' ')"/>
				<xsl:variable name="identificador" select="substring-after(.,' ')"/>
				<xsl:value-of select="concat($prefix,$esquema,$identificador)"/>
			</xsl:otherwise>
		</xsl:choose>
					
	</xsl:template>
	
	
	<!--  Removing dc.description.provenance --> 
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='description']/doc:element[@name='provenance']" />
	
	<!--  Removing cic.isPeerReviewed --> 
	<xsl:template match="/doc:metadata/doc:element[@name='cic']/doc:element[@name='isPeerReviewed']" />
	
	<!--  Removing cic.isFullText -->
	<xsl:template match="/doc:metadata/doc:element[@name='cic']/doc:element[@name='isFullText']" />
	
	<!-- Formatting language to ISO 639-3--> 
	<xsl:template  match="/doc:metadata/doc:element[@name='dcterms']/doc:element[@name='language']/doc:element/doc:field[@name='value']/text()">	
		<xsl:call-template name="snrd-language">
			<xsl:with-param name="value" select="."/>
		</xsl:call-template> 
	</xsl:template>		
	
	<!-- Formatting cic.thesis.degree cic.thesis.grantor -->
	
	<xsl:template match="/doc:metadata/doc:element[@name='cic']/doc:element[@name='thesis']/doc:element[@name='degree']/doc:element/doc:field[@name='value']/text()">
		<xsl:variable name="grantor" select="/doc:metadata/doc:element[@name='cic']/doc:element[@name='thesis']/doc:element[@name='grantor']/doc:element/doc:field[@name='value']/text()"/>
		<xsl:value-of select="concat(.,'(',$grantor,')')"/>
	</xsl:template>
		
	<!-- Prefixing and Modifying dc.rights -->
	<!-- Removing unwanted -->
	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element/doc:element" />
	<!-- Replacing -->
<!-- 	<xsl:template match="/doc:metadata/doc:element[@name='dc']/doc:element[@name='rights']/doc:element/doc:field/text()"> -->
<!-- 		<xsl:text>info:eu-repo/semantics/openAccess</xsl:text> -->
<!-- 	</xsl:template> -->
	


	<!-- AUXILIARY TEMPLATES -->
	
	<xsl:template name="type-driver-version">
		<xsl:param name="theValue" />
		
		<xsl:variable name="finalValue">
			<xsl:choose>
				<xsl:when test="$theValue='info:eu-repo/semantics/submittedVersion'">
					info:eu-repo/semantics/submittedVersion
				</xsl:when>
				<xsl:when test="$theValue='info:eu-repo/semantics/acceptedVersion'">
					info:eu-repo/semantics/acceptedVersion
				</xsl:when>
				<xsl:otherwise>
					info:eu-repo/semantics/publishedVersion
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<doc:element name='type-version'>
			<doc:element name='driver'>				
				<doc:field name="value"><xsl:value-of select="normalize-space($finalValue)"/></doc:field>				
			</doc:element>
		</doc:element>
	</xsl:template>
	
	<xsl:template name="type-driver">
		<xsl:param name="theValue" />
		
		<xsl:variable name="finalValue">
			<xsl:choose>
				<xsl:when test="$theValue ='Documento de trabajo'">
					workingPaper
				</xsl:when>
				<xsl:when test="$theValue ='Articulo'">
					article
				</xsl:when>
				<xsl:when test="$theValue ='Artículo'">
					article
				</xsl:when>
				<xsl:when test="$theValue ='Comunicacion'">
					article
				</xsl:when>
				<xsl:when test="$theValue ='Comunicación'">
					article
				</xsl:when>
				<xsl:when test="$theValue ='Revision'">
					review
				</xsl:when>
				<xsl:when test="$theValue ='Revisión'">
					review
				</xsl:when>
				<xsl:when test="$theValue ='Contribucion a revista'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='Contribución a revista'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='Informe tecnico/Reporte'">
					report
				</xsl:when>
				<xsl:when test="$theValue ='Informe técnico/Reporte'">
					report
				</xsl:when>
				<xsl:when test="$theValue ='Informe de investigador'">
					report
				</xsl:when>
				<xsl:when test="$theValue ='Informe de becario'">
					report
				</xsl:when>
				<xsl:when test="$theValue ='Informe de personal de apoyo'">
					report
				</xsl:when>
				<xsl:when test="$theValue ='Tesis de doctorado'">
					doctoralThesis
				</xsl:when>
				<xsl:when test="$theValue ='Tesis de maestria'">
					masterThesis
				</xsl:when>
				<xsl:when test="$theValue ='Tesis de maestría'">
					masterThesis
				</xsl:when>
				<xsl:when test="$theValue='Trabajo de especializacion'">
					masterThesis
				</xsl:when>
				<xsl:when test="$theValue='Trabajo de especialización'">
					masterThesis
				</xsl:when>
				<xsl:when test="$theValue='Tesis de grado'">
					bachelorThesis
				</xsl:when>
				<xsl:when test="$theValue='Trabajo final de Grado'">
					bachelorThesis
				</xsl:when>
				<xsl:when test="$theValue='Trabajo final de grado'">
					bachelorThesis
				</xsl:when>
				<xsl:when test="$theValue ='Libro'">
					book
				</xsl:when>
				<xsl:when test="$theValue = 'Parte de libro'">
					bookPart
				</xsl:when>
				<xsl:when test="$theValue ='Documento de conferencia'">
					conferenceObject
				</xsl:when>
				<xsl:when test="$theValue ='Resolucion'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='Resolución'">
					other
				</xsl:when>
				<xsl:when test="$theValue='Actas de directorio'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='Legislacion'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='Legislación'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='Convenio'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='fotografía'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='fotografia'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='Audio'">
					other
				</xsl:when>
				<xsl:when test="$theValue ='Video'">
					other
				</xsl:when>
				<xsl:when test="$theValue='Informe tecnico'">
					report
				</xsl:when>
				<xsl:when test="$theValue='Informe técnico'">
					report
				</xsl:when>
				<!-- No se exporta 
				<xsl:when test="$value='Objeto de Aprendizaje'">
					report
				</xsl:when>
				 -->		
			</xsl:choose>
		</xsl:variable>
		<doc:element name="type">
			<doc:element name='driver'>
				<doc:field name="value"><xsl:value-of select="normalize-space($finalValue)"/></doc:field>							
			</doc:element>
		</doc:element>
	</xsl:template>
				
	<xsl:template name="type-snrd">
		<xsl:param name="theValue"/>
		
		<xsl:variable name="finalValue">
			<xsl:choose>
				<xsl:when test="$theValue='Documento de trabajo'">
					documento de trabajo
				</xsl:when>
				<xsl:when test="$theValue='Articulo'">
					artículo
				</xsl:when>
				<xsl:when test="$theValue='Artículo'">
					artículo
				</xsl:when>				
				<xsl:when test="$theValue='Comunicacion'">
					artículo
				</xsl:when>
				<xsl:when test="$theValue='Comunicación'">
					artículo
				</xsl:when>
				<xsl:when test="$theValue='Revision'">
					revisión literaria
				</xsl:when>
				<xsl:when test="$theValue='Revisión'">
					revisión literaria
				</xsl:when>				
				<xsl:when test="$theValue='Contribucion a revista'">
					artículo
				</xsl:when>
				<xsl:when test="$theValue='Contribución a revista'">
					artículo
				</xsl:when>
				 <xsl:when test="$theValue='Informe tecnico/Reporte'">
					informe técnico
				</xsl:when>
				<xsl:when test="$theValue='Informe técnico/Reporte'">
					informe técnico
				</xsl:when>				
				<xsl:when test="$theValue='Informe de investigador'">
					informe técnico
				</xsl:when>
				<xsl:when test="$theValue='Informe de becario'">
					informe técnico
				</xsl:when>
				<xsl:when test="$theValue='Informe de personal de apoyo'">
					informe técnico
 				</xsl:when>
				<xsl:when test="$theValue='Tesis de doctorado'">
					tesis doctoral
				</xsl:when>
				<xsl:when test="$theValue='Tesis de maestria'">
					tesis de maestría
				</xsl:when>
				<xsl:when test="$theValue='Tesis de maestría'">
					tesis de maestría
				</xsl:when>				
				<xsl:when test="$theValue='Trabajo de especializacion'">
					tesis de maestría
				</xsl:when>
				<xsl:when test="$theValue='Trabajo de especialización'">
					tesis de maestría
				</xsl:when>
				<xsl:when test="$theValue='Tesis de grado'">
					tesis de grado
				</xsl:when>
				<xsl:when test="$theValue='Trabajo final de Grado'">
					trabajo final de grado
				</xsl:when>
				<xsl:when test="$theValue='Trabajo final de grado'">
					trabajo final de grado
				</xsl:when>
				<xsl:when test="$theValue='Libro'">
					libro
				</xsl:when>
				<xsl:when test="$theValue='Parte de libro'">
					parte de libro
				</xsl:when>
				<xsl:when test="$theValue='Documento de conferencia'">
					documento de conferencia
				</xsl:when>
				<xsl:when test="$theValue='Informe tecnico'">
					informe técnico
				</xsl:when>
				<xsl:when test="$theValue='Informe técnico'">
					informe técnico
				</xsl:when>
				<xsl:when test="$theValue='Video'">
					videograbación
				</xsl:when>
				<!-- 
				<xsl:when test="$value='Resolucion'">
					other
				</xsl:when>
				<xsl:when test="$theValue='Actas de directorio'">
					other
				</xsl:when>
				<xsl:when test="$value='Legislación'">
					other
				</xsl:when>
				<xsl:when test="$value='Convenio'">
					other
				</xsl:when>
				 -->
				 <xsl:when test="$theValue='fotografia'">
					fotografía
				</xsl:when>
				<xsl:when test="$theValue='fotografía'">
					fotografía
				</xsl:when>
				<!-- 
				<xsl:when test="$value='Audio'">
					other
				</xsl:when>
				 -->
				<!-- No se exporta 
				<xsl:when test="$value='Objeto de Aprendizaje'">
					report
				</xsl:when>
				 -->		
			</xsl:choose>
		</xsl:variable>
		<doc:element name="type">
			<doc:element name='snrd'>
				<doc:field name="value"><xsl:value-of select="normalize-space($finalValue)"/></doc:field>
			</doc:element>
		</doc:element>
	</xsl:template>
	
	<xsl:template name="snrd-language" >		
		<xsl:param name="value"/>
		
		<xsl:variable name="valueLanguage">
			<xsl:choose>
				<xsl:when test="$value='es'">
					spa
				</xsl:when>
				<xsl:when test="$value='Español'">
					spa
				</xsl:when>
				<xsl:when test="$value='en'">
					eng
				</xsl:when>
				<xsl:when test="$value='Inglés'">
					eng
				</xsl:when>
				<xsl:when test="$value='Ingles'">
					eng
				</xsl:when>
				<xsl:when test="$value='pt'">
					por
				</xsl:when>
				<xsl:when test="$value='Portugués'">
					por
				</xsl:when>
				<xsl:when test="$value='Portugues'">
					por
				</xsl:when>
				<xsl:when test="$value='fr'">
					fra
				</xsl:when>
				<xsl:when test="$value='Francés'">
					fra
				</xsl:when>
				<xsl:when test="$value='Frances'">
					fra
				</xsl:when>
				<xsl:when test="$value='it'">
					ita
				</xsl:when>
				<xsl:when test="$value='Italiano'">
					ita
				</xsl:when>
				<xsl:otherwise>
					spa
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>		
			<xsl:value-of select="normalize-space($valueLanguage)"/>	
	</xsl:template>
	
	<!-- dc.type prefixing -->
	<xsl:template name="addPrefix">
		<xsl:param name="value" />
		<xsl:param name="prefix" />
		<xsl:choose>
			<xsl:when test="starts-with($value, $prefix)">
				<xsl:value-of select="$value" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($prefix, $value)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Date format -->
	<xsl:template name="formatdate">
		<xsl:param name="datestr" />
		<xsl:variable name="sub">
			<xsl:value-of select="substring($datestr,1,10)" />
		</xsl:variable>
		<xsl:value-of select="$sub" />
	</xsl:template>
</xsl:stylesheet>
