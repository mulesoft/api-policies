package org.mule.policies;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;
import org.mule.policies.utils.JWTConstants;
import org.mule.policies.utils.JWTTokenGenerator;

public class RestRSATestCase extends AbstractPolicyTestCase
{
		
	private static final String PROXY_FILE = "rest-proxy/proxy.xml";
	private static final String API_FILE = "rest-api/api.xml";
	private static final String AUTO_DISCOVERY_FILE = "rest-api/auto_discovery_api.xml";
	
    public RestRSATestCase()
    {    	
        super(PROXY_FILE, API_FILE, AUTO_DISCOVERY_FILE);        
    }
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
                
        parameters.put("secret", Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
        
        parameters.put("audience", JWTConstants.AUDIENCE);
        parameters.put("issuer", JWTConstants.ISSUER);
        
        super.compilePolicy();
    }
    
    private void assertRSAResponse(AssertEndpointResponseBuilder assertResponseBuilder, String algorithm) throws UnsupportedEncodingException, JoseException, InvalidJwtException, NoSuchAlgorithmException, InvalidKeySpecException{				
		assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getRSA(algorithm, keyPair, 
        		JWTConstants.ISSUER, JWTConstants.AUDIENCE, false))
        .setExpectedStatus(200)
        .assertResponse();
	}
    
    @Test
    public void testSuccessfulRequestRSA256() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException, NoSuchAlgorithmException, InvalidKeySpecException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();                
		assertRSAResponse(assertResponseBuilder, AlgorithmIdentifiers.RSA_USING_SHA256);        
        unapplyPolicy(assertResponseBuilder);                
    }
    
    @Test
    public void testSuccessfulRequestRSA384() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException, NoSuchAlgorithmException, InvalidKeySpecException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();                
		assertRSAResponse(assertResponseBuilder, AlgorithmIdentifiers.RSA_USING_SHA384);        
        unapplyPolicy(assertResponseBuilder);                
    }
	
    @Test
    public void testSuccessfulRequestRSA512() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException, NoSuchAlgorithmException, InvalidKeySpecException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();                
		assertRSAResponse(assertResponseBuilder, AlgorithmIdentifiers.RSA_USING_SHA512);        
        unapplyPolicy(assertResponseBuilder);                
    }
    
    @Test
    public void testInvalidSecret() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException, NoSuchAlgorithmException, InvalidKeySpecException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();                
		assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getRSA(AlgorithmIdentifiers.RSA_USING_SHA256, generateKey(), 
        		JWTConstants.ISSUER, JWTConstants.AUDIENCE, false))
        .setExpectedStatus(403)
        .assertResponse();        
        unapplyPolicy(assertResponseBuilder);                
    }
	
}
