package org.mule.ssl.fromName;

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
		
		final Properties props = initGatewayParams();    	
    	 	
    	deployHTTPS();      	
		prepareToConnectToAP(props, "ramlApiName", "ramlApiVersion");
		// may change in future
	    proxyAppZip = props.getProperty("ramlApiName") + "-v" + props.getProperty("ramlApiVersion")  + "-" + GATEWAY_VERSION.replace("0", "x") + ".zip";
	}

	@Test
	public void testProcessing() throws IOException, ParserConfigurationException, SAXException, InterruptedException{
		super.testProcessing(new SSLPostProcessingWithDownloadFromNames(new SSLPostProcessingWithDownloadFromID(new SSLPostProcessing())));
		Thread.sleep(5000);
		makeTestRequest();		
	}
	
	@Override
	@After
	public void tearDown() throws IOException{
		super.tearDown();
	}
}
