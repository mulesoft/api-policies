package org.mule.ssl.outbound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.AbstractTemplateTest;
import org.mule.scripts.InboundSSLPostProcessing;
import org.xml.sax.SAXException;

/**
 * tests RAML-based endpoint targeting HTTPS API
 * @author Miroslav Rusin
 *
 */
public class RamlSSLPostProcessingTest extends AbstractTemplateTest {

		
	@Override
	@Before
	public void prepare() throws IOException{
		LOGGER.info("Testing RAML proxy");
		final Properties props = new Properties();
    	try {
    		props.load(new FileInputStream(TEST_RESOURCES_FOLDER + File.separator + "test.properties"));
    	} catch (final Exception e) {
    		LOGGER.info("Error occured while reading test.properties" + e);
    	} 
    	
    	apiNameId = props.getProperty("ramlApiNameId");
    	apiVersionId = props.getProperty("ramlApiVersionId");
    	
    	GATEWAY_APPS_FOLDER = props.getProperty("gatewayAppDir");    	
    	super.deployHTTPS();  
    	
    	
    	super.prepare();
	}
	
	@Test
	public void testProcessing() throws IOException, ParserConfigurationException, SAXException, InterruptedException{
		super.testOutboundProcessing(new InboundSSLPostProcessing());
		makeTestRequest(HTTP_PROXY_URL);		
	}
	
	@Override
	@After
	public void tearDown() throws IOException{
		super.tearDown();
	}
}
