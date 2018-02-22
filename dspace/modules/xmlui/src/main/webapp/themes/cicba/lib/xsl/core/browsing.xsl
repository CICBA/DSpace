<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:xlink="http://www.w3.org/TR/xlink/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="i18n dri mets xlink xsl dim xhtml mods dc">
	<!-- La variable elementsPerColumn contiene la cantidad de elementos por columna que se mostrarán -->
	<xsl:variable name="elementsPerColumn"> 
			<xsl:number value="25" />
	</xsl:variable>
	<!--  este template machea con el div que trae los autores a listar -->
	<xsl:template match="dri:div[@id='aspect.artifactbrowser.ConfigurableBrowse.div.browse-by-author-results' and @n='browse-by-author-results']/dri:table[@id='aspect.artifactbrowser.ConfigurableBrowse.table.browse-by-author-results'] | dri:div[@id='aspect.artifactbrowser.ConfigurableBrowse.div.browse-by-subject-results' and @n='browse-by-subject-results']/dri:table[@id='aspect.artifactbrowser.ConfigurableBrowse.table.browse-by-subject-results']">
		
		<!-- La variable rows contiene la cantidad de autores que se van a mostar -->
		<xsl:variable name="rows"> 
			<xsl:value-of select="@rows" />
		</xsl:variable>
		<!--  la variable cantColumns contiene la cantidad de columnas que yo debo crear -->
		<xsl:variable name="cantColumns">
				 	<xsl:value-of select="ceiling(($rows -1) div $elementsPerColumn)"/>
		</xsl:variable>
		<div class="container">
			<div class="row item-head">
				<xsl:call-template name="createColumns">
               		<xsl:with-param name="columns">
               			<xsl:value-of select="$cantColumns"/>
               		</xsl:with-param>
               		<xsl:with-param name="pos">2</xsl:with-param>
            	</xsl:call-template> 
			</div>
		</div>
	</xsl:template>	
	<!--  Hago este metodo recursivo porque en XSLT 1.0 no se puede realizar un for sobre una cantidad n. Referencia: https://stackoverflow.com/questions/3802235/how-i-can-repeat-an-action-x-times-with-xslt -->
	<xsl:template name="createColumns">
    	<xsl:param name="columns"></xsl:param>
    	<xsl:param name="pos"></xsl:param>
   		<xsl:if test="$columns &gt; 0">
   			<xsl:call-template name="create-divs" >
				<xsl:with-param name="pos-data">
					<xsl:value-of select="$pos"/>
				</xsl:with-param>
			</xsl:call-template>
			
			<xsl:call-template name="createColumns">
               	<xsl:with-param name="columns">
               		<xsl:number value="number($columns)-1"/>
               	</xsl:with-param>
               	<xsl:with-param name="pos">
               		<xsl:number value="number($pos)+$elementsPerColumn"/>
               	</xsl:with-param>
            </xsl:call-template> 
			
    	</xsl:if>
    </xsl:template>
    
	<xsl:template name="create-divs">
		<xsl:param name="pos-data"></xsl:param>
		<div class="col-md-3">
			<xsl:call-template name="loop">
				<xsl:with-param name="count-left">
					<xsl:value-of select="$elementsPerColumn"></xsl:value-of>
				</xsl:with-param>
		 		<xsl:with-param name="pos">
		 			<xsl:value-of select="$pos-data" />
		 		</xsl:with-param>
			</xsl:call-template>				
		</div>
	</xsl:template>
	
	
	<xsl:template name="loop">
	  <xsl:param name="count-left"></xsl:param>
	  <xsl:param name="pos"></xsl:param>
	    <xsl:if test="$count-left &gt; 0 and dri:row[position()=$pos]/dri:cell/dri:xref/@target !=''">
	     <a>
	     	<xsl:attribute name="href">
	     		<xsl:value-of select="dri:row[position()=$pos]/dri:cell/dri:xref/@target"/>
	     	</xsl:attribute>
	     	<xsl:choose>
		     	<xsl:when test="contains(dri:row[position()=$pos]/dri:cell/dri:xref,'(')">
		     		<xsl:value-of select="substring-before(dri:row[position()=$pos]/dri:cell/dri:xref,'(')"/>
		     	</xsl:when>
		     	<xsl:otherwise>
			     	<xsl:value-of select="dri:row[position()=$pos]/dri:cell/dri:xref/text()"/>
		     	</xsl:otherwise>
		     </xsl:choose>
	     	
	     </a>
	     <xsl:value-of select="dri:row[position()=$pos]/dri:cell/text()"/>
	     <br></br>
	     <xsl:call-template name="loop">
	        <xsl:with-param name="count-left">
	        	<xsl:number value="number($count-left)-1" />
	        </xsl:with-param>
	        <xsl:with-param name="pos">
	        	<xsl:number value="number($pos)+1" />
	        </xsl:with-param>
	      </xsl:call-template>
	    </xsl:if>
	</xsl:template>
	
	
	
	
</xsl:stylesheet>