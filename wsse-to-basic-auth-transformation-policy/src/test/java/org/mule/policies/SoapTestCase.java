package org.mule.policies;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

import com.opensymphony.util.FileUtils;

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
        
        SOAP_REQUEST = FileUtils.readFile(new File(TEST_RESOURCES + "admission_request.xml"));    	    	
    }

    @Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	
    	expectedResponseHeaders.clear();
    	expectedResponseHeaders.put("Authorization", "Basic VGVzdF9TT0FfQ29uc3VtZXJUZXN0X1NPQV9Db25zdW1lcjpUZXN0X1NPQV9Db25zdW1lclRlc3RfU09BX0NvbnN1bWVy");
    	
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
