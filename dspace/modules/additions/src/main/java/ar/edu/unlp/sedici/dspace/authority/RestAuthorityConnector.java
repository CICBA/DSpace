package ar.edu.unlp.sedici.dspace.authority;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.json.JSONArray;

public class RestAuthorityConnector {

	private static String getRestEndpoint() {
		ConfigurationService configurationService = DSpaceServicesFactory.getInstance().getConfigurationService();
		String endpoint = configurationService.getProperty("rest-authorities.endpoint.url", null);
		if (endpoint != null) {
			return endpoint;
		} else {
			throw new NullPointerException("Missing endpoint configuration.");
		}
	}

	private static JSONArray getJsonResponseFromQuery(InputStream queryStream) throws IOException {
		String response = "";
		Scanner scanner = new Scanner(queryStream);
		// Write all the JSON data into a string using a scanner
		while (scanner.hasNext()) {
			response += scanner.nextLine();
		}
		// Close the scanner and de inputStream
		scanner.close();
		queryStream.close();
		// Using the JSONObject to simple parse the string into a json object
		JSONArray json = new JSONArray(response);
		return json;
	}

	public static JSONArray executeGetRequest(String path, String param, String filter) {
		String base_url = getRestEndpoint();
		base_url = base_url + "/" + path + "/";
		String charset = StandardCharsets.UTF_8.name();
		String param1 = param;
		String filter1 = filter;
		String query;
		try {
			query = String.format(param1 + "=%s", URLEncoder.encode(filter1, charset));
		} catch (UnsupportedEncodingException e) {
			query = "";
		}
		URL url;
		try {
			url = new URL(base_url + "?" + query);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept-Charset", charset);
			conn.connect();
			return getJsonResponseFromQuery(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
			return new JSONArray();
		}
	}

}
