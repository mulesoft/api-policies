package org.mule.policies;

import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

public class InvalidMELTestCase extends AbstractPolicyTestCase
{
		
	private static final String PROXY_FILE = "rest-proxy/proxy.xml";
	private static final String API_FILE = "rest-api/api.xml";
		
    public InvalidMELTestCase()
    {
        super(PROXY_FILE, API_FILE);
    }
    
    @Before
    public void compilePolicy() {    	
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
        parameters.put("modification", "#[invalid MEL]");        
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
