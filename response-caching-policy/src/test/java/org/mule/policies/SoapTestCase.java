package org.mule.policies;

import static org.junit.Assert.assertTrue;

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
        parameters.put("key", "#[payload]");
        parameters.put("ttl", "3000");
        try {
			RESPONSE = FileUtils.readFileToString(new File(SOAP_REQUEST_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
}
