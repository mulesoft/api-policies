package org.mule.ssl.fromName;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.AbstractTemplateTest;
import org.mule.scripts.OutboundSSLPostProcessing;
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
	    proxyAppZip = props.getProperty("ramlApiName") + "-v" + props.getProperty("ramlApiVersion") + "-" + GATEWAY_VERSION + ".zip";
	}

	@Test
	public void testProcessing() throws IOException, ParserConfigurationException, SAXException, InterruptedException{
		super.testOutboundProcessing(new SSLPostProcessingWithDownloadFromNames(new SSLPostProcessingWithDownloadFromID(new OutboundSSLPostProcessing())));
		Thread.sleep(5000);
		makeTestRequest(HTTP_PROXY_URL);
	}
	
	@Override
	@After
	public void tearDown() throws IOException{
		super.tearDown();
	}
}
