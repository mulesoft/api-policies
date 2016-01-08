package org.mule.policies;

import org.junit.Before;

public class HttpTestCase extends AbstractHttpRestCommonTestCase {

	private static final String PROXY_FILE = "http-proxy/proxy.xml";
	private static final String API_FILE = "http-api/api.xml";
	
	public HttpTestCase() {
		super(PROXY_FILE, API_FILE);		
	}	
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
        parameters.put("mustUnderstand", "0");
        parameters.put("useActor", "abc-");
        parameters.put("apiName", "http-test");
        parameters.put("apiVersionName", "1");
        
        super.compilePolicy();
    }
}
