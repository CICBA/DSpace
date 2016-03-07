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
		<div class="row" id="cic-menu">
			<nav class="navbar navbar-inverse" role="navigation">
				<div class="container-fluid">
				<div class="row">
					<div class="navbar-header" id="navbar-brand-dspace">
				      <div class="col-xs-2">
					      <div class="row">
						      <button type="button" id="navbar-toggle-button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-label="menu">
						        <span class="sr-only">Toggle navigation</span>
						        <span class="glyphicon glyphicon-menu-hamburger" aria-hidden="true"></span>
		<!-- 					      <xsl:call-template name="build-img"> -->
		<!-- 					      	<xsl:with-param name="img.src">images/dspace-logo-only.png</xsl:with-param> -->
		<!-- 					      	<xsl:with-param name="img.alt">DSpace</xsl:with-param> -->
		<!-- 					      </xsl:call-template> -->
						      </button>
					      </div>
				      </div>
<!-- 				      <div class="col-xs-9 col-xs-pull-3"> -->
<!-- 				      <a class="navbar-brand visible-xs" href="#"> -->
<!-- 				      	Repositorio CIC-DIGITAL -->
<!-- 					  </a> -->
<!-- 					  </div> -->
				    </div>
					<!-- Collect the nav links, forms, and other content for toggling -->
 					<div id="bs-example-navbar-collapse-1" class="collapse navbar-collapse" >
						<ul class="nav navbar-nav">
<!-- 							<li class="link-ba"> -->
<!-- 							    <xsl:call-template name="build-anchor"> -->
<!-- 									<xsl:with-param name="a.href">http://www.gba.gob.ar</xsl:with-param> -->
<!-- 									<xsl:with-param name="img.src">images/header_ba-10.png</xsl:with-param> -->
<!-- 									<xsl:with-param name="img.alt">BA</xsl:with-param> -->
<!-- 								</xsl:call-template> -->
<!-- 							</li> -->
							<li>
								<xsl:call-template name="build-anchor">
									<xsl:with-param name="a.href">/</xsl:with-param>
									<xsl:with-param name="a.value">
										<i18n:text>xmlui.general.dspace_home</i18n:text>
									</xsl:with-param>
								</xsl:call-template>
							</li>
							<xsl:for-each select="/dri:document/dri:options/dri:list[@n='browse']">
								<xsl:call-template  name="buildMenuItemAsList"   />
							</xsl:for-each>
							
							<li>
								<xsl:call-template name="build-anchor">
									<xsl:with-param name="a.href">/submissions</xsl:with-param>
									<xsl:with-param name="a.value">
										<i18n:text>xmlui.cicdigital.home.aportar-material</i18n:text>
									</xsl:with-param>
								</xsl:call-template>
							</li>
							
							<li class="dropdown">
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
									<li>
										<a href="xmlui.cicdigital.staticPage.reposiotry-policies.uri" i18n:attr="href"/>
											<!--<xsl:attribute name="href">
												<xsl:value-of select="concat($context-path,'/page/politicas-del-repositorio')"></xsl:value-of>
											</xsl:attribute> -->
											<i18n:text>xmlui.cicdigital.title.politicas-del-repositorio</i18n:text>	
									</li>
									<li class="divider"></li>
									<li class="dropdown-header">
										<i18n:text>xmlui.cicdigital.home.informacion-autores</i18n:text>
									</li>
									<li>
										<a href="xmlui.cicdigital.staticPage.how-to-contribute-material.uri" i18n:attr="href"/>
											<!-- <xsl:attribute name="href">
												<xsl:value-of select="concat($context-path,'/page/como-aportar-material')"></xsl:value-of>
											</xsl:attribute>-->
											<i18n:text>xmlui.cicdigital.title.como-aportar-material</i18n:text>	 
									</li>
									<li>
										<a>
											<xsl:attribute name="href">
												<xsl:value-of select="concat($context-path,'/register')"></xsl:value-of>
											</xsl:attribute>
											<i18n:text>xmlui.EPerson.Navigation.register</i18n:text>
										</a>
									</li>
								</ul>
							</li>
							<li>
								<xsl:call-template name="build-anchor">
									<xsl:with-param name="a.href">/feedback</xsl:with-param>
									<xsl:with-param name="a.value">
										<i18n:text>xmlui.dri2xhtml.structural.contact-link</i18n:text>
									</xsl:with-param>
								</xsl:call-template>
							</li>
							
						</ul>
<!-- 					</div> -->
						
<!-- 					<div id="bs-example-navbar-collapse-2">class="collapse navbar-collapse"  -->

						<ul class="nav navbar-nav navbar-right">
							<li>
								<a>
									<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
								</a>
							</li>
							<xsl:for-each select="/dri:document/dri:options/dri:list[@n!='browse' and @n!='discovery']">
								<xsl:if test="count(child::*) &gt; 0">
									<xsl:call-template  name="buildMenuItemAsTree"   />
								</xsl:if>
							</xsl:for-each>
<!-- 							<li> -->
<!-- 								<xsl:choose> -->
<!-- 									<xsl:when -->
<!-- 										test="/dri:document/dri:meta/dri:userMeta/@authenticated = 'yes'"> -->
<!-- 										<a class="dropdown-toggle" id="dropdownMenu1" data-toggle="dropdown"> -->
<!-- 											<xsl:value-of -->
<!-- 												select="/dri:document/dri:meta/dri:userMeta/ -->
<!--                                     dri:metadata[@element='identifier' and @qualifier='firstName']" /> -->
<!-- 											<xsl:text> </xsl:text> -->
<!-- 											<xsl:value-of -->
<!-- 												select="/dri:document/dri:meta/dri:userMeta/ -->
<!--                                     dri:metadata[@element='identifier' and @qualifier='lastName']" /> -->
<!-- 											<span class="caret"></span> -->
<!-- 										</a> -->
<!-- 										<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1"> -->
<!-- 											<li role="presentation"> -->
<!-- 												<xsl:for-each -->
<!-- 													select="//dri:options/dri:list[@n='account']/dri:item/dri:xref"> -->
<!-- 													<a role="menuitem" tabindex="-1"> -->
<!-- 														<xsl:attribute name="href"><xsl:value-of -->
<!-- 															select="@target" /></xsl:attribute> -->
<!-- 														<xsl:copy-of select="." /> -->
<!-- 													</a> -->
<!-- 												</xsl:for-each> -->
<!-- 											</li> -->
<!-- 										</ul> -->
<!-- 									</xsl:when> -->
<!-- 									<xsl:otherwise> -->
<!-- 										<a> -->
<!-- 											<xsl:attribute name="href"> -->
<!-- 	                        	<xsl:value-of -->
<!-- 												select="/dri:document/dri:meta/dri:userMeta/dri:metadata[@element='identifier' and @qualifier='loginURL']" /> -->
<!-- 							</xsl:attribute> -->
<!-- 											<i18n:text>xmlui.dri2xhtml.structural.login</i18n:text> -->
<!-- 										</a> -->
<!-- 									</xsl:otherwise> -->
<!-- 								</xsl:choose> -->
<!-- 							</li> -->
							<li>
								<xsl:call-template name="languageSelection" />

							</li>
						</ul>
						
					</div><!-- /.navbar-collapse -->
				</div>
				</div><!-- /.container-fluid -->
			</nav>
		</div>
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
		<div id="cic-header" class="row">
		
			
			<div id="logo-cic-digital" class="col-xs-12 col-sm-3 col-md-3">
				<xsl:call-template name="build-anchor">
					<xsl:with-param name="img.src">images/Header_cic.png</xsl:with-param>
					<xsl:with-param name="img.alt">CIC-DIGITAL</xsl:with-param>
				</xsl:call-template>
			</div><!-- 
			 --><div id="banner-cic-digital"  class="hidden-xs col-sm-7 col-md-7">
				<div class="">Repositorio de la Comisión de Investigaciones Científicas de la Provincia de Buenos Aires</div>
				<small>Ministerio de Producción, Ciencia y Tecnología</small>
			</div><!-- 
			 --><div id="logo-ba" class="hidden-xs col-sm-2 col-md-2">
				<xsl:call-template name="build-img">
					<xsl:with-param name="img.src">images/logo_BA.png</xsl:with-param>
					<xsl:with-param name="img.alt">Buenos Aires</xsl:with-param>
				</xsl:call-template>
			</div>
		</div>
		
			<xsl:call-template name="buildTopSidebar" />
	</xsl:template>
</xsl:stylesheet>