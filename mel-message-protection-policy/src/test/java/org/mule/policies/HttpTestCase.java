package org.mule.policies;

import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

public class HttpTestCase extends AbstractPolicyTestCase {

	private static final String PROXY_FILE = "http-proxy/proxy.xml";
	private static final String API_FILE = "http-api/api.xml";
	
	public HttpTestCase() {
		super(PROXY_FILE, API_FILE);		
	}	
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "http-test");
        parameters.put("apiVersionName", "1");
        parameters.put("query", "#[invalid MEL]");
        super.compilePolicy();
    }
    
    @Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .setExpectedStatus(500)                
        .assertResponse();                    
    }
    
}
