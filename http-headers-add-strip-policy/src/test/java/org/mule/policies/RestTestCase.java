package org.mule.policies;

import org.junit.Before;

public class RestTestCase extends AbstractHttpRestCommonTestCase
{
		
	private static final String PROXY_FILE = "rest-proxy/proxy.xml";
	private static final String API_FILE = "rest-api/api.xml";
	private static final String AUTO_DISCOVERY_FILE = "rest-api/auto_discovery_api.xml";
	
    public RestTestCase()
    {
        super(PROXY_FILE, API_FILE, AUTO_DISCOVERY_FILE);
    }
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	createConfig();
            
    	parameters.put("policyId", "1");
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
        
        super.compilePolicy();
    }
    
}
