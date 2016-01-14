package org.mule.policies;

import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.AbstractPolicyTestCase;

public class RestTestCase extends AbstractPolicyTestCase {
		
	private static final String PROXY_FILE = "rest-proxy/proxy.xml";
	private static final String API_FILE = "rest-api/api.xml";
		
    public RestTestCase()
    {
        super(PROXY_FILE, API_FILE);
    }
    
    @Before
    public void compilePolicy() {    	
    	
    	parameters.put("policyId", "1");
        parameters.put("apiName", "sampleApi");
        parameters.put("apiVersionName", "1.0.0");
        
        parameters.put("key", "#[message.inboundProperties['http.query.string']]");
        parameters.put("ttl", "3000");
        endpointURI = "http://localhost:" + proxyPort.getNumber();
        super.compilePolicy();
    }        
    
    @Test
    public void testSuccessfulRequest() throws InterruptedException
    {    	    
    	endpointURI = "http://localhost:" + proxyPort.getNumber() + QUERY;	    	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("get");        
        
		long durationNotCached = getRequestDuration(assertResponseBuilder, true);
		long durationCached = getRequestDuration(assertResponseBuilder, true);
		
		assertTrue("Response time should be lowered after caching", (durationNotCached - durationCached) > 300);
    }
    
    @Test
    public void testCacheRenewal() throws InterruptedException
    {    	    
    	endpointURI = "http://localhost:" + proxyPort.getNumber() + QUERY;	    	
		AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("get");        
        
		long durationNotCached = getRequestDuration(assertResponseBuilder, true);
		Thread.sleep(10000);		
		endpointURI = "http://localhost:" + proxyPort.getNumber() + QUERY;
		assertResponseBuilder = new AssertEndpointResponseBuilder(endpointURI, "get");
		long durationCached = getRequestDuration(assertResponseBuilder, true);
		
		assertTrue("Response time should be lowered after caching", (durationNotCached - durationCached) < 300);
    }	
      
    @Test
    public void testMissingKey() throws InterruptedException
    {    	    
    	AssertEndpointResponseBuilder assertResponseBuilder = applyPolicyAndTest("get");        
        
		long durationNotCached = getRequestDuration(assertResponseBuilder, true);
		long durationCached = getRequestDuration(assertResponseBuilder, true);
		assertTrue("Response time should be lowered after caching", (durationNotCached - durationCached) > 50);
		
    }
    
    @Test
    public void testConcurrency() throws InterruptedException
    {       	
    	applyPolicyAndTest("post");        
    	int count = 8;
    	
    	ExecutorService executor = Executors.newFixedThreadPool(count);
    	CountDownLatch latch = new CountDownLatch(count);
    	Random random = new Random();
    	
    	for (int i = 0; i < count; i++){
	    	executor.submit(new Runnable() {
				
				@Override
				public void run() {
					AssertEndpointResponseBuilder assertResponseBuilder = new AssertEndpointResponseBuilder(endpointURI + "?n=" + (random.nextInt(2) + 1), "get");
					long durationNotCached = getRequestDuration(assertResponseBuilder, false);
					long durationCached = getRequestDuration(assertResponseBuilder, false);
					assertTrue("Response time should be lowered after caching", durationNotCached > durationCached);
					latch.countDown();
				}
			});
    	}
    	latch.await();
    	executor.shutdown();
    							
    } 
}
