package org.mule.policies.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.mule.MuleCoreExtension;
import org.mule.tck.probe.PollingProber;
import org.mule.tck.probe.Probe;
import org.mule.tck.probe.Prober;
import org.mule.transport.http.PatchMethod;
import org.mule.util.FileUtils;

import com.google.common.collect.Lists;
import com.mulesoft.anypoint.tests.AbstractMultipleFakeMuleServersTestCase;
import com.mulesoft.anypoint.tests.FakeMuleServer;
import com.mulesoft.module.endpoint.EndpointAliasesCoreExtension;
import com.mulesoft.module.policies.PoliciesCoreExtension;
import com.mulesoft.mule.cluster.boot.ClusterCoreExtension;
import com.mulesoft.mule.plugin.PluginCoreExtension;

public abstract class AbstractPolicyAwareMultipleMuleServersTestCase extends AbstractMultipleFakeMuleServersTestCase
{
    private static final Logger LOGGER = Logger.getLogger(AbstractPolicyAwareMultipleMuleServersTestCase.class);

    private static final int CONNECTION_TIMEOUT = 1000;
    private static final int SO_TIMEOUT = 1000;
    private static final int PROBER_TIMEOUT_MILLIS = 1800;
    private static final int POLL_DELAY_MILLIS = 200;
    
    @Override
    protected List<MuleCoreExtension> getCoreExtensions()
    {
        List<MuleCoreExtension> extensions = super.getCoreExtensions();
        extensions.add(new PluginCoreExtension());
        extensions.add(new ClusterCoreExtension());
        extensions.add(new PoliciesCoreExtension());
        extensions.add(new EndpointAliasesCoreExtension());
        
        return extensions;
    }


    protected class AssertEndpointResponseBuilder
    {
    	
    	private String expectedResult;
        private final String endpointUri;
        private String method;
        private List<String> expectedResults;
        private int expectedStatus;
        private Map<String, String> expectedResponseHeaders = new HashMap<String, String>();
        private List<String> unexpectedResponseHeaders = new ArrayList<String>();
        private List<String> expectedResponseHeadersPresent = new ArrayList<String>();
        private Map<String, String> requestHeaders = new HashMap<String, String>();
        private String payload;
        String responsePayload;
        
        public AssertEndpointResponseBuilder(String endpointUri)
        {
            this(endpointUri, "get");
        }

        public AssertEndpointResponseBuilder(String endpointUri, String method)
        {
            this.endpointUri = endpointUri;            
            this.method = method;
        }

        public AssertEndpointResponseBuilder requestHeader(String name, String value)
        {
            requestHeaders.put(name, value);
            return this;
        }

        public AssertEndpointResponseBuilder requestHeaders(Map<String, String> headers)
        {
            requestHeaders = headers;
            return this;
        }

        public AssertEndpointResponseBuilder setExpectedResult(String expectedResult)
        {
            this.expectedResult = expectedResult;
            return this;
        } 

        public AssertEndpointResponseBuilder setExpectedResults(List<String> expectedResults)
        {
            this.expectedResults = expectedResults;
            return this;
        }

        public AssertEndpointResponseBuilder addExpectedResult(String expectedResult)
        {
            if (this.expectedResults == null)
            {
                this.expectedResults = Lists.newArrayList();
            }
            this.expectedResults.add(expectedResult);
            return this;
        }

        public AssertEndpointResponseBuilder setExpectedStatus(int expectedStatus)
        {
            this.expectedStatus = expectedStatus;
            return this;
        }

        public AssertEndpointResponseBuilder setExpectedResponseHeaders(Map<String, String> expectedResponseHeaders)
        {
            this.expectedResponseHeaders = expectedResponseHeaders;
            return this;
        }

        public AssertEndpointResponseBuilder addExpectedResponseHeader(String responseHeader, String value)
        {
            expectedResponseHeaders.put(responseHeader, value);
            return this;
        }

        public AssertEndpointResponseBuilder addUnexpectedResponseHeader(String unexpectedResponseHeader)
        {
            unexpectedResponseHeaders.add(unexpectedResponseHeader);
            return this;
        }

        public AssertEndpointResponseBuilder checkExpectedResponseHeaderKey(String responseHeader)
        {
            expectedResponseHeadersPresent.add(responseHeader);
            return this;
        }

        public AssertEndpointResponseBuilder clear()
        {
            this.expectedStatus = 0;
            this.payload = null;
            this.expectedResult = null;
            this.expectedResponseHeaders = new HashMap<String, String>();
            this.expectedResponseHeadersPresent = new ArrayList<String>();
            this.unexpectedResponseHeaders = new ArrayList<String>();
            return this;
        }

        public void assertResponse()
        {
            Prober prober = new PollingProber(PROBER_TIMEOUT_MILLIS, POLL_DELAY_MILLIS);
            final HttpClient httpClient = createDefaultHttpClient();
            
            prober.check(new Probe()
            {
                @Override
                public boolean isSatisfied()
                {
                    HttpMethod request = createRequest();
                    try
                    {
                        httpClient.executeMethod(request);
                        String responseBodyAsString = request.getResponseBodyAsString();
                        responsePayload = responseBodyAsString;

                        if (expectedStatus > 0 && request.getStatusCode() != expectedStatus)
                        {
                            return false;
                        }
                        
                        if (expectedResult != null)
                        {
                            if (!responseBodyAsString.equals(expectedResult))
                            {
                                return false;
                            }
                        } 
                        
                        if (expectedResponseHeaders != null)
                        {
                            for (String responseHeader : expectedResponseHeaders.keySet())
                            {
                            	
                                if (request.getResponseHeader(responseHeader) == null ||
                                    !expectedResponseHeaders.get(responseHeader).equals(request.getResponseHeader(responseHeader).getValue()))
                                {
                                    return false;
                                }
                            }
                        }
                        if (expectedResponseHeadersPresent != null)
                        {
                            for (String responseHeader : expectedResponseHeadersPresent)
                            {
                                if (request.getResponseHeader(responseHeader) == null)
                                {
                                    return false;
                                }
                            }
                        }

                        if (unexpectedResponseHeaders != null)
                        {
                            for (String responseHeader : unexpectedResponseHeaders)
                            {
                                if (request.getResponseHeader(responseHeader) != null)
                                {
                                    return false;
                                }
                            }
                        }

                        return true;

                    }
                    catch (Exception e)
                    {
                        return false;
                    }
                }

                @Override
                public String describeFailure()
                {
                    return "Invalid result";
                }
            });
                        
        }

        public String getResponseBody(){
        	return responsePayload;
        }
        
        @SuppressWarnings("deprecation")
		private HttpMethod createRequest()
        {
            HttpMethod request = null;
            if ("get".equalsIgnoreCase(method))
            {
                request = new GetMethod(endpointUri);
            }
            if ("put".equalsIgnoreCase(method))
            {
                request = new PutMethod(endpointUri);
            }
            if ("post".equalsIgnoreCase(method))
            {
                request = new PostMethod(endpointUri);                                
                ((PostMethod)request).setRequestBody(payload);
                
            }
            if ("delete".equalsIgnoreCase(method))
            {
                request = new DeleteMethod(endpointUri);
            }
            if ("patch".equalsIgnoreCase(method))
            {
                request = new PatchMethod(endpointUri);
            }
            if ("head".equalsIgnoreCase(method))
            {
                request = new HeadMethod(endpointUri);
            }
            if ("options".equalsIgnoreCase(method))
            {
                request = new OptionsMethod(endpointUri);
            }
            
            if (request == null)
            {
                throw new RuntimeException("Invalid method: " + method);
            }

            for (Map.Entry<String, String> header : requestHeaders.entrySet())
            {
                request.addRequestHeader(header.getKey(), header.getValue());
            }
            return request;
        }

		public AssertEndpointResponseBuilder setPayload(String payload) {
			this.payload = payload;
			return this;
		}
    }


    private HttpClient createDefaultHttpClient()
    {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(SO_TIMEOUT);
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        return httpClient;
    }

    protected void deployOfflinePolicies(List<String> offlinePolicies) throws FileNotFoundException
    {
        for (FakeMuleServer muleServer : muleServers)
        {
            deployOfflinePolicies(muleServer, offlinePolicies);
        }
    }

    protected void deployOfflinePolicies(FakeMuleServer muleServer, List<String> offlinePolicies) throws FileNotFoundException
    {
        if (offlinePolicies != null && !offlinePolicies.isEmpty())
        {
            File testPoliciesFolder = getFileFromPolicies(null);
            Collection<File> policies = FileUtils.listFiles(testPoliciesFolder, new String[] {"xml"}, true);
            for (String offlinePolicy : offlinePolicies)
            {
                boolean found = false;
                for (File policy : policies)
                {
                    if (policy.getAbsolutePath().endsWith(offlinePolicy))
                    {
                        copyPolicyToDirectory(muleServer, policy);
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    throw new FileNotFoundException(offlinePolicy);
                }
            }
        }
    }

    protected void addPolicy(FakeMuleServer muleServer, String policyName)
    {
        File policy = getFileFromPolicies(policyName);
        copyPolicyToDirectory(muleServer, policy);
    }

    protected void updatePolicy(FakeMuleServer muleServer, String policyName, String updatedPolicyName)
    {
        File policy = new File(muleServer.getPoliciesDir(), policyName);
        File updatedPolicy = getFileFromPolicies(updatedPolicyName);

        try
        {
            FileReader reader = new FileReader(updatedPolicy);
            FileWriter writer = new FileWriter(policy);

            IOUtils.copy(reader, writer);

            writer.close();
            reader.close();
        }
        catch (IOException ioe)
        {
            LOGGER.error("Error updating file " + policyName + " with contents of file " + updatedPolicyName);
        }
    }

    protected void removePolicy(FakeMuleServer muleServer, String policyName)
    {
        try
        {
            FileUtils.forceDelete(new File(muleServer.getPoliciesDir(), policyName));
        }
        catch (IOException ioe)
        {
            LOGGER.error("Error deleting policy " + policyName + " from policies directory.");
        }
    }

    protected File getFileFromPolicies(String name)
    {
        String path = name != null ? "/" + name : "";
        return new File(getClass().getResource(getPoliciesFromPath() + path).getPath());
    }

    protected String getPoliciesFromPath()
    {
        return "/policies";
    }

    protected void copyPolicyToDirectory(FakeMuleServer muleServer, File policy)
    {
        try
        {
            org.apache.commons.io.FileUtils.copyFileToDirectory(policy, muleServer.getPoliciesDir());
        }
        catch (IOException ioe)
        {
            LOGGER.error("Error copying policy " + policy.getName() + " to policies directory.");
        }
    }
}
