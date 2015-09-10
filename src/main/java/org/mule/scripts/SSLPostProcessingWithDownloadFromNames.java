package org.mule.scripts;

import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.Constants;
import org.mule.api.Scriptable;
import org.mule.util.AddAuthorizationHeaderFilter;

/**
 * Extends a scriptable class with downloading a proxy based on API name and API Version name
 * @author Miroslav Rusin
 *
 */
public class SSLPostProcessingWithDownloadFromNames implements Scriptable {

	private final Scriptable script;
	protected static Logger LOGGER = LogManager.getLogger(SSLPostProcessingWithDownloadFromNames.class);
	private static String PROXY_URI;
	private static String LOGIN_URI;	
	private static String ORG_URI;
	private static String HIERARCHY_URI;
	private static String APIS_URI;
	
	protected static final String MAIN_RESOURCES_FOLDER = "./src/main/resources";
		
	private String apiNameId;
	private String apiVersionId;
		
	public SSLPostProcessingWithDownloadFromNames(Scriptable script){
		this.script = script;
		init();
	}
	
	/**
	 * initializes test parameters from properties file
	 */
	private void init(){
		final Properties props = new Properties();
    	try {
    		props.load(getClass().getResourceAsStream("/anypoint.properties"));
    	} catch (final Exception e) {
    		LOGGER.info("Error occured while reading test.properties" + e);
    	} 
    	
    	PROXY_URI = props.getProperty("proxy.uri");
    	LOGIN_URI = props.getProperty("login.uri");
    	ORG_URI = props.getProperty("org.uri");
    	APIS_URI = props.getProperty("apis.uri");
    	HIERARCHY_URI = props.getProperty("hierarchy.uri");
	}
	
	/**
	 *  downloads a proxy from Anypoint Platform using given credentials and modifies it to allow secure calls
	 */
	@Override
	public void process(Map<String, String> input) throws Exception {
		final String USER = input.get(Constants.USER).toString();
		final String PASSWORD = input.get(Constants.PASSWORD).toString();
		final String apiName = input.get(Constants.API_NAME).toString();
		final String apiVersion = input.get(Constants.API_VERSION_NAME).toString();
		
		LOGGER.info("Signing in to Anypoint Platform with a user: " + USER);
    	final ResteasyClient client = new ResteasyClientBuilder().build();
        final ResteasyWebTarget target = client.target(PROXY_URI + LOGIN_URI);
        final Response response = target.request().post(Entity.entity("{ \"username\": \"" + USER + "\", \"password\": \"" + PASSWORD + "\" }", "application/json"));
        
        if (response.getStatus() != 200)
        	throw new IllegalArgumentException("Unable to authorize to Anypoint Platform. Please check your credentials.");
        
        //Read output in string format        
        final JSONObject jso = new JSONObject(response.readEntity(String.class));
        response.close();
        final String access_token = jso.getString("access_token");
        
        client.register(new AddAuthorizationHeaderFilter(access_token));
        LOGGER.info("Searching for proxy app: Api Name " + apiName + " Api Version " + apiVersion);
        
        final JSONObject currentOrg = readJSONObjectFromURL(client, PROXY_URI + ORG_URI);	
        
        final JSONObject root = readJSONObjectFromURL(client, PROXY_URI + HIERARCHY_URI.replace("ORG_ID", currentOrg.getString("id")));
        final JSONObject parent = findParent(root, client);
        
        iterateSubOrganizations(parent, client, apiName, apiVersion);
				
		if (apiNameId == null || apiVersionId == null)
			throw new IllegalArgumentException("There is no API with API Name: " + apiName + " and API Version: " + apiVersion);
        
        
        input.put(Constants.API_ID, apiNameId);
		input.put(Constants.API_VERSION_ID, apiVersionId);
		
		script.process(input);
	}

	private void iterateSubOrganizations(final JSONObject org, ResteasyClient client, String apiName, String apiVersion) {					
		getIdsFromNames(client, apiName, apiVersion, org.getString("id"));
		if (apiNameId != null)
			return;
		
		for (int i = 0; i < org.getJSONArray("subOrganizations").length(); i++){
			final JSONObject subOrg = org.getJSONArray("subOrganizations").getJSONObject(i);			
			iterateSubOrganizations(subOrg, client, apiName, apiVersion);
		}
	}

	private JSONObject findParent(final JSONObject org, ResteasyClient client) {											
		for (int i = 0; i < org.getJSONArray("parentOrganizationIds").length(); i++){
			final String parentOrgId = org.getJSONArray("parentOrganizationIds").getString(i);						
			final JSONObject parentOrg = readJSONObjectFromURL(client, PROXY_URI + HIERARCHY_URI.replace("ORG_ID", parentOrgId));
			if (parentOrg.getJSONArray("parentOrganizationIds").length() == 0)
				return parentOrg;
		}
		return org;
	}
	
	private JSONObject readJSONObjectFromURL(final ResteasyClient client, String url) {
		final ResteasyWebTarget target1 = client.target(url);
		final Response response1 = target1.request().get();
		final JSONObject jso1 = new JSONObject(response1.readEntity(String.class));
		return jso1;
	}

	/**
	 * searches Anypoint platform for IDs based on the given apiName and apiVersion
	 * @param client object to make REST calls with
	 * @param apiName name of API 
	 * @param apiVersion version name of API
	 */
	private void getIdsFromNames(ResteasyClient client, String apiName, String apiVersion, String orgId) {
		final ResteasyWebTarget target = client.target(PROXY_URI + APIS_URI.replace("ORG_ID", orgId));		
		final Response response = target.request().get();
		final JSONObject jso = new JSONObject(response.readEntity(String.class));
		final JSONArray apis = jso.getJSONArray("apis");
		for (int i = 0; i < apis.length(); i++){
			if (apis.getJSONObject(i).get("name").equals(apiName)){
				for (int j = 0; j < apis.getJSONObject(i).getJSONArray("versions").length(); j++){
					final JSONObject version = apis.getJSONObject(i).getJSONArray("versions").getJSONObject(j);
					if (version.getString("name").equals(apiVersion)){
						apiNameId = version.get("apiId").toString();
						apiVersionId = version.get("id").toString();
						return;
					}
				}				
			}
						
		}
		response.close();
		
        
	}
	
}
