package org.mule.policies;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;
import org.mule.policies.utils.JWTConstants;
import org.mule.policies.utils.JWTTokenGenerator;

public class SoapRSATestCase extends AbstractPolicyTestCase
{
		
	private static final String PROXY_FILE = "soap-proxy/proxy.xml";
	private static final String API_FILE = "soap-api/api.xml";
	private static final String AUTO_DISCOVERY_FILE = "soap-api/auto_discovery_api.xml";
	private String SOAP_REQUEST;
	
	protected static final String SOAP_REQUEST_FILE = TEST_RESOURCES + "admission_request.xml";
	
    public SoapRSATestCase()
    {    	
        super(PROXY_FILE, API_FILE, AUTO_DISCOVERY_FILE);        
    }
    
    @Before
    public void compilePolicy() {
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
    	parameters.put("apiName", "soap-test");
        parameters.put("apiVersionName", "1");
        
        parameters.put("secret", Base64.encodeBase64String(keyPair.getPublic().getEncoded()));        
        parameters.put("audience", JWTConstants.AUDIENCE);
        parameters.put("issuer", JWTConstants.ISSUER);
        
        
    	try {
			SOAP_REQUEST = FileUtils.readFileToString(new File(SOAP_REQUEST_FILE));
		} catch (IOException e) {
			logger.error("Error reading SOAP request XML: " + e);
			e.printStackTrace();
		}
		    	
        super.compilePolicy();
    }        
    
    @Test
    public void testSuccessfulRequestRSA256() throws InterruptedException, UnsupportedEncodingException, JoseException, InvalidJwtException, NoSuchAlgorithmException, InvalidKeySpecException
    {    	    					
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();                
		assertResponseBuilder.clear()   
        .requestHeader("Authorization", "Bearer " + JWTTokenGenerator.getRSA(AlgorithmIdentifiers.RSA_USING_SHA256, keyPair, JWTConstants.ISSUER, 
        		JWTConstants.AUDIENCE, false))
        .setExpectedStatus(200)
        .setPayload(SOAP_REQUEST)
        .assertResponse();        
        unapplyPolicy(assertResponseBuilder);
                
    }
       	
}
