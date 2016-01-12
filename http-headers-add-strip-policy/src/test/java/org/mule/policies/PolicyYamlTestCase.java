package org.mule.policies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mule.policies.commons.ConfigurationParameter;
import org.mule.policies.commons.ConfigurationParameter.Builder;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class PolicyYamlTestCase {
	
	private Logger logger = LogManager.getLogger(PolicyYamlTestCase.class); 
	private static final String POLICY_NAME = "http-headers-add-strip-policy";
	private Map<String, ConfigurationParameter> expectedParameters = new HashMap<>();
	private String id = "http-headers-add-strip-policy";
	private String name = "HTTP headers add/strip policy";
	private String description = "Adds or removes specific HTTP headers from the message";
	private String category = "Control";
	
	@Before
	public void prepare(){
		ConfigurationParameter.Builder builder = new Builder();
		
		for (int i = 1; i < 4; i++){
			expectedParameters.put("request-header-to-add" + i, 
					builder
					.withType("string")
					.withName(i + ". Request header name to add")
					.withDescription("Header name to be added to the request")					
					.withPropertyName("request-header-to-add" + i)
					.withOptional(true)
					.withSensitive(false)
					.build());
			expectedParameters.put("request-value-to-add" + i, 
					builder
					.withName(i + ". Request header value to add")
					.withDescription("Header value to be added to the request")
					.withType("string")
					.withPropertyName("request-value-to-add" + i)
					.withOptional(true)
					.withSensitive(false)
					.build());
					
			expectedParameters.put("response-header-to-add" + i, 
					builder
					.withType("string")
					.withName(i + ". Response header name to add")
					.withDescription("Header name to be added to the response")
					.withPropertyName("response-header-to-add" + i)
					.withOptional(true)
					.withSensitive(false)
					.build());
			expectedParameters.put("response-value-to-add" + i, 
					builder
					.withType("string")
					.withName(i + ". Response header value to add")
					.withDescription("Header value to be added to the response")
					.withPropertyName("response-value-to-add" + i)
					.withOptional(true)
					.withSensitive(false)
					.build());
		}
		
		expectedParameters.put("response-headers-to-remove", 
				builder
				.withType("string")
				.withName("Response headers to remove")
				.withDescription("Separator-separeted list of HTTP headers to be removed from the response")
				.withPropertyName("response-headers-to-remove")
				.withOptional(true)
				.withSensitive(false)
				.build());
		
	}	
	
	@Test
	@SuppressWarnings("unchecked")
	public void testYaml(){
		YamlReader reader;
		try {
			reader = new YamlReader(new FileReader(POLICY_NAME + ".yaml"));
			
			Map<String, Object> policyDefinition = (Map<String, Object>)reader.read();
			assertEquals("Id should be set", policyDefinition.get("id"), id);
			assertEquals("Category should be set", policyDefinition.get("category"), category);
			assertEquals("Name should be set", policyDefinition.get("name"), name);
			assertEquals("Description should be set", policyDefinition.get("description"), description);
			
			ArrayList<Map<String, Object>> configuration = (ArrayList<Map<String, Object>>) (policyDefinition).get("configuration");
			Map<String, Map<String, Object>> configMap = new HashMap<>();
			
			for (Map<String, Object> parameter : configuration){
				configMap.put(parameter.get("propertyName").toString(), parameter);
			}
			
			assertEquals("Number of parameters should match", expectedParameters.size(), configuration.size());
			
			for (ConfigurationParameter configParam : expectedParameters.values()){
				Map<String, Object> parameter = configMap.get(configParam.getPropertyName());
				assertEquals("type should be set for " + parameter.get("propertyName"), 
						configParam.getType(), parameter.get("type"));
				assertEquals("propertyName should be set for " + parameter.get("propertyName"), 
						configParam.getPropertyName(), parameter.get("propertyName"));
				assertEquals("optional should be set for " + parameter.get("propertyName"), 
						configParam.isOptional(), Boolean.valueOf(parameter.get("optional").toString()));
				assertEquals("sensitive should be set for " + parameter.get("propertyName"), 
						configParam.isSensitive(), Boolean.valueOf(parameter.get("sensitive").toString()));							
				assertEquals("name should be set for " + parameter.get("propertyName"), 
						configParam.getName(), parameter.get("name"));							
				assertEquals("description should be set for " + parameter.get("propertyName"), 
						configParam.getDescription(), parameter.get("description"));			
			}
		} catch (FileNotFoundException | YamlException e) {
			logger.error("Error reading YAML file: " + e);
			assertTrue("Error reading YAML file", false);
		}
        
	}
		
}
