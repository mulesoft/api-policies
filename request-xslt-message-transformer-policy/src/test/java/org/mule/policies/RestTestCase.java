package org.mule.policies;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
    	try {
			XSLT = FileUtils.readFileToString(new File(XSLT_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
        parameters.put("xslt", XSLT);
        parameters.put("encoding", "UTF-8");
        parameters.put("mimetype", "application/xml");
        
        super.compilePolicy();
    }        
    
    @Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	    
	    	
    	expectedResponseHeaders.put(CONTENT_TYPE, "application/xml");
    	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
		assertResponseBuilder.clear()
		.requestHeader(CONTENT_TYPE, "text/html")
        .setExpectedStatus(200)
        .setPayload(REQUEST)
        .setExpectedResult(RESPONSE)        
        .assertResponse();     
        	
		// TODO uncomment for GW 2.2
		//assertTrue("Content-type should be changed", assertResponseBuilder.getResponseHeaders().get(CONTENT_TYPE).contains(expectedResponseHeaders.get(CONTENT_TYPE)));
    }	
    
    @Test
    public void testInvalidRequest() throws InterruptedException
    {    	    
    	expectedResponseHeaders.put(CONTENT_TYPE, "text/html");
    	AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
		assertResponseBuilder.clear()        
        .requestHeaders(expectedResponseHeaders)
        .setExpectedStatus(200)
        .setPayload(REQUEST.replace("</cities>", ""))
        .setExpectedResult(REQUEST.replace("</cities>", "")) 
        .assertResponse();    
		
		assertTrue("Content-type should not be changed", assertResponseBuilder.getResponseHeaders().get(CONTENT_TYPE).contains(expectedResponseHeaders.get(CONTENT_TYPE)));
                
    }
}
