package org.mule.policies;

import java.io.UnsupportedEncodingException;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;
import org.mule.policies.utils.JWTConstants;
import org.mule.policies.utils.JWTTokenGenerator;

public class HttpHMACTestCase extends AbstractPolicyTestCase {

	private static final String PROXY_FILE = "http-proxy/proxy.xml";
	private static final String API_FILE = "http-api/api.xml";
	private static final String AUTO_DISCOVERY_FILE = "http-api/auto_discovery_api.xml";
	
	public HttpHMACTestCase() {
		super(PROXY_FILE, API_FILE, AUTO_DISCOVERY_FILE);		
	}	
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "http-test");
        parameters.put("apiVersionName", "1");
        parameters.put("secret", JWTConstants.HMAC_SECRET);
        parameters.put("audience", JWTConstants.AUDIENCE);
        parameters.put("issuer", JWTConstants.ISSUER);
        
        super.compilePolicy();
    }
    
    @Test
    public void testSuccessfulRequestHMAC256() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();                
		assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA256, JWTConstants.HMAC_SECRET, JWTConstants.ISSUER, 
        		JWTConstants.AUDIENCE, false))
        .setExpectedStatus(200)
        .assertResponse();        
        unapplyPolicy(assertResponseBuilder);
                
    }
       
}
