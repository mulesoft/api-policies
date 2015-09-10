package org.mule.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONException;
import org.json.JSONObject;
import org.mule.api.Constants;
import org.mule.api.Scriptable;
import org.mule.util.AddAuthorizationHeaderFilter;

public class SSLPostProcessingWithDownloadFromID implements Scriptable {

	private static final String SUB_ORGANIZATION_IDS = "subOrganizationIds";
	private final Scriptable script;
	protected static Logger LOGGER = LogManager.getLogger(SSLPostProcessingWithDownloadFromID.class);
	private static String PROXY_URI;
	private static String LOGIN_URI;
	private static String ORG_URI;
	private static String HIERARCHY_URI;
	
	private static String DOWNLOAD_PROXY_URI;
	protected static final String MAIN_RESOURCES_FOLDER = "./src/main/resources";	
	private static final Object INPUT_FOLDER = "input";
	private static String GATEWAY_VERSION;
	private String proxyAppName;
	
	public SSLPostProcessingWithDownloadFromID(Scriptable script){
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
    	DOWNLOAD_PROXY_URI = props.getProperty("download.proxy.uri");
    	ORG_URI = props.getProperty("org.uri");
    	GATEWAY_VERSION = props.getProperty("gateway.version");
    	HIERARCHY_URI = props.getProperty("hierarchy.uri");
	}
		
	/**
	 * downloads a proxy from Anypoint Platform using given credentials and modifies it to allow secure calls
	 */
	@Override
	public void process(Map<String, String> input) throws Exception {
		final String user = input.get(Constants.USER).toString();
		final String password = input.get(Constants.PASSWORD).toString();
		final String apiNameId = input.get(Constants.API_ID).toString();
		final String apiVersionId = input.get(Constants.API_VERSION_ID).toString();
		
		LOGGER.info("Signing in to Anypoint Platform with a user: " + user);
    	final ResteasyClient client = new ResteasyClientBuilder().build();
    	final ResteasyWebTarget target = client.target(LOGIN_URI);
        
        final Response response = target.request().post(Entity.entity("{ \"username\": \"" + user + "\", \"password\": \"" + password + "\" }", MediaType.APPLICATION_JSON));        
        //Read output in string format        
        if (response.getStatus() != 200)
        	throw new IllegalArgumentException("Unable to authorize to Anypoint Platform. Please check your credentials.");
        	
        final JSONObject jso = new JSONObject(response.readEntity(String.class));
        response.close();
        final String access_token = jso.getString("access_token");
        client.register(new AddAuthorizationHeaderFilter(access_token));
        LOGGER.info("Downloading proxy app: Api Name Id " + apiNameId + " Api Version Id " + apiVersionId);
        
        final JSONObject currentOrg = readJSONObjectFromURL(client, PROXY_URI + ORG_URI);			
        final JSONObject root = readJSONObjectFromURL(client, PROXY_URI + HIERARCHY_URI.replace("ORG_ID", currentOrg.getString("id")));
        //downloadProxy(client, apiNameId, apiVersionId, root.getString("id"));
		iterateSubOrganizations(root, client, apiNameId, apiVersionId);
        
		if (proxyAppName == null)			
        	throw new IllegalArgumentException("There is no API with API Name Id: " + apiNameId + " and API Version Id: " + apiVersionId +". If there exists such API, be sure that the endpoint is configured using Anypoint Platform.");
	        
        input.put(Constants.PROXY, MAIN_RESOURCES_FOLDER + File.separator + INPUT_FOLDER + File.separator + proxyAppName);
        		
		script.process(input);
	}
	
	private void iterateSubOrganizations(final JSONObject org, ResteasyClient client, String apiNameId, String apiVersionId) throws FileNotFoundException, JSONException, IOException {		
		downloadProxy(client, apiNameId, apiVersionId, org.getString("id"));
		if (proxyAppName != null)
			return;
	
		for (int i = 0; i < org.getJSONArray("subOrganizations").length(); i++){
			final JSONObject subOrg = org.getJSONArray("subOrganizations").getJSONObject(i);								
			iterateSubOrganizations(subOrg, client, apiNameId, apiVersionId);
		}
	}
	
	private JSONObject readJSONObjectFromURL(final ResteasyClient client, String url) {
		final ResteasyWebTarget target1 = client.target(url);
		final Response response1 = target1.request().get();
		final JSONObject jso1 = new JSONObject(response1.readEntity(String.class));
		return jso1;
	}

	/**
	 * downloads a proxy app in ZIP format from Anypoint Platform
	 * @param client ResteasyClient instance used to make REST calls
	 * @param apiNameId defines which API proxy to download 
	 * @param apiVersionId defines which API version proxy to download
	 * @throws FileNotFoundException thrown during saving a file to a disk
	 * @throws IOException if I/O error occurs
	 */
	private void downloadProxy(final ResteasyClient client, String apiNameId, String apiVersionId, String orgId)
			throws FileNotFoundException, IOException {
		
		final ResteasyWebTarget target1 = client.target(PROXY_URI + ORG_URI);
		final Response response1 = target1.request().get();
		final JSONObject jso1 = new JSONObject(response1.readEntity(String.class));
					
		final ResteasyWebTarget target = client.target(PROXY_URI + DOWNLOAD_PROXY_URI.replace("API_ID", apiNameId).replace("VERSION_ID", apiVersionId).replace("ORG_ID", orgId) + "/proxy?gatewayVersion=" + GATEWAY_VERSION);        
        final Response response = target.request().get();
        
        final InputStream inputStream = response.readEntity(InputStream.class);
        
        if (response.getHeaderString("content-disposition") != null){        
	        proxyAppName = response.getHeaderString("content-disposition").substring(response.getHeaderString("content-disposition").indexOf("filename=") + "filename=".length()).replace("\"", "");
	        new File(MAIN_RESOURCES_FOLDER + File.separator + INPUT_FOLDER).mkdirs();
	                
	        saveToFile(inputStream, MAIN_RESOURCES_FOLDER + File.separator + INPUT_FOLDER + File.separator + proxyAppName);
        }
        response.close();
	}
	
	/**
	 * reads from inputStream and writes to a file using path parameter
	 * @param inputStream inputStream to read data from
	 * @param path defines a location to store a file to
	 * @throws FileNotFoundException thrown by FileOutputStream 
	 * @throws IOException if I/O error occurs
	 */
	private void saveToFile(final InputStream inputStream, String path) throws FileNotFoundException, IOException {		
        final OutputStream outputStream = new FileOutputStream(path);
        final byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();		
	}
}
