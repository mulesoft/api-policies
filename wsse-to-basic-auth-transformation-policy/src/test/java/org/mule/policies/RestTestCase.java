package org.mule.policies;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

public class RestTestCase extends AbstractPolicyTestCase {
		
	private static final String PROXY_FILE = "rest-proxy/proxy.xml";
	private static final String API_FILE = "rest-api/api.xml";
	private String REQUEST_WITHOUT_WSSE;
	
		
    public RestTestCase()
    {
        super(PROXY_FILE, API_FILE);
    }
    
    @Before
    public void compilePolicy() {    	
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
        
        parameters.put("remove-wsse", "false");        
        endpointURI = "http://localhost:" + proxyPort.getNumber();
        
        try {
			REQUEST_WITHOUT_WSSE = FileUtils.readFileToString(new File(TEST_RESOURCES + "requestWithoutWSSE.xml"));
		} catch (IOException e) {
			logger.error("Error reading requestWithoutWSSE.xml");
			e.printStackTrace();
		}
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
        .setExpectedResult(SOAP_REQUEST)
        .setExpectedResponseHeaders(expectedResponseHeaders)
        .assertResponse();     
    	    
    }
    
    @Test
    public void testMissingWSSEHeader() throws InterruptedException
    {    	    
    	AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("post");        
        
    	assertResponseBuilder.clear()
		.requestHeader(CONTENT_TYPE, "application/xml")
        .setExpectedStatus(200)
        .setPayload(REQUEST_WITHOUT_WSSE)
        .addUnexpectedResponseHeader("authorization")
        .assertResponse();     
        	
    }
    
    @Test
    public void testMissingContentTypeHeader() throws InterruptedException
    {    	    
    	AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("post");        
        
    	assertResponseBuilder.clear()
		.setExpectedStatus(200)
        .setPayload(REQUEST_WITHOUT_WSSE)
        .addUnexpectedResponseHeader("authorization")
        .assertResponse();     
        	
    }
    
    @Test
    public void testMissingPayload() throws InterruptedException
    {    	    
    	AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("post");        
        
    	assertResponseBuilder.clear()
    	.requestHeader(CONTENT_TYPE, "application/xml")
		.setExpectedStatus(200)
        .addUnexpectedResponseHeader("authorization")
        .assertResponse();     
        	
    }
    
    @Test
    public void testExistingAuthorizationHeader() throws InterruptedException
    {   
    	expectedResponseHeaders.clear();
    	expectedResponseHeaders.put("Authorization", "Basic bm9lOnNlY3JldA==");
    	
    	AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("post");        
        
    	assertResponseBuilder.clear()
		.requestHeader(CONTENT_TYPE, "application/xml")
		.requestHeader("Authorization", "Basic bm9lOnNlY3JldA==")
        .setExpectedStatus(200)
        .setExpectedResponseHeaders(expectedResponseHeaders)
        .setPayload(REQUEST_WITHOUT_WSSE)
        
        .assertResponse();     
        	
    }
        
}
