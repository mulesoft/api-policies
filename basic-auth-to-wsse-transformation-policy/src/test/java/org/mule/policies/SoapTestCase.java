package org.mule.policies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class SoapTestCase extends AbstractBasicAuthToWSSETransformationPolicyTestCase
{
		
	private static final String proxyFile = "soap-proxy/proxy.xml";
	private static final String apiFile = "soap-api/api.xml";
	protected String endpointURI = "http://localhost:" + port.getNumber() + "/AdmissionService";
	protected static final String SOAP_REQUEST_FILE = TEST_RESOURCES + "admission_request.xml";
	
    public SoapTestCase()
    {    	
        super(proxyFile, apiFile);        
    }
    
    @Before
    public void prepare() {
    	
    	
    	try {
    		SOAP_REQUEST = FileUtils.readFileToString(new File(SOAP_REQUEST_FILE));
    		
			MustacheFactory mf = new DefaultMustacheFactory();
	        Mustache mustache = mf.compile(POLICY_NAME + ".xml");
	        
	        parameters.put("policyId", "1");
	        parameters.put("mustUnderstand", "0");
	        parameters.put("useActor", "abc-");
	        parameters.put("apiName", "soap-test");
	        parameters.put("apiVersionName", "1");
	        
	        FileUtils.forceMkdir(new File(SAMPLE_DIR));
	        mustache.execute(new FileWriter(SAMPLE_DIR + File.separator + POLICY_NAME + ".xml"), parameters).flush();
	        
		} catch (IOException e) {
			logger.error("Error preparing test: " + e);
			e.printStackTrace();
		} 
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
        }
                        
       // unapplyPolicy(assertResponseBuilder);
                
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
            assertFalse("Security element should be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);            
            assertFalse("Username element should be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertFalse("Password element should be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();  
        }
                        
        unapplyPolicy(assertResponseBuilder);
                
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
            assertFalse("Security element should be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);            
            assertFalse("Username element should be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertFalse("Password element should be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();  
        }
                        
        unapplyPolicy(assertResponseBuilder);
                
    }

	@Test(expected = SAXException.class)
    public void testRequestWithMissingPayload() throws InterruptedException, ParserConfigurationException, SAXException, IOException
    {    	    
	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest();        
        
        assertResponseBuilder.clear()        
        .requestHeader("Content-type", "application/xml")
        .requestHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).getBytes()))        
        .setExpectedStatus(200)
        .assertResponse();
             
        logger.debug("Response payload: " + assertResponseBuilder.getResponseBody());
        createXMLDocument(assertResponseBuilder.getResponseBody());            
                               
        unapplyPolicy(assertResponseBuilder);
                
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
            assertFalse("Security element should be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);            
            assertFalse("Username element should be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertFalse("Password element should be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();  
        }
                        
        unapplyPolicy(assertResponseBuilder);
                
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
            assertFalse("Security element should be present", xmlDocument.getElementsByTagName("wsse:Security").getLength() > 0);            
            assertFalse("Username element should be present", xmlDocument.getElementsByTagName("wsse:Username").getLength() > 0);
            assertFalse("Password element should be present", xmlDocument.getElementsByTagName("wsse:Password").getLength() > 0);            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();  
        }
                        
        unapplyPolicy(assertResponseBuilder);
                
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
                     
        unapplyPolicy(assertResponseBuilder);
                
    }
	
	private void unapplyPolicy(
			AssertEndpointResponseBuilder assertResponseBuilder) {
		removePolicy(POLICY_NAME);
        assertResponseBuilder.clear().setExpectedStatus(200).assertResponse();
	}

	private Document createXMLDocument(
			String body)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ByteArrayInputStream(body.getBytes()));
		return xmlDocument;
	}

	private AssertEndpointResponseBuilder applyPolicyAndTest()
			throws InterruptedException {
		addPolicy(POLICY_NAME + ".xml");		
        Thread.sleep(5000);
        
        try {
			Document document = createXMLDocument(FileUtils.readFileToString(new File(SAMPLE_DIR + POLICY_NAME + ".xml")));
			assertEquals("PolicyId should be set", parameters.get("policyId"), document.getElementsByTagName("policy").item(0).getAttributes().getNamedItem("id").getTextContent());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
        
		AssertEndpointResponseBuilder assertResponseBuilder = new AssertEndpointResponseBuilder(endpointURI, "post");
        //assertResponseBuilder.setPayload(SOAP_REQUEST).setExpectedStatus(200).assertResponse();
		return assertResponseBuilder;
	}
}
