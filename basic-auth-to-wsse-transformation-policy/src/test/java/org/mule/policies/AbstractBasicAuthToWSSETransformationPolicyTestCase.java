package org.mule.policies;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.mule.policies.commons.AbstractSamplePoliciesTestCase;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public abstract class AbstractBasicAuthToWSSETransformationPolicyTestCase extends AbstractSamplePoliciesTestCase
{
	protected static final String USER = "user";
	protected static final String PASSWORD = "password";
	
	protected static final String TEST_RESOURCES = "./src/test/resources/";
	protected static final String SAMPLE_DIR = TEST_RESOURCES + "samples/";
	protected static final String SOAP_REQUEST_FILE = TEST_RESOURCES + "request.xml";
	
	protected static final String POLICY_NAME = "basic-auth-to-wsse-transformation-policy";
	protected static String SOAP_REQUEST;
	// FIXME redirect requests to proxy instead of api
	protected String endpointURI = "http://localhost:" + port.getNumber() + "/api";
	protected Map<String, Object> parameters = new HashMap<>();
	
	
    public AbstractBasicAuthToWSSETransformationPolicyTestCase(String proxyFile, String apiFile)
    {
        super(proxyFile, apiFile, true);
    }
    
    protected void prepare() {    	    
    	try {
    		SOAP_REQUEST = FileUtils.readFileToString(new File(SOAP_REQUEST_FILE));
    		
				        
	    	MustacheFactory mf = new DefaultMustacheFactory();
	        Mustache mustache = mf.compile(POLICY_NAME + ".xml");
	        	        	       
	        FileUtils.forceMkdir(new File(SAMPLE_DIR));
	        mustache.execute(new FileWriter(SAMPLE_DIR + File.separator + POLICY_NAME + ".xml"), parameters).flush();
	        
		} catch (IOException e) {
			logger.error("Error preparing test: " + e);
			e.printStackTrace();
		} 
    }       
       
	
}
