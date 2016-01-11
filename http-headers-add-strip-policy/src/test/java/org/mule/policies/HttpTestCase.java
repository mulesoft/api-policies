package org.mule.policies;

import org.junit.Before;

public class HttpTestCase extends AbstractHttpRestCommonTestCase {

	private static final String PROXY_FILE = "http-proxy/proxy.xml";
	private static final String API_FILE = "http-api/api.xml";
	private static final String AUTO_DISCOVERY_FILE = "http-api/auto_discovery_api.xml";
	
	public HttpTestCase() {
		super(PROXY_FILE, API_FILE, AUTO_DISCOVERY_FILE);		
	}	
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	createConfig();
    	parameters.put("policyId", "1");
        parameters.put("apiName", "http-test");
        parameters.put("apiVersionName", "1");
        
        super.compilePolicy();
    }
}
