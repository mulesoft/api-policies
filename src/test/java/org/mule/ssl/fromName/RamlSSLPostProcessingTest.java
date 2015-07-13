package org.mule.ssl.fromName;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.AbstractTemplateTest;
import org.mule.scripts.SSLPostProcessing;
import org.mule.scripts.SSLPostProcessingWithDownloadFromID;
import org.mule.scripts.SSLPostProcessingWithDownloadFromNames;
import org.xml.sax.SAXException;

/**
 * tests HTTP URL-based endpoint targeting HTTPS API
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
    	
    	GATEWAY_APPS_FOLDER = props.getProperty("gatewayAppDir");    	
    	deployHTTPS();      	
		prepareToConnectToAP(props, "ramlApiName", "ramlApiVersion");
		// may change in future
	    proxyAppZip = props.getProperty("ramlApiName") + "-v" + props.getProperty("ramlApiVersion") + ".zip";
	}

	@Test
	public void testProcessing() throws IOException, ParserConfigurationException, SAXException, InterruptedException{
		super.testProcessing(new SSLPostProcessingWithDownloadFromNames(new SSLPostProcessingWithDownloadFromID(new SSLPostProcessing())));
		Thread.sleep(5000);
		final String response = makeTestRequest();
		assertEquals("HTTPS request should be successful", MESSAGE, response);
	}
	
	@Override
	@After
	public void tearDown() throws IOException{
		super.tearDown();
	}
}
