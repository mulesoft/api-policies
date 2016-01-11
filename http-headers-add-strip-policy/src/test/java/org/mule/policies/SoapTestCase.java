package org.mule.policies;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

public class SoapTestCase extends AbstractPolicyTestCase
{
		
	private static final String PROXY_FILE = "soap-proxy/proxy.xml";
	private static final String API_FILE = "soap-api/api.xml";
	private static final String AUTO_DISCOVERY_FILE = "soap-api/auto_discovery_api.xml";
	private String SOAP_REQUEST;
	
	protected static final String SOAP_REQUEST_FILE = TEST_RESOURCES + "admission_request.xml";
	
    public SoapTestCase()
    {    	
        super(PROXY_FILE, API_FILE, AUTO_DISCOVERY_FILE);        
    }
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	try {
			SOAP_REQUEST = FileUtils.readFileToString(new File(SOAP_REQUEST_FILE));
		} catch (IOException e) {
			logger.error("Error reading SOAP request XML: " + e);
			e.printStackTrace();
		}
		
    	parameters.put("policyId", "1");
    	parameters.put("apiName", "soap-test");
        parameters.put("apiVersionName", "1");
        
        parameters.put("response-headers-to-remove", "Date|Vary");
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
        
        super.compilePolicy();
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
        unExpectedResponseHeaders.add("http.reason");
        unExpectedResponseHeaders.add("Vary");
        
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        Thread.sleep(2000);
        assertResponseBuilder.clear()           
        .setExpectedStatus(200)
        .setExpectedResponseHeaders(expectedResponseHeaders)
        .setUnExpectedResponseHeaders(unExpectedResponseHeaders)        
        .setPayload(SOAP_REQUEST)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testRequestHeaders() throws InterruptedException
    {
		endpointURI = "http://localhost:" + autoDiscoveryPort.getNumber() + "/AdmissionService";
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
        .setUnExpectedResponseHeaders(unExpectedResponseHeaders)
        .setPayload(SOAP_REQUEST)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }	       
	
}
