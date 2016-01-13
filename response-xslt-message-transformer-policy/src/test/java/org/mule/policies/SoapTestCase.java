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
    	try {
			XSLT = FileUtils.readFileToString(new File(XSLT_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}	
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "soap-test");
        parameters.put("apiVersionName", "1");
        parameters.put("xslt", XSLT);
        parameters.put("encoding", "UTF-8");
        parameters.put("mimetype", "application/json");
        
        super.compilePolicy();
    }

    @Test
    public void testInvalidRequest() throws InterruptedException
    {    	    
	    	    	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
		assertResponseBuilder.clear()
		.requestHeader(CONTENT_TYPE, "text/html")
        .setExpectedStatus(200)
        .setPayload(SOAP_REQUEST)        
        .assertResponse();
					
		assertTrue("Content-type should not be changed", assertResponseBuilder.getResponseHeaders().get(CONTENT_TYPE).contains(parameters.get("mimetype").toString()));
        			
    }	               
	
}
