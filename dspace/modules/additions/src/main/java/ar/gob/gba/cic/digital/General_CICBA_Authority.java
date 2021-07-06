package ar.gob.gba.cic.digital;

import java.util.LinkedList;
import java.util.List;

import org.dspace.content.authority.Choice;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import ar.edu.unlp.sedici.dspace.authority.SPARQLAuthorityProvider;

public class General_CICBA_Authority extends SPARQLAuthorityProvider {

    protected final String CHOICES_ONLYLEAFS_PREFIX = "choices.onlyLeafs.";
    protected final String CHOICES_PARENT_PREFIX = "choices.parent.";
    protected final String CHOICES_PARENTPROPERTY_PREFIX = "choices.parentProperty.";
    protected final String CHOICES_TYPEPROPERTY_PREFIX = "choices.typeProperty.";
    protected final String CHOICES_LABELPROPERTY_PREFIX = "choices.labelProperty.";
    protected final String CHOICES_EXTERNALKEY_PREFIX = "choices.externalKeyProperty.";

	protected ParameterizedSparqlString getSparqlSearch(
			String filter, String locale, boolean idSearch) {

		ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
		String metadataField= this.getMetadataField();
		String typeProperty= configurationService.getProperty(CHOICES_TYPEPROPERTY_PREFIX+metadataField,"skos:concept");
		String labelProperty= configurationService.getProperty(CHOICES_LABELPROPERTY_PREFIX+metadataField,"skos:prefLabel");
		String parent= configurationService.getProperty(CHOICES_PARENT_PREFIX+metadataField,null);
		String parentProperty= configurationService.getProperty(CHOICES_PARENTPROPERTY_PREFIX+metadataField,"skos:broader");
		boolean onlyLeafs= configurationService.getBooleanProperty(CHOICES_ONLYLEAFS_PREFIX+metadataField,false);
		String externalKey= configurationService.getProperty(CHOICES_EXTERNALKEY_PREFIX + metadataField, null);

		ParameterizedSparqlString pqs = new ParameterizedSparqlString();

		pqs.setNsPrefix("foaf", NS_FOAF);
		pqs.setNsPrefix("dc", NS_DC);
		pqs.setNsPrefix("cic", NS_CIC);
		pqs.setNsPrefix("skos", NS_SKOS);
		pqs.setNsPrefix("owl", NS_OWL);
		pqs.setNsPrefix("rdfs", NS_RDFS);

		pqs.setCommandText("SELECT "+ this.getSelectQueryFields(idSearch) + "\n");
		pqs.append("WHERE {\n");
		pqs.append("?concept a "+ typeProperty + " .\n");
		pqs.append("?concept "+ labelProperty +" ?label .\n");
		if (externalKey != null) {
			pqs.append("?concept "+ externalKey +" ?externalKey .\n");
		}

		if (parent != null) {
		   //Si el parent es vacio se buscan nodos raiz, es decir, sin padre
		   if ("".equals(parent)){
		      pqs.append("OPTIONAL { ?concept " +parentProperty+" ?father } \n");
		      pqs.append("FILTER(!bound(?father)) \n");
		   }
		   //Si no es vacio es un parent id, se buscan los hijos directos de ese padre
		   else
		      pqs.append("?concept " +parentProperty+" "+parent+".\n");
		}
		//Si la propiedad onlyLeafs es true se buscan los nodos hoja, es decir, sin hijos
		if (onlyLeafs) {
			pqs.append("OPTIONAL { ?hijos "+parentProperty+" ?concept}\n");
			pqs.append("FILTER(!bound(?hijos)) \n");
		}
		//Dependiendo del paramtro idSearch la busqueda se hace por id o por text
		if (idSearch) {
			pqs.append("FILTER(REGEX(?concept, ?key, \"i\")) \n");
			pqs.append("}\n");

			pqs.setLiteral("key", filter);
		}
		else {
			filter = normalizeTextForParserSPARQL10(filter);
			getTextFilterQuery(pqs,filter);
			pqs.append("}\n");
			pqs.append("ORDER BY ASC(?label)\n");
		}

		return pqs;
	}

	private String getMetadataField() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getSelectQueryFields(boolean idSearch){
		return "?concept ?label";
	}

	protected void getTextFilterQuery(ParameterizedSparqlString pqs, String filter) {
		if (!"".equals(filter)) {
			pqs.append("FILTER(REGEX(?label, ?text, \"i\")) \n");
			pqs.setLiteral("text", filter);
		}
	}
	protected Choice extractChoice(QuerySolution solution) {
		String key = solution.getResource("concept").getURI();
		String label = solution.getLiteral("label").getString();
		return new Choice(key, label, label);
	}

	protected Choice[] extractChoicesfromQuery(QueryEngineHTTP httpQuery) {
		List<Choice> choices = new LinkedList<Choice>();
		ResultSet results = httpQuery.execSelect();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			choices.add(this.extractChoice(solution));
		}
		return choices.toArray(new Choice[0]);
	}

}
