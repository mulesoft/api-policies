package org.mule.policies.commons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.mule.MuleCoreExtension;
import org.mule.api.config.MuleProperties;
import org.mule.config.spring.util.ProcessingStrategyUtils;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.transport.NullPayload;

import com.mulesoft.anypoint.tests.FakeMuleApplication;
import com.mulesoft.module.endpoint.EndpointAliasesCoreExtension;


public abstract class AbstractSamplePoliciesTestCase extends AbstractStandalonePolicyAwareTestCase
{

    protected static final long PROBER_TIMEOUT = 2000L;
    protected static final long PROBER_POLLING_INTERVAL = 200L;
    protected static NullPayload NULL_PAYLOAD = NullPayload.getInstance();

    @Rule
    public SystemProperty systemProperty;

    protected String apiFile, proxyFile;    

    public AbstractSamplePoliciesTestCase(String proxyFile, String apiFile, boolean nonBlocking)
    {
        this.apiFile = apiFile;
        this.proxyFile = proxyFile;
        if (nonBlocking)
        {
            systemProperty = new SystemProperty(MuleProperties.MULE_DEFAULT_PROCESSING_STRATEGY,
                                                ProcessingStrategyUtils.NON_BLOCKING_PROCESSING_STRATEGY);
        }
    }

    @Override
    protected List<FakeMuleApplication> getApplications()
    {
    	Properties properties = new Properties();
    	
    	try {
			properties.load(getClass().getResourceAsStream("/soap-proxy/config.properties"));
			properties.setProperty("wsdl.uri", properties.getProperty("wsdl.uri").replace("{port}", port.getValue()));
			List<String> lines = new ArrayList<>();			
	    	for (Entry<Object, Object> prop :properties.entrySet()){
	    		lines.add(prop.getKey() + "=" + prop.getValue());
	    	}
	    	
			FileUtils.writeLines(new File(getClass().getResource("/soap-proxy/classes/config.properties").getFile()), lines );
		} catch (IOException e) {			
			e.printStackTrace();
		}
    	
        return Arrays.asList(new FakeMuleApplication("api", new String[] {apiFile}),
        		new FakeMuleApplication("proxy", new String[] {proxyFile}));
    }

    @Override
    protected List<MuleCoreExtension> getCoreExtensions()
    {
        List<MuleCoreExtension> extensions = super.getCoreExtensions();
        extensions.add(new EndpointAliasesCoreExtension());
        return extensions;
    }

    @Rule
    public DynamicPort port = new DynamicPort("api_port");

    @Rule
    public DynamicPort proxyPort = new DynamicPort("proxy_port");

    @Override
    protected String getPoliciesFromPath()
    {
        return "";
    }

}