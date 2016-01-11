package org.mule.policies;

import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

public abstract class AbstractHttpRestCommonTestCase extends AbstractPolicyTestCase {

	
	public AbstractHttpRestCommonTestCase(String proxyFile, String apiFile, String autoDiscoveryFile) {
		super(proxyFile, apiFile, autoDiscoveryFile);
		
	}

	@Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	    			
		expectedResponseHeaders.clear();
		expectedResponseHeaders.put("k", "");
        expectedResponseHeaders.put("i", "j");
        
        unExpectedResponseHeaders.clear();
        unExpectedResponseHeaders.add("");
        unExpectedResponseHeaders.add("a");
        unExpectedResponseHeaders.add("c");        
        unExpectedResponseHeaders.add("Vary");
        
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        Thread.sleep(2000);
        assertResponseBuilder.clear()           
        .setExpectedStatus(200)
        .setExpectedResponseHeaders(expectedResponseHeaders)
        .setUnExpectedResponseHeaders(unExpectedResponseHeaders)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testRequestHeaders() throws InterruptedException
    {
		endpointURI = "http://localhost:" + autoDiscoveryPort.getNumber() + "/api";
    	 		
		expectedResponseHeaders.clear();
		expectedResponseHeaders.put("a", "b");
        expectedResponseHeaders.put("c", "");
        
        unExpectedResponseHeaders.clear();
        unExpectedResponseHeaders.add("");
        unExpectedResponseHeaders.add("http.reason");
        unExpectedResponseHeaders.add("Vary");
        
        
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        Thread.sleep(2000);
        assertResponseBuilder.clear()           
        .setExpectedStatus(200)
        .setExpectedResponseHeaders(expectedResponseHeaders)        
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testOwerwriteRequestHeaders() throws InterruptedException
    {
		endpointURI = "http://localhost:" + autoDiscoveryPort.getNumber() + "/api";
    	 		
		expectedResponseHeaders.clear();
		expectedResponseHeaders.put("a", "b");
        expectedResponseHeaders.put("c", "");
                               
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        Thread.sleep(2000);
        assertResponseBuilder.clear()           
        .requestHeader("a", "c")
        .setExpectedStatus(200)
        .setExpectedResponseHeaders(expectedResponseHeaders)        
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	protected void createConfig(){
		parameters.clear();
		
        parameters.put("response-headers-to-remove", "Vary");
        parameters.put("request-header-to-add1", "a");
        parameters.put("request-value-to-add1", "b");
        parameters.put("request-header-to-add2", "c");
        parameters.put("request-value-to-add2", "");
        parameters.put("request-header-to-add3", "");
        parameters.put("request-value-to-add3", "f");
        
        parameters.put("response-header-to-add1", "");
        parameters.put("response-value-to-add1", "h");
        parameters.put("response-header-to-add2", "i");
        parameters.put("response-value-to-add2", "j");
        parameters.put("response-header-to-add3", "k");
        parameters.put("response-value-to-add3", " ");
	}
	
       	
}
