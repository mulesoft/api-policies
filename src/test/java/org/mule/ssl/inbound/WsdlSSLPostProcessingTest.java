package org.mule.ssl.inbound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.AbstractTemplateTest;
import org.mule.scripts.InboundSSLPostProcessing;
import org.xml.sax.SAXException;

/**
 * tests WSDL-based endpoint targeting HTTPS API
 * @author Miroslav Rusin
 *
 */
public class WsdlSSLPostProcessingTest extends AbstractTemplateTest {

		
	@Override
	@Before
	public void prepare() throws IOException{
		LOGGER.info("Testing WSDL proxy");
		final Properties props = new Properties();
    	try {
    		props.load(new FileInputStream(TEST_RESOURCES_FOLDER + File.separator + "test.properties"));
    	} catch (final Exception e) {
    		LOGGER.info("Error occured while reading test.properties" + e);
    	} 
    	IMPLEMENTATION_URI = "wsdl.uri";
    	apiNameId = props.getProperty("wsdlApiNameId");
    	apiVersionId = props.getProperty("wsdlApiVersionId");
    	
    	GATEWAY_APPS_FOLDER = props.getProperty("gatewayAppDir");    	
    	super.deployHTTPSforWSDL();  
    	
    	super.prepare();
	}

	
	@Test
	public void testProcessing() throws IOException, ParserConfigurationException, SAXException, InterruptedException{
		super.testInboundProcessing(new InboundSSLPostProcessing());		   		
		makeTestRequest(HTTP_PROXY_URL, "/AdmissionService", FileUtils.readFileToString(new File(TEST_RESOURCES_FOLDER + File.separator + "soap-message.xml")));		       
	}
	
	@Override
	@After
	public void tearDown() throws IOException{
		super.tearDown();
	}
}
