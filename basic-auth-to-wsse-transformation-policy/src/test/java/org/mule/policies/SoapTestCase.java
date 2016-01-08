package org.mule.policies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Base64;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SoapTestCase extends AbstractBasicAuthToWSSETransformationPolicyTestCase
{
		
	private static final String PROXY_FILE = "soap-proxy/proxy.xml";
	private static final String API_FILE = "soap-api/api.xml";
	
	protected static final String SOAP_REQUEST_FILE = TEST_RESOURCES + "admission_request.xml";
	
    public SoapTestCase()
    {    	
        super(PROXY_FILE, API_FILE);        
    }
    
    @Before
    public void compilePolicy() {    	
    	endpointURI = "http://localhost:" + proxyPort.getNumber();
    	
    	parameters.put("policyId", "1");
        parameters.put("mustUnderstand", "0");
        parameters.put("useActor", "abc-");
        parameters.put("apiName", "soap-test");
        parameters.put("apiVersionName", "1");
        
        super.compilePolicy();
    }

	@Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .requestHeader("Content-type", "application/xml")
        .requestHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes()))
        .setPayload(SOAP_REQUEST)
        .setExpectedStatus(200)
        .assertResponse();
             
        logger.debug("Response payload: " + assertResponseBuilder.getResponseBody());
        try {
        	Document xmlDocument = createXMLDocument(assertResponseBuilder.getResponseBody());
        	        	           
            assertTrue("Security element should be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);
            assertEquals("mustUnderstandValue should be set", 
            		xmlDocument.getElementsByTagName("wsse:Security").item(0).getAttributes().getNamedItem("mustUnderstandValue").getTextContent(), 
            		parameters.get("mustUnderstand"));
            
            assertTrue("Username element should be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertTrue("Password element should be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);
            assertEquals("User should be set", xmlDocument.getElementsByTagName("wsse:Username").item(0).getTextContent(), USER);
            assertEquals("Password should be set", xmlDocument.getElementsByTagName("wsse:Password").item(0).getTextContent(), PASSWORD);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();  
            assertTrue("Error processing XML", false);
        }
                                             
    }		
       
	@Test
    public void testRequestWithMissingContentType() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .requestHeader("Content-type", "application/json")
        .requestHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes()))
        .setPayload(SOAP_REQUEST)
        .setExpectedStatus(200)
        .assertResponse();
             
        logger.debug("Response payload: " + assertResponseBuilder.getResponseBody());
        try {
        	Document xmlDocument = createXMLDocument(assertResponseBuilder.getResponseBody());            
            assertFalse("Security element should not be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);            
            assertFalse("Username element should not be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertFalse("Password element should not be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();  
            assertTrue("Error processing XML", false);
        }
    }
	
	@Test
    public void testRequestWithInvalidContentType() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .requestHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes()))
        .setPayload(SOAP_REQUEST)
        .setExpectedStatus(200)
        .assertResponse();
             
        logger.debug("Response payload: " + assertResponseBuilder.getResponseBody());
        try {
        	Document xmlDocument = createXMLDocument(assertResponseBuilder.getResponseBody());            
            assertFalse("Security element should not be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);            
            assertFalse("Username element should not be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertFalse("Password element should not be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();  
            assertTrue("Error processing XML", false);
        }
    }

	@Test
    public void testRequestWithMissingPayload() throws InterruptedException, ParserConfigurationException, SAXException, IOException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .requestHeader("Content-type", "application/xml")
        .requestHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes()))        
        .setExpectedStatus(500)
        .assertResponse();
             
        logger.debug("Response payload: " + assertResponseBuilder.getResponseBody());
        createXMLDocument(assertResponseBuilder.getResponseBody());            
    }
	
	@Test
    public void testRequestWithMissingAuthorizationHeader() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .requestHeader("Content-type", "application/xml")
        .setPayload(SOAP_REQUEST)
        .setExpectedStatus(200)
        .assertResponse();
             
        logger.debug("Response payload: " + assertResponseBuilder.getResponseBody());
        try {
        	Document xmlDocument = createXMLDocument(assertResponseBuilder.getResponseBody());            
            assertFalse("Security element should not be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);            
            assertFalse("Username element should not be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertFalse("Password element should not be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();  
            assertTrue("Error processing XML", false);
        }
    }
	
	@Test
    public void testRequestWithInvalidAuthorizationHeader() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .requestHeader("Content-type", "application/xml")
        .requestHeader("Authorization", "Bearer " + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes()))        
        .setPayload(SOAP_REQUEST)
        .setExpectedStatus(200)
        .assertResponse();
             
        logger.debug("Response payload: " + assertResponseBuilder.getResponseBody());
        try {
        	Document xmlDocument = createXMLDocument(assertResponseBuilder.getResponseBody());            
            assertFalse("Security element should not be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);            
            assertFalse("Username element should not be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertFalse("Password element should not be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace(); 
            assertTrue("Error processing XML", false);
        }                                             
    }
	
	@Test
    public void testRequestWithInvalidPayload() throws InterruptedException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .requestHeader("Content-type", "application/xml")
        .requestHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes()))        
        .setPayload("invalid payload")
        .setExpectedStatus(500)
        .assertResponse();
                                            
    }
	
}
