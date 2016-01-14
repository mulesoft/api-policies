package org.mule.policies;

import static org.junit.Assert.assertTrue;

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
    	endpointURI = "http://localhost:" + proxyPort.getNumber() + QUERY;
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "http-test");
        parameters.put("apiVersionName", "1");
        parameters.put("key", "#[payload]");
        parameters.put("ttl", "3000");
               
        super.compilePolicy();
                               
    }
    
    @Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	        		    
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("post");        
        
		long durationNotCached = getRequestDuration(assertResponseBuilder, true);
		long durationCached = getRequestDuration(assertResponseBuilder, true);
		
		assertTrue("Response time should be lowered after caching", (durationNotCached - durationCached) > 300);
    }   
    
    @Test
    public void testMissingPayload() throws InterruptedException
    {    	    
    	AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("post");        
        
		long durationNotCached = getRequestDuration(assertResponseBuilder, false);
		long durationCached = getRequestDuration(assertResponseBuilder, false);
		
		assertTrue("Response time should be lowered after caching", (durationNotCached - durationCached) > 300);
    } 
    
     
}
