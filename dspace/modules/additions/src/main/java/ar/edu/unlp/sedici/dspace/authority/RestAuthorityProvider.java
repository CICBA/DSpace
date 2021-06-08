package ar.edu.unlp.sedici.dspace.authority;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.authority.Choice;
import org.dspace.content.authority.ChoiceAuthority;
import org.dspace.content.authority.Choices;
import org.json.JSONArray;
import org.json.JSONObject;

public class RestAuthorityProvider implements ChoiceAuthority {

	protected static Logger log = Logger.getLogger(RestAuthorityProvider.class);

	/**
	 *
	 * @return the path to the specific endpoint aimed by our query
	 */
	private String getPath() {
		return "todos";
	}

	/**
	 *
	 * @return the attribute we are going to filter by
	 */
	protected String getFilterField() {
		return "title";
	}

	/**
	 *
	 * @return the attribute containing the key by which we will do exact match
	 *         filtering
	 */
	protected String getIdField() {
		return "id";
	}

	private Choice extractChoice(JSONObject jsonChoice) {
		String key = jsonChoice.getNumber(getIdField()).toString();
		String value = jsonChoice.getString(getFilterField());
		return new Choice(key, value, value);
	}

	protected Choice[] extractChoicesfromQuery(JSONArray response) {
		List<Choice> choices = new LinkedList<Choice>();
		for (int i = 0; i < response.length(); i++) {
			choices.add(this.extractChoice(response.getJSONObject(i)));
		}
		return choices.toArray(new Choice[0]);
	}

	protected Choice[] doChoicesQuery(String param, String filter) {
		JSONArray response = RestAuthorityConnector.executeGetRequest(getPath(), param, filter);
		return extractChoicesfromQuery(response);
	}

	@Override
	public Choices getMatches(String metadata, String text, Collection collection, int start, int limit,
			String locale) {
		Choice[] choices = this.doChoicesQuery(getFilterField(), text);
		log.trace(choices.length + "matches found for text " + text);
		return new Choices(choices, start, limit, Choices.CF_ACCEPTED, false);
	}

	@Override
	public final Choices getBestMatch(String field, String text, Collection collection, String locale) {
		return this.getMatches(field, text, collection, 0, 1, locale);
	}

	@Override
	public String getLabel(String field, String key, String locale) {
		Choice[] choices = this.doChoicesQuery(getIdField(), key);
		if (choices.length == 0)
			return null;
		else
			return choices[0].label;
	}

}
