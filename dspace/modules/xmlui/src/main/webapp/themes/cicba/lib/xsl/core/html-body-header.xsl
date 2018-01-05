<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/" xmlns:mets="http://www.loc.gov/METS/"
	xmlns:xlink="http://www.w3.org/TR/xlink/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:confman="org.dspace.core.ConfigurationManager"
	xmlns:str="http://exslt.org/strings"
	xmlns:xmlui="xalan://ar.edu.unlp.sedici.dspace.xmlui.util.XSLTHelper"
	xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="i18n dri mets xlink xsl str dim xhtml mods dc confman">

	<!-- Display language selection if more than 1 language is supported -->
	<xsl:template name="languageSelection">
		<xsl:variable name="currentLocale"
			select="//dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='page'][@qualifier='currentLocale']" />
		<a class="dropdown-toggle text-uppercase" id="dropdownMenuLocale"
			data-toggle="dropdown">
			<xsl:value-of select="$currentLocale" />
			<span class="caret"></span>
		</a>
		<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenuLocale">
			<xsl:for-each
				select="//dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='page'][@qualifier='supportedLocale']">
				<xsl:variable name="locale" select="." />
				<xsl:variable name="queryString" select="xmlui:replaceAll(/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='request'][@qualifier='queryString']/text(), '\blocale-attribute=(en&amp;?|es&amp;?)', '')"/>
				<li role="presentation">
					<xsl:if test="$locale = $currentLocale">
						<xsl:attribute name="class">active</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="build-anchor">
						<xsl:with-param name="a.href">
							<xsl:choose>
								<xsl:when test="starts-with($request-uri, 'page/')">
									<xsl:value-of select="xmlui:replaceAll($request-uri, '(_en|_es)', concat('_', $locale))" />							
								</xsl:when>
								
								<!-- En el caso de admin/groups, en algunos casos al momento de cambiar el idioma se 
								redirecciona a admin/group/(main | edit | delet ...) en lugar de admin/groups, y no se 
								encuentra el recurso. para salvar este caso se parsea la uri recibida 
								(mas informacion en el ticket 3815)-->
								<xsl:when test="starts-with($request-uri, 'admin/')">
									<xsl:variable name="section">
										<xsl:choose>
											<xsl:when test="starts-with(substring-before(substring-after($request-uri, 'admin/'), '/'), 'group')">
												<xsl:value-of select="xmlui:replaceAll(substring-before(substring-after($request-uri, 'admin/'), '/'), 'group','groups')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="substring-before(substring-after($request-uri, 'admin/'), '/')"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:variable>
									<!-- <xsl:choose>
										<xsl:when test="starts-with(substring-before(substring-after($request-uri, 'admin/'), '/'), 'group')">
											<xsl:variable name="section" select="xmlui:replaceAll(substring-before(substring-after($request-uri, 'admin/'), '/'), 'group','groups')"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:variable name="section" select="substring-before(substring-after($request-uri, 'admin/'), '/')"/>
										</xsl:otherwise>
									</xsl:choose> -->
									<xsl:choose>
										<xsl:when test="$section != ''">
											<xsl:value-of select="concat('admin/', $section)"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="$request-uri"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$current-uri" />
								</xsl:otherwise>
							</xsl:choose>
							<xsl:text>?locale-attribute=</xsl:text>
							<xsl:value-of select="$locale" />
							<xsl:if test="$queryString != '' ">
								<xsl:value-of select="concat('&amp;', $queryString)"/>
							</xsl:if>
						</xsl:with-param>
						<xsl:with-param name="a.value">
							<xsl:value-of
								select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='supportedLocale'][@qualifier=$locale]" />
						</xsl:with-param>
					</xsl:call-template>
				</li>
			</xsl:for-each>

		</ul>

	</xsl:template>

	<!-- The header (distinct from the HTML head element) contains the title, 
		subtitle, login box and various placeholders for header images -->
	<xsl:template name="buildTopSidebar">
			<nav class="navbar navbar-inverse" role="navigation" id="cic-menu">
				<div class="navbar-header" id="navbar-brand-dspace">
				      <div class="row">
						  <div class="col-xs-2">
					          <button type="button" id="navbar-toggle-button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-label="menu">
						        <span class="sr-only">Toggle navigation</span>
						        <span class="glyphicon glyphicon-menu-hamburger" aria-hidden="true"></span>
						      </button>
					      </div>
				      </div>
				    </div>
				<!-- Collect the nav links, forms, and other content for toggling -->
 				<div id="bs-example-navbar-collapse-1" class="collapse navbar-collapse" >
					<div class="container">
						<ul class="nav navbar-nav row">
							<li class="col">
								<xsl:call-template name="build-anchor">
									<xsl:with-param name="a.href">/</xsl:with-param>
									<xsl:with-param name="a.value">
										<i18n:text>xmlui.general.home</i18n:text>
									</xsl:with-param>
								</xsl:call-template>
							</li>
							<xsl:for-each select="/dri:document/dri:options/dri:list[@n='browse']">
								<xsl:call-template  name="buildMenuItemAsList"   />
							</xsl:for-each>
							
							<li class="col">
								<xsl:call-template name="build-anchor">
									<xsl:with-param name="a.href">/submissions</xsl:with-param>
									<xsl:with-param name="a.value">
										<i18n:text>xmlui.cicdigital.home.aportar-material</i18n:text>
									</xsl:with-param>
								</xsl:call-template>
							</li>
							
							<li class="dropdown col">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown"
									role="button" aria-expanded="false">
									<i18n:text>xmlui.cicdigital.home.mas-informacion</i18n:text>
									<span class="caret"></span>
								</a>
								<ul class="dropdown-menu" role="menu">
									<li class="dropdown-header">
										<i18n:text>xmlui.cicdigital.home.sobre-repositorio</i18n:text>
									</li>
									<li>
										<a href="xmlui.cicdigital.staticPage.what-is-cic-digital.uri" i18n:attr="href"/>
											<!-- <xsl:attribute name="href">
												<xsl:value-of select="concat($context-path,'/page/que-es-cic-digital')"></xsl:value-of>
											</xsl:attribute>  -->
											<i18n:text>xmlui.cicdigital.title.que-es-cic-digital</i18n:text>
									</li>
									<li class="col">
										<a href="xmlui.cicdigital.staticPage.reposiotry-policies.uri" i18n:attr="href"/>
											<!--<xsl:attribute name="href">
												<xsl:value-of select="concat($context-path,'/page/politicas-del-repositorio')"></xsl:value-of>
											</xsl:attribute> -->
											<i18n:text>xmlui.cicdigital.title.politicas-del-repositorio</i18n:text>	
									</li>
									<li class="divider col"></li>
									<li class="dropdown-header col">
										<i18n:text>xmlui.cicdigital.home.informacion-autores</i18n:text>
									</li>
									<li class="col">
										<a href="xmlui.cicdigital.staticPage.how-to-contribute-material.uri" i18n:attr="href"/>
											<!-- <xsl:attribute name="href">
												<xsl:value-of select="concat($context-path,'/page/como-aportar-material')"></xsl:value-of>
											</xsl:attribute>-->
											<i18n:text>xmlui.cicdigital.title.como-aportar-material</i18n:text>	 
									</li>
									<li class="col">
										<a>
											<xsl:attribute name="href">
												<xsl:value-of select="concat($context-path,'/register')"></xsl:value-of>
											</xsl:attribute>
											<i18n:text>xmlui.EPerson.Navigation.register</i18n:text>
										</a>
									</li>
								</ul>
							</li>
							<li class="col">
								<xsl:call-template name="build-anchor">
									<xsl:with-param name="a.href">/feedback</xsl:with-param>
									<xsl:with-param name="a.value">
										<i18n:text>xmlui.dri2xhtml.structural.contact-link</i18n:text>
									</xsl:with-param>
								</xsl:call-template>
							</li>
							
						</ul>

						<ul class="nav navbar-nav navbar-right row">
							<li class="col">
								<a>
									<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
								</a>
							</li>
							<xsl:for-each select="/dri:document/dri:options/dri:list[@n!='browse' and @n!='discovery']">
								<xsl:if test="count(child::*) &gt; 0">
									<xsl:call-template  name="buildMenuItemAsTree"   />
								</xsl:if>
							</xsl:for-each>
							<li class="col">
								<xsl:call-template name="languageSelection" />

							</li>
						</ul>
					
					</div>		
				</div><!-- /.navbar-collapse -->
			</nav>
	</xsl:template>

	<xsl:template name="buildMenuItemAsList">
								
		<li class="dropdown">
			<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button"
				aria-expanded="false">
				<xsl:copy-of select="dri:head/*" />
				<span class="caret"></span>
			</a>

			<ul class="dropdown-menu" role="menu">
				<xsl:for-each select="dri:item/dri:xref">
					<li>
						<xsl:call-template name="build-anchor">
							<xsl:with-param name="a.href" select="@target" />
							<xsl:with-param name="a.value" select="*" />
						</xsl:call-template>
					</li>
				</xsl:for-each>
				<xsl:for-each select="dri:list">
					<xsl:if test="count(dri:item) &gt; 0">
						<li class="dropdown-header"><xsl:copy-of select="dri:head/*" /></li>
					</xsl:if>
					<xsl:for-each select="dri:item/dri:xref">
						<li>
							<xsl:call-template name="build-anchor">
								<xsl:with-param name="a.href" select="@target" />
								<xsl:with-param name="a.value" select="node()" />
							</xsl:call-template>
						</li>
					</xsl:for-each>
				</xsl:for-each>
			</ul>
		</li>
	</xsl:template>

	<xsl:template name="buildMenuItemAsTree">
								
		<li class="dropdown">
			<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button"
				aria-expanded="false">
				<xsl:copy-of select="dri:head/*" />
				<span class="caret"></span>
			</a>

			<ul class="dropdown-menu" role="menu">
				<xsl:for-each select="dri:item/dri:xref">
					<li>
						<xsl:call-template name="build-anchor">
							<xsl:with-param name="a.href" select="@target" />
							<xsl:with-param name="a.value" select="*" />
						</xsl:call-template>
					</li>
				</xsl:for-each>
				<xsl:for-each select="dri:list">
					<xsl:if test="count(dri:item) &gt; 0">
						<li class="dropdown-submenu">
							<a tabindex="-1" href="#"><xsl:copy-of select="dri:head/*" /></a>
							<ul class="dropdown-menu" role="menu">
								<xsl:for-each select="dri:item/dri:xref">
									<li>
										<xsl:call-template name="build-anchor">
											<xsl:with-param name="a.href" select="@target" />
											<xsl:with-param name="a.value" select="node()" />
										</xsl:call-template>
									</li>
								</xsl:for-each>
							</ul>
						</li>
					</xsl:if>
				</xsl:for-each>
			</ul>
		</li>
	</xsl:template>

	<!-- The header (distinct from the HTML head element) contains the title, 
		subtitle, login box and various placeholders for header images -->
	<xsl:template name="buildHeader">
		<header>
		<div id="cic-header" class="container">
		
			<div class="row text-center">
				<span id="logo-cic-digital">
				<xsl:call-template name="build-anchor">
					<xsl:with-param name="img.src">images/Header_cic.png</xsl:with-param>
					<xsl:with-param name="img.alt">CIC-DIGITAL</xsl:with-param>
					<xsl:with-param name="img.class">img-responsive inline-element</xsl:with-param>
				</xsl:call-template>
			</span> 
				<span id="banner-cic-digital"  class="hidden-xs">
				<xsl:call-template name="build-anchor">
					<xsl:with-param name="img.src">images/Header_cic2.png</xsl:with-param>
					<xsl:with-param name="img.alt">CIC-DIGITAL</xsl:with-param>
				</xsl:call-template>
			</span> 
			</div>
		</div>
		</header>
			<xsl:call-template name="buildTopSidebar" />
	</xsl:template>
</xsl:stylesheet>