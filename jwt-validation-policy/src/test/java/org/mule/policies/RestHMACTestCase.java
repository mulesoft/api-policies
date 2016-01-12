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

public class RestHMACTestCase extends AbstractPolicyTestCase
{
		
	private static final String PROXY_FILE = "rest-proxy/proxy.xml";
	private static final String API_FILE = "rest-api/api.xml";
	private static final String AUTO_DISCOVERY_FILE = "rest-api/auto_discovery_api.xml";
	
    public RestHMACTestCase()
    {    	
        super(PROXY_FILE, API_FILE, AUTO_DISCOVERY_FILE);        
    }
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
        
        parameters.put("secret", JWTConstants.HMAC_SECRET);
        parameters.put("audience", JWTConstants.AUDIENCE);
        parameters.put("issuer", JWTConstants.ISSUER);
        
        super.compilePolicy();
    }

    private void assertHMACResponse(AssertEndpointResponseBuilder assertResponseBuilder, String algorithm) throws UnsupportedEncodingException, JoseException, InvalidJwtException{
		assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(algorithm, JWTConstants.HMAC_SECRET, JWTConstants.ISSUER, JWTConstants.AUDIENCE, false))
        .setExpectedStatus(200)
        .assertResponse();
	}
	    
    @Test
    public void testSuccessfulRequestHMAC256() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();                
        assertHMACResponse(assertResponseBuilder, AlgorithmIdentifiers.HMAC_SHA256);        
        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testSuccessfulRequestHMAC384() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();                
        assertHMACResponse(assertResponseBuilder, AlgorithmIdentifiers.HMAC_SHA384);
        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testSuccessfulRequestHMAC512() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
		assertHMACResponse(assertResponseBuilder, AlgorithmIdentifiers.HMAC_SHA512);
        unapplyPolicy(assertResponseBuilder);
                
    }		
	
	@Test
    public void testSuccessfulRequestHMAC512WithAditionalTrailingCharacter() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
		assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA512, 
        		JWTConstants.HMAC_SECRET, JWTConstants.ISSUER, JWTConstants.AUDIENCE, false) + "=")
        .setExpectedStatus(200)
        .assertResponse();
        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testMissingAuthorizatonHeader() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testInvalidAuthorizatonHeader() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Basic " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA256, JWTConstants.HMAC_SECRET, 
        		JWTConstants.ISSUER, JWTConstants.AUDIENCE, false))
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testWrongSecret() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA256, JWTConstants.HMAC_SECRET + "1", 
        		JWTConstants.ISSUER, JWTConstants.AUDIENCE, false))
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testWrongAudience() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA256, JWTConstants.HMAC_SECRET, 
        		JWTConstants.ISSUER, JWTConstants.AUDIENCE + "1", false))
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
			
	@Test
    public void testWrongIssuer() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA256, JWTConstants.HMAC_SECRET, 
        		JWTConstants.ISSUER + "1", JWTConstants.AUDIENCE, false))
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
      
	@Test
    public void testExpiredToken() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA256, JWTConstants.HMAC_SECRET, 
        		JWTConstants.ISSUER, JWTConstants.AUDIENCE, true))
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
    
	@Test
    public void testNullValues() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA256, JWTConstants.HMAC_SECRET, 
        		null, (String) null, false))
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testInvalidJWTTokenTwoParts() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer aaa.bbb")
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testInvalidJWTTokenThreeParts() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer aaa.bbb.ccc")
        .setExpectedStatus(500)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }
	
	@Test
    public void testInvalidJWTTokenOnePart() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer aaabbbccc")
        .setExpectedStatus(403)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }			
	
	@Test
    public void testMultipleAudiences() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getHMAC(AlgorithmIdentifiers.HMAC_SHA256, JWTConstants.HMAC_SECRET, 
        		JWTConstants.ISSUER, new String[] { JWTConstants.AUDIENCE, JWTConstants.AUDIENCE + "1" }, false))
        .setExpectedStatus(200)
        .assertResponse();

        unapplyPolicy(assertResponseBuilder);
                
    }			

}
