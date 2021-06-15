package ar.edu.unlp.sedici.dspace.authority;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.authority.Choice;
import org.dspace.content.authority.ChoiceAuthority;
import org.dspace.content.authority.Choices;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RestAuthorityProvider implements ChoiceAuthority {

	private final String idField = "auth_key";

	protected static Logger log = Logger.getLogger(RestAuthorityProvider.class);

	/**
	 *
	 * @param field metadata field responsible for the query
	 * @return the path to the specific endpoint aimed by our query
	 */
	protected abstract String getPath(String field);

	/**
	 *
	 * @param field metadata field responsible for the query
	 * @return the attribute we are going to filter by
	 */
	protected abstract String getFilterField(String field);

	/**
	 *
	 * @param field metadata field responsible for the query
	 * @return the attribute containing the key by which we will do exact match
	 *         filtering
	 */
	protected final String getIdField(String field) {
		return this.idField;
	};

	/**
	 *
	 * @param field metadata field responsible for the query
	 * @param jsonChoice one of the results returned by the query as a json object
	 * @return a Choice object made from de json object result
	 */
	protected abstract Choice extractChoice(String field, JSONObject jsonChoice);

	protected final Choice[] extractChoicesfromQuery(String field, JSONArray response) {
		List<Choice> choices = new LinkedList<Choice>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject jsonResponse = response.getJSONObject(i);
			choices.add(this.extractChoice(field, jsonResponse));
		}
		return choices.toArray(new Choice[0]);
	}

	private final Choice[] doChoicesQuery(String field, HashMap<String, String> params) {
		String path = getPath(field);
		JSONArray response = RestAuthorityConnector.executeGetRequest(path, params);
		return extractChoicesfromQuery(field, response);
	}

	protected final Choice[] doChoicesIdQuery(String field, String key) {
		String idField = getIdField(field);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(idField, key);
		return doChoicesQuery(field, params);
	}

	protected Choice[] doChoicesTextQuery(String field, String filter) {
		String filterField = getFilterField(field);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(filterField, filter);
		return doChoicesQuery(field, params);
	}

	@Override
	public final Choices getMatches(String field, String text, Collection collection, int start, int limit,
			String locale) {
		Choice[] choices = this.doChoicesTextQuery(field, text);
		log.trace(choices.length + "matches found for text " + text);
		return new Choices(choices, start, limit, Choices.CF_ACCEPTED, false);
	}

	@Override
	public final Choices getBestMatch(String field, String text, Collection collection, String locale) {
		return this.getMatches(field, text, collection, 0, 1, locale);
	}

	@Override
	public final String getLabel(String field, String key, String locale) {
		Choice[] choices = this.doChoicesIdQuery(field, key);
		if (choices.length == 0)
			return null;
		else
			return choices[0].label;
	}

}
