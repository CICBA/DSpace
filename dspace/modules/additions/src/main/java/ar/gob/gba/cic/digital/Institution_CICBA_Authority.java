package ar.gob.gba.cic.digital;

import org.dspace.content.authority.Choice;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QuerySolution;

public class Institution_CICBA_Authority extends General_CICBA_Authority {

	@Override
	protected String getSelectQueryFields() {
		return super.getSelectQueryFields() +" ?initials";
	}

	@Override
	protected void getTextFilterQuery(ParameterizedSparqlString pqs, String filter) {
		pqs.setNsPrefix("sioc", NS_SIOC);
		pqs.append("OPTIONAL { ?concept sioc:id ?initials} \n");
		if (!"".equals(filter)) {
			pqs.append("FILTER(REGEX(?label, ?text, \"i\") || REGEX(?initials, ?text, \"i\"))\n");
			pqs.setLiteral("text", filter);
		}

	}

	@Override
	protected Choice extractChoice(QuerySolution solution) {
		String key = solution.getResource("concept").getURI();
		String label = solution.getLiteral("label").getString();
		String value = label;
		if (solution.contains("initials") && !"".equals(solution.getLiteral("initials").getString())) {
			String initials = solution.getLiteral("initials").getString();
			label = label + " (" + initials + ")";
		}
		
		return new Choice(key, value, label);
	}
}
