package ar.gob.gba.cic.digital;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.dspace.content.authority.Choice;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import ar.edu.unlp.sedici.dspace.authority.SPARQLAuthorityProvider;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;

public class Author_CICBA_Authority extends SPARQLAuthorityProvider {

	protected static final Resource person = ResourceFactory.createResource(NS_FOAF + "Person");
	protected static final Property familyName = ResourceFactory.createProperty(NS_FOAF + "familyName");
	protected static final Property type = ResourceFactory.createProperty(NS_RDF + "type");
	protected static final Property givenName = ResourceFactory.createProperty(NS_FOAF + "givenName");
	protected static final Property mbox = ResourceFactory.createProperty(NS_FOAF + "mbox");
	protected static final Property organization = ResourceFactory.createProperty(NS_FOAF + "Organization");
	protected static final Property linksToOrganisationUnit = ResourceFactory.createProperty(NS_CERIF, "linksToOrganisationUnit");
	protected static final Property title = ResourceFactory.createProperty(NS_DC + "title");	
	protected static final Property siocId = ResourceFactory.createProperty(NS_SIOC + "id");
	protected static final Property startDate = ResourceFactory.createProperty(NS_CERIF + "startDate");
	protected static final Property endDate = ResourceFactory.createProperty(NS_CERIF + "endDate");

	protected ResIterator getRDFResources(Model model) {
		return model.listSubjectsWithProperty(type, person);
	}

	protected Choice extractChoice(Resource subject) {
		
		String key = subject.getURI();
		String label = subject.getProperty(familyName).getString() + ", " + subject.getProperty(givenName).getString() ;
		String value = label;
		StmtIterator links = subject.listProperties(linksToOrganisationUnit);
		if (links.hasNext()){
			label += getAffiliations(links);
		}
		
		return new Choice(key, value, label);
	}

	protected Choice[] extractChoicesfromQuery(QueryEngineHTTP httpQuery) {
		List<Choice> choices = new LinkedList<Choice>();

		Model model = httpQuery.execConstruct(ModelFactory.createDefaultModel());
		ResIterator RDFResources = getRDFResources(model);
		while (RDFResources.hasNext()){
			choices.add(this.extractChoice(RDFResources.next()));
		};
		return choices.toArray(new Choice[0]);
	}

	@Override
	protected ParameterizedSparqlString getSparqlSearch(String field, String filter, String locale, boolean idSearch) {
		if (idSearch)
			return this.getSparqlSearchByIdQuery(field, filter, locale);
		else
			return this.getSparqlSearchByTextQuery(field, filter, locale);
	}

	protected ParameterizedSparqlString getSparqlSearchByIdQuery(String field,
			String key, String locale) {
		ParameterizedSparqlString pqs = new ParameterizedSparqlString();

		pqs.setNsPrefix("foaf", NS_FOAF);
		pqs.setNsPrefix("rdf", NS_RDF);

		pqs.setCommandText("CONSTRUCT { ?person a foaf:Person. ?person foaf:givenName ?name . ?person foaf:familyName ?surname. }\n");
		pqs.append("WHERE {\n");
		pqs.append("?person a foaf:Person ; foaf:givenName ?name ; foaf:familyName ?surname .\n");
		pqs.append("FILTER(REGEX(?person, ?key, \"i\"))\n");
		pqs.append("}\n");
		pqs.append("ORDER BY ?surname \n");

		pqs.setLiteral("key", key);
		return pqs;
	}

	protected ParameterizedSparqlString getSparqlSearchByTextQuery(
			String field, String text, String locale) {
		ParameterizedSparqlString pqs = new ParameterizedSparqlString();
		text = normalizeTextForParserSPARQL10(text);

		pqs.setNsPrefix("foaf", NS_FOAF);
		pqs.setNsPrefix("dc", NS_DC);
		pqs.setNsPrefix("cerif", NS_CERIF);
		pqs.setNsPrefix("rdf", NS_RDF);
		pqs.setNsPrefix("sioc", NS_SIOC);

		pqs.setCommandText("CONSTRUCT { ?person a foaf:Person. ?person foaf:givenName ?name . ?person foaf:mbox ?mail . ?person foaf:familyName ?surname. ?person cerif:linksToOrganisationUnit ?link . ?link cerif:startDate ?inicio. ?link cerif:endDate ?fin . ?link foaf:Organization ?org . ?org dc:title ?affiliation. ?org sioc:id ?id. }\n");
		pqs.append("WHERE {\n");
		pqs.append("?person a foaf:Person ; foaf:givenName ?name; foaf:familyName ?surname. \n");
		pqs.append("	OPTIONAL {\n");
		pqs.append("	?person foaf:mbox ?mail . \n");
		pqs.append("	} . \n");
		pqs.append("	OPTIONAL {\n");
		pqs.append("	?person cerif:linksToOrganisationUnit ?link . ");
		pqs.append("        OPTIONAL { ?link cerif:startDate ?inicio; cerif:endDate ?fin. } .");
		pqs.append("        OPTIONAL { ?link foaf:Organization ?org . ?org dc:title ?affiliation. ");
		pqs.append("                     OPTIONAL { ?org sioc:id ?id . }.");
		pqs.append("                 } \n");
		pqs.append("	}\n");
		if (!"".equals(text)) {
			String[] tokens = text.split(",");
			if (tokens.length > 1 && tokens[0].trim().length() > 0 && tokens[1].trim().length() > 0) {
				pqs.append("FILTER(REGEX(?name, ?text2, \"i\") && REGEX(?surname, ?text1, \"i\"))\n");
				pqs.setLiteral("text1", tokens[0].trim());
				pqs.setLiteral("text2", tokens[1].trim());
			} else {
				pqs.append("FILTER(REGEX(?name, ?text, \"i\") || REGEX(?surname, ?text, \"i\") || REGEX(?id, ?text, \"i\"))\n");
				pqs.setLiteral("text", tokens[0]);
			}
		}
		pqs.append("}\n");
		pqs.append("ORDER BY ?surname ?link\n");
		
		return pqs;
	}


	private String getAffiliations(StmtIterator links) {
		StringBuilder string = new StringBuilder().append(" (");
		while (links.hasNext()){
			Statement link = links.next();
			
			Resource affiliation = link.getObject().asResource();
			if (affiliation.hasProperty(organization)){
				Resource org = affiliation.getProperty(organization).getObject().asResource();
				if (org.hasProperty(siocId) && !org.getProperty(siocId).getString().equals("")){
					string.append(org.getProperty(siocId).getString());
				}
				else if (org.hasProperty(title)) {
					string.append(org.getProperty(title).getString());
				}
			}
			String start = affiliation.getProperty(startDate).getString();
			String end = affiliation.getProperty(endDate).getString();
			if(!"".equals(start) || !"".equals(end)){
				string.append(getPeriodForFiliation(start, end));
			}

			if (links.hasNext()) string.append(", ");
		};
		return string.append(")").toString();
	}

	private String getPeriodForFiliation(String start, String end) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
			if (!"".equals(start)){
				start = df.format(new java.util.Date((Long.valueOf(start)*1000)));
			}
			if (!"".equals(end)){
				end = df.format(new java.util.Date((Long.valueOf(end)*1000)));
			}
		return " [" + start + " - "+  end + "]";
	}


	public ParameterizedSparqlString getSparqlEmailByTextQuery(String field,
			String text, String locale) {
		return  this.getSparqlSearchByTextQuery(field,text,locale);		
	}
	
	
	/**
	 * Get the name and email from a Jena Model containing (NS_FOAF + "Person") elements.
	 * @param model
	 * @return return and array with the email in the 0 position and the name in the 1 position
	 */
	public ArrayList<String[]> extractNameAndEmailFromAuthors(Model model){
		ResIterator persons = getRDFResources(model);
		ArrayList<String[]> result = new ArrayList<String[]>();
		while(persons.hasNext()) {
			Resource currentPerson = persons.next();
			if(currentPerson.getProperty(mbox) != null) {
				String[] respuesta = new String[2];				
				respuesta[0] = currentPerson.getProperty(mbox).getString();
				respuesta[1] = currentPerson.getProperty(givenName).getString() + ", " + currentPerson.getProperty(familyName).getString();
				result.add(respuesta);
			}
		}
		return result;
	}

}
