package org.mule.policies;

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
        parameters.put("query", "#[message.inboundProperties['http.method'] == 'GET']");
        parameters.put("denied-message", DENIED_MESSAGE);
        
        super.compilePolicy();
    }

    @Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .setExpectedStatus(200)
        .setPayload(SOAP_REQUEST)        
        .assertResponse();     
                
    }			
       	
	
}
