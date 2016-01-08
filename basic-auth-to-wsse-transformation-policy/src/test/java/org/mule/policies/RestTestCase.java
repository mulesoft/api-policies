package org.mule.policies;

import org.junit.Before;

public class RestTestCase extends AbstractHttpRestCommonTestCase
{
		
	private static final String PROXY_FILE = "rest-proxy/proxy.xml";
	private static final String API_FILE = "rest-api/api.xml";
		
    public RestTestCase()
    {
        super(PROXY_FILE, API_FILE);
    }
    
    @Before
    public void compilePolicy() {    	
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
        parameters.put("mustUnderstand", "0");
        parameters.put("useActor", "abc-");
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
        
        super.compilePolicy();
    }
    
}
