package org.mule.scripts;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
public class SSLPostProcessing implements Scriptable {

	private static final String HTTPS_TLS_SERVER = "https:tls-server";
	private static final String HTTPS_PROTOCOL = "https";
	private static final String HTTP_PROTOCOL = "http";
	private static final String IMPLEMENTATION_URI = "implementation.uri";
	private static final String HTTPS_SCHEME = "http://www.mulesoft.org/schema/mule/https";
	private static final String XMLNS_HTTPS = "xmlns:https";
	private static final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation";	
	private static final String TEMP_FOLDER = "tmp";	
	private static final String PROXY_FILE_NAME = "proxy.xml";
	private static final String CONFIG_SRC_PATH = "META-INF/src/main/resources/config.properties";
	private static final String CONFIG_CLASSES_PATH = "classes/config.properties";
	private static final String HTTPS_CONNECTOR_PATH = "/https-connector.xml";
	private static final String HTTPS_OUTBOUND_PATH = "/https-outbound.xml";
	private static final String HTTPS_NAMESPACE = "http://www.mulesoft.org/schema/mule/https http://www.mulesoft.org/schema/mule/https/current/mule-https.xsd";
	private static final String WSDL_URI = "wsdl.uri";
	
	protected static Logger LOGGER = LogManager.getLogger(SSLPostProcessing.class);
	
	
	/**
	 * This method performs SSL post processing of the generated proxy application acquired from the Anypoint Platform. It executes following steps:
	 * 1. extracts the proxy app zip file 
	 * 2. inserts required XML elements that allows making secure HTTP calls to the underlying API
	 * 3. verifies that the configuration parameter 'implementation.uri' contains HTTPS protocol in the URL
	 * 4. packages the modified app in the zip file  
	 * @param input map of input parameters
	 * @throws Exception 
	 */
	public void process(Map<String, String> input) throws Exception {
		LOGGER.debug("Executing SSL Post processing to " + input.get(Constants.PROXY));
		final File zipFile = new File(input.get(Constants.PROXY));
		final File certFile = new File(input.get(Constants.KEY_FILE));
		final String storePassword = input.get(Constants.KEY_PASSWORD).toString();
		final String exportPath = input.get(Constants.EXPORT).toString();
		
		if (!zipFile.exists() || zipFile.isDirectory()){
			throw new IllegalArgumentException("File " + zipFile + " does not exists or is a directory.");
		}
		
		if (!certFile.exists() || certFile.isDirectory()){
			throw new IllegalArgumentException("Key File " + certFile + " does not exists or is a directory.");
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
		final Document doc = injectHTTPS(docBuilder.parse(filepath), docBuilder, storePassword, certFile.getName());				
										
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();		
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(new File(filepath));
		transformer.transform(source, result);
				
		LOGGER.debug("Modifying " + PROXY_FILE_NAME);
		
		// modify config.properties file so the implementation uri contains https. cannot use Properties because it inserts '/' to values
		final List<String> propLines = FileUtils.readLines(new File(tmp + File.separator + zipFile.getName() + File.separator + CONFIG_SRC_PATH));
		if (!propLines.isEmpty()){
			int i = 0;
			while (i < propLines.size()){
				final String line = propLines.get(i);
				if (line.startsWith(IMPLEMENTATION_URI)){
					if (line.contains(HTTP_PROTOCOL + ":")){
						propLines.set(i, propLines.get(i).replace(HTTP_PROTOCOL, HTTPS_PROTOCOL));
						i = propLines.size();
					}
					
				}
				if (line.startsWith(WSDL_URI)){
					if (line.contains(HTTP_PROTOCOL + ":")){
						propLines.set(i, propLines.get(i).replace(HTTP_PROTOCOL, HTTPS_PROTOCOL));
						i = propLines.size();
					}
				}
				i++;
			}
		}
		
		FileUtils.writeLines(new File(tmp + File.separator + zipFile.getName() + File.separator + CONFIG_SRC_PATH), propLines);
		FileUtils.writeLines(new File(tmp + File.separator + zipFile.getName() + File.separator + CONFIG_CLASSES_PATH), propLines);
		LOGGER.debug("Updating config.properties");
		
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
	private Document injectHTTPS(Document doc, DocumentBuilder docBuilder, String storePassword, String certPath) throws SAXException, IOException{
		final Node mule = doc.getFirstChild();
		
		final Node xsi = mule.getAttributes().getNamedItem(XSI_SCHEMA_LOCATION);
		xsi.setTextContent(xsi.getTextContent().concat(" " + HTTPS_NAMESPACE));
		mule.getAttributes().setNamedItem(xsi);
		
		((Element) mule).setAttribute(XMLNS_HTTPS, HTTPS_SCHEME);		
		
		final Node httpsConnectorNode = doc.importNode(docBuilder.parse(getClass().getResourceAsStream(HTTPS_CONNECTOR_PATH)).getDocumentElement(), true);
		final Element tlsNode = (Element) ((Element) httpsConnectorNode).getElementsByTagName(HTTPS_TLS_SERVER).item(0);
		tlsNode.setAttribute("path", certPath);
		tlsNode.setAttribute("storePassword", storePassword);
		
		doc.getDocumentElement().appendChild(httpsConnectorNode);
		
		final Node httpsOutboundNode = doc.importNode(docBuilder.parse(getClass().getResourceAsStream(HTTPS_OUTBOUND_PATH)).getDocumentElement(), true);		
		
		final NodeList flows = doc.getElementsByTagName("http:outbound-endpoint");
		if (flows.getLength() > 0){
			final Node httpOutboundNode = flows.item(0); 
			httpOutboundNode.getParentNode().replaceChild(httpsOutboundNode, httpOutboundNode);
		}
		return doc;
	}
	
}