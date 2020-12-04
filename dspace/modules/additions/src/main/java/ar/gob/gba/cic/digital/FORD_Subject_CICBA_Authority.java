package ar.gob.gba.cic.digital;

import org.dspace.content.authority.Choice;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QuerySolution;

public class FORD_Subject_CICBA_Authority extends General_CICBA_Authority {

	@Override
	protected String getSelectQueryFields(boolean idSearch) {
		return " DISTINCT ?label ?externalKey";
	}

	@Override
	protected Choice extractChoice(QuerySolution solution) {
		String key = "";
		if (solution.contains("externalKey")) {
			key = solution.getLiteral("externalKey").getString();
		} else {
			key = solution.getResource("concept").getURI();
		}
		String value = solution.getLiteral("label").getString();
		String label = value;
		return new Choice(key, value, label);
	}

	protected void getIdSearchFilterQuery(ParameterizedSparqlString pqs, String filter) {
		pqs.append("FILTER(?externalKey = ?key) \n");
		pqs.setLiteral("key", filter);
	}

}
