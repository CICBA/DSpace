<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:xlink="http://www.w3.org/TR/xlink/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:confman="org.dspace.core.ConfigurationManager"
	xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="i18n dri mets xlink xsl dim xhtml mods dc confman">


	<!-- Like the header, the footer contains various miscellaneous text, links, 
		and image placeholders -->
	<xsl:template name="buildFooter">
	<footer class="footer container">
		<div class="row text-center" id="cic-footer">
			<div class="col-md-3">
			<xsl:call-template name="build-anchor">
					<xsl:with-param name="a.href">
						<xsl:text>http://sedici.unlp.edu.ar</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="img.src"><xsl:text>images/logo_sedici_footer.png</xsl:text></xsl:with-param>
					<xsl:with-param name="img.alt"><xsl:text>SEDICI</xsl:text></xsl:with-param>
				</xsl:call-template>
			</div>
			<div class="col-md-6">
				<address>
					<strong>Comisión de Investigaciones Científicas</strong>
					<br />
					<i18n:text>mxlui.dri2xhtml.structural.cc-street</i18n:text> 526 <i18n:text>mxlui.dri2xhtml.structural.cc-between</i18n:text> 10 y 11
					<br />
					CP: 1900 - La Plata - Buenos Aires - Argentina
					<br />
					<a href="mailto:#">repositorio@cic.gba.gob.ar</a>
					<br />
					
					<abbr title="Phone"><i18n:text>mxlui.dri2xhtml.structural.cc-phone</i18n:text></abbr>
					+54 (0221) 423 6696/6677 (int. 141) (CIC-DIGITAL)
					<br />
					<abbr title="Phone"><i18n:text>mxlui.dri2xhtml.structural.cc-phone</i18n:text></abbr>
					+54 (0221) 421-7374 / 482-3795 (CIC-Central)
				</address>
			</div>

			<div class="col-md-3">
				<xsl:call-template name="build-anchor">
					<xsl:with-param name="a.href">
						<xsl:text>http://www.cic.gba.gob.ar</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="img.src"><xsl:text>images/logo_cic_footer.png</xsl:text></xsl:with-param>
					<xsl:with-param name="img.alt"><xsl:text>CIC</xsl:text></xsl:with-param>
					<xsl:with-param name="img.class">img-responsive inline-element</xsl:with-param>
				</xsl:call-template>
			</div>
		
		</div>
	</footer>
	</xsl:template>
</xsl:stylesheet>