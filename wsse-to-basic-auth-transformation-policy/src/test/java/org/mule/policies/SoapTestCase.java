package org.mule.policies;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

public class SoapTestCase extends AbstractPolicyTestCase
{
		
	private static final String PROXY_FILE = "soap-proxy/proxy.xml";
	private static final String API_FILE = "soap-api/api.xml";
		
    public SoapTestCase()
    {    	
        super(PROXY_FILE, API_FILE);        
    }
    
    @Before
    public void compilePolicy() {    	
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	parameters.put("policyId", "1");
        parameters.put("apiName", "soap-test");
        parameters.put("apiVersionName", "1");
        
        parameters.put("remove-wsse", "true");        
        
        super.compilePolicy();
    }

    @Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	
    	expectedResponseHeaders.clear();
    	expectedResponseHeaders.put("Authorization", "Basic am9lOnNlY3JldA==");
    	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("post");        
		
		assertResponseBuilder.clear()
		.requestHeader(CONTENT_TYPE, "text/xml")
        .setExpectedStatus(200)
        .setPayload(SOAP_REQUEST)       
        .setExpectedResponseHeaders(expectedResponseHeaders)
        .assertResponse();    
		
		assertFalse("WSSE Security header should be stripped", assertResponseBuilder.getResponseBody().contains("wsse:Security"));
    }  	               
	
}
