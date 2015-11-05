package org.mule.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mule.api.Constants;
import org.mule.api.Scriptable;
import org.mule.util.Zipper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Miroslav Rusin
 * This class is responsible for updating a proxy application to configure HTTPS outbound endpoint and including user provided certificates. 
 */
public class OutboundSSLPostProcessing implements Scriptable {

	private static final String HTTP_REQUEST = "http:request";
	private static final String HTTP_REQUEST_CONFIG = "http:request-config";	
	private static final String HTTPS_TLS_SERVER = "tls:trust-store";
	private static final String TLS_SCHEME = "http://www.mulesoft.org/schema/mule/tls";
	private static final String XMLNS_TLS = "xmlns:tls";
	private static final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation";	
	private static final String TEMP_FOLDER = "tmp";	
	private static final String PROXY_FILE_NAME = "proxy.xml";
	private static final String HTTPS_CONNECTOR_PATH = "https-outbound-config.xml";
	private static final String HTTPS_OUTBOUND_PATH = "/https-outbound.xml";
	private static final String HTTPS_NAMESPACE = "http://www.mulesoft.org/schema/mule/https http://www.mulesoft.org/schema/mule/https/current/mule-https.xsd";
	private static final String CONFIG_PATH = "classes/config.properties";
	private static final Object WSDL_URI = "wsdl.uri";
	protected static Logger LOGGER = LogManager.getLogger(OutboundSSLPostProcessing.class);
	
	
	/**
	 * This method performs SSL post processing of the generated proxy application acquired from the Anypoint Platform. It executes following steps:
	 * 1. extracts the proxy app zip file 
	 * 2. inserts required XML elements that allows making secure HTTP calls to the underlying API
	 * 3. verifies that the configuration parameter 'implementation.uri' contains HTTPS protocol in the URL
	 * 4. packages the modified app in the zip file  
	 * @param input map of input parameters
	 * @throws Exception 
	 */
	@Override
	public void process(Map<String, String> input) throws Exception {
		LOGGER.debug("Executing SSL Post processing to " + input.get(Constants.PROXY));
		final File zipFile = new File(input.get(Constants.PROXY));
		final File certFile = new File(input.get(Constants.KEY_FILE));
		final String storePassword = input.get(Constants.KEY_PASSWORD).toString();
		final String exportPath = input.get(Constants.EXPORT).toString();
		
		if (!zipFile.exists() || zipFile.isDirectory()){
			throw new IllegalArgumentException("File " + zipFile + " does not exist or is a directory.");
		}
		
		if (!certFile.exists() || certFile.isDirectory()){
			throw new IllegalArgumentException("Key File " + certFile + " does not exist or is a directory.");
		}
		
		if (storePassword == null){
			throw new IllegalArgumentException("Key store password is not provided.");
		}
		
		final String tmp = TEMP_FOLDER + File.separator + UUID.randomUUID().toString();
		new File(tmp).mkdirs();
				
		Zipper.unZip(zipFile, tmp + File.separator + zipFile.getName());
		LOGGER.debug("Unzipping " + zipFile.getName() + " to " + (tmp + File.separator + zipFile.getName()));
		
		final String filepath = tmp + File.separator + zipFile.getName() + File.separator + PROXY_FILE_NAME;
		
		// replace XML elements
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		final Document doc = injectHTTPS(docBuilder.parse(filepath), docBuilder, storePassword, certFile.getName(), 
				tmp + File.separator + zipFile.getName() + File.separator);				
		
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();		
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(new File(filepath));
		transformer.transform(source, result);
				
		LOGGER.debug("Modifying " + PROXY_FILE_NAME);
					
		// copy keystore file
		FileUtils.copyFile(certFile, new File(tmp + File.separator + zipFile.getName() + File.separator + certFile.getName()));
		
		FileUtils.copyFile(certFile, new File(tmp + File.separator + zipFile.getName() + File.separator + "classes" + File.separator + certFile.getName()));
		
		new File(exportPath).mkdirs();
				
		// package the modified proxy app
		Zipper.zip(tmp + File.separator + zipFile.getName(), exportPath + File.separator + zipFile.getName());
		LOGGER.debug("Exporting proxy app to " + exportPath + File.separator + zipFile.getName());
		
		FileUtils.deleteDirectory(new File(tmp));		
	}
	
	/**
	 * replaces a HTTP outbound endpoint with the HTTPS outbound endpoint in the document representing the proxy XML
	 * @param doc represents the proxy XML
	 * @param docBuilder used for parsing XML snippets from resource files and importing them
	 * @param storePassword  password to the key store
	 * @param certPath path to the key store
	 * @return modified XML document
	 * @throws SAXException If any parse errors occur 
	 * @throws IOException If any IO errors occur
	 */
	private Document injectHTTPS(Document doc, DocumentBuilder docBuilder, String storePassword, String certPath, String root) throws SAXException, IOException{
		// add required namespace
		
		final Node mule = doc.getFirstChild();
		final Node xsi = mule.getAttributes().getNamedItem(XSI_SCHEMA_LOCATION);
		xsi.setTextContent(xsi.getTextContent().concat(" " + HTTPS_NAMESPACE));
		mule.getAttributes().setNamedItem(xsi);
		
		((Element) mule).setAttribute(XMLNS_TLS, TLS_SCHEME);	
		final NodeList httpReqConfig = doc.getElementsByTagName(HTTP_REQUEST_CONFIG);
		if (httpReqConfig.getLength() > 0){
			httpReqConfig.item(0).getParentNode().removeChild(httpReqConfig.item(0));
		}
		// import global HTTP config and change attributes as given
		final Properties configProps = new Properties();
		final FileInputStream fis = new FileInputStream(root + CONFIG_PATH);
		configProps.load(fis);
		
		final String httpConnectorPath = (configProps.containsKey(WSDL_URI) ?  "/wsdl-" : "/") + HTTPS_CONNECTOR_PATH;
		final Node httpsConnectorNode = doc.importNode(docBuilder.parse(getClass().getResourceAsStream(httpConnectorPath)).getDocumentElement(), true);
		final Element tlsNode = (Element) ((Element) httpsConnectorNode).getElementsByTagName(HTTPS_TLS_SERVER).item(0);
		tlsNode.setAttribute("path", certPath);
		tlsNode.setAttribute("password", storePassword);
		fis.close();
		doc.getDocumentElement().appendChild(httpsConnectorNode);
		
		final Node httpsOutboundNode = doc.importNode(docBuilder.parse(getClass().getResourceAsStream(HTTPS_OUTBOUND_PATH)).getDocumentElement(), true);		
		// replace HTTP request component with HTTPS
		final NodeList flows = doc.getElementsByTagName(HTTP_REQUEST);
		if (flows.getLength() > 0){
			final Node httpOutboundNode = flows.item(0); 
			httpOutboundNode.getParentNode().replaceChild(httpsOutboundNode, httpOutboundNode);
		}
		return doc;
	}
	
} 