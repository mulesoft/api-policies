package org.mule.policies;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
    	
    	try {
			XSLT = FileUtils.readFileToString(new File(XSLT_FILE));
			RESPONSE = FileUtils.readFileToString(new File(TEST_RESOURCES + "response1251.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	parameters.put("policyId", "1");
        parameters.put("apiName", "http-test");
        parameters.put("apiVersionName", "1");
        
        parameters.put("xslt", XSLT);
        parameters.put("encoding", "cp1251");
        parameters.put("mimetype", "application/xml");
        
        super.compilePolicy();
        
        try {
			RESPONSE = FileUtils.readFileToString(new File(TEST_RESOURCES + "response1251.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}                
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
        				
    }	        
    
}
