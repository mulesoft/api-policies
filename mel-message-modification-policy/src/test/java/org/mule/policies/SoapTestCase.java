package org.mule.policies;

import java.util.Collections;

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
        parameters.put("modification", "#[message.outboundProperties['" + KEY +  "'] = '" + VALUE +"']");
        
        super.compilePolicy();
    }

	@Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .setPayload(SOAP_REQUEST)
        .setExpectedStatus(200)
        .setExpectedResponseHeaders(Collections.singletonMap(KEY, VALUE))
        .assertResponse();
                                                                 
    }		
       	
	
}
