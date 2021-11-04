package org.dspace.content.authority;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dspace.app.requestitem.RequestItemAuthor;
import org.dspace.app.requestitem.RequestItemSubmitterStrategy;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.authority.service.ChoiceAuthorityService;
import org.dspace.content.authority.service.MetadataAuthorityService;
import org.dspace.content.service.ItemService;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.core.Context;
import org.dspace.core.service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class CICRequestItemMetadataAuthority extends RequestItemSubmitterStrategy{
	
	protected String emailMetadata;
    protected String fullNameMetadata;

    @Autowired(required = true)
    protected ItemService itemService;
	@Autowired(required = true)
    protected MetadataAuthorityService metadataAuthorityService;
    @Autowired(required = true)
    protected ChoiceAuthorityService choiceAuthorityService;
    @Autowired(required = true)
    protected PluginService pluginService;
    
    private String email;
    private String fullname;
	
	public RequestItemAuthor getRequestItemAuthor(Context context, Item item)
			throws SQLException {
		
		if (emailMetadata != null)
		{
			List<MetadataValue> vals = itemService.getMetadataByMetadataString(item, emailMetadata);
			for (MetadataValue metadataValue : vals) {
				if (StringUtils.isNotBlank((metadataValue.getAuthority())))
				{
					String value = metadataValue.getValue();
					String[] metadata = emailMetadata.split("\\.");
					String fieldKey=null;
					if(metadata.length == 2){
						fieldKey = metadataAuthorityService.makeFieldKey(metadata[0],metadata[1],null);
					}else if (metadata.length == 3){
						fieldKey = metadataAuthorityService.makeFieldKey(metadata[0],metadata[1],metadata[2]);
					}
					if(choiceAuthorityService.isChoicesConfigured(fieldKey)){
						this.getNameAndEmail(fieldKey, value,item.getOwningCollection(), 0, 0, null);					
					}
					//Check if we have an email and an author. If the author authority have them, then send an email, if not keep looking...
					if(StringUtils.isNotBlank(email) && StringUtils.isNotBlank(fullname)){
						RequestItemAuthor author = new RequestItemAuthor(
								fullname, email);
						return author;
					}
				}
			}
		}
		//If cannot send an email to any of the item authors, then send an email to the item submitter as defined in superclass...
		return super.getRequestItemAuthor(context, item);
	}
	
	public void setEmailMetadata(String emailMetadata) {
		this.emailMetadata = emailMetadata;
	}

	public void setFullNameMetadata(String fullNameMetadata) {
		this.fullNameMetadata = fullNameMetadata;
	}
	
	private void getNameAndEmail(String fieldKey, String text, Collection collection, int offset, int limit, String locale){
		ar.gob.gba.cic.digital.Author_CICBA_Authority cicAuth = (ar.gob.gba.cic.digital.Author_CICBA_Authority) pluginService.getNamedPlugin(ChoiceAuthority.class, "Author_CICBA_Authority");
		ParameterizedSparqlString parameterizedSparqlString = cicAuth.getSparqlEmailByTextQuery(fieldKey, text, locale);
		Query query = QueryFactory.create(parameterizedSparqlString.toString(),	Syntax.syntaxSPARQL_10);
		query.setOffset(offset);
		if (limit == 0)
			query.setLimit(Query.NOLIMIT);
		else
			query.setLimit(limit);
		ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
		String endpoint = configurationService.getProperty("sparql-authorities", "endpoint.url");
		QueryEngineHTTP httpQuery = new QueryEngineHTTP(endpoint, query);
		httpQuery.setAllowDeflate(false);
		httpQuery.setAllowGZip(false);
		Model results=httpQuery.execConstruct();
		ArrayList<String[]> authors = cicAuth.extractNameAndEmailFromAuthors(results);
		if (authors.size() > 0) {
			String[] author=authors.get(0);
			if(StringUtils.isNotBlank(author[0]) && StringUtils.isNotBlank(author[1])) {
				this.email = author[0];
				this.fullname = author[1];
			}
		}
	}

}
