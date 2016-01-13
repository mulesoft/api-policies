package org.mule.policies;

import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

public class RestTestCase extends AbstractPolicyTestCase {
		
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
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
        parameters.put("query", "#[message.inboundProperties['http.method'] == 'POST']");
        parameters.put("denied-message", DENIED_MESSAGE);
        
        super.compilePolicy();
    }
    
    @Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .setExpectedStatus(403)    
        .setExpectedResult(DENIED_MESSAGE)
        .assertResponse();     
                
    }	
    
}
