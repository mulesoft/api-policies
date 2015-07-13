package org.mule.ssl.fromName;

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
    	
    	GATEWAY_APPS_FOLDER = props.getProperty("gatewayAppDir");    	
    	deployHTTPS();  
    	    	    	
		prepareToConnectToAP(props, "wsdlApiName", "wsdlApiVersion");
		// may change in future
	    proxyAppZip = props.getProperty("wsdlApiName") + "-v" + props.getProperty("wsdlApiVersion") + ".zip";
	}

	@Test
	public void testProcessing() throws IOException, ParserConfigurationException, SAXException, InterruptedException{
		super.testProcessing(new SSLPostProcessingWithDownloadFromNames(new SSLPostProcessingWithDownloadFromID(new SSLPostProcessing())));
		Thread.sleep(5000);
		testWsdlReqest();
	}
	
	@Override
	@After
	public void tearDown() throws IOException{
		super.tearDown();
	}
}
