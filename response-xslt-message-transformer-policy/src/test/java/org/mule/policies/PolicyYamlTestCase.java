package org.mule.policies;

import static org.junit.Assert.assertEquals;

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
	private static final String POLICY_NAME = "response-xslt-message-transformer-policy";
	private Map<String, ConfigurationParameter> expectedParameters = new HashMap<>();
	private String id = "xslt-message-transformer-policy";
	private String name = "Response XSLT Message Transformer Policy";
	private String description = "Modifies the API's responses based on the provided XSLT transformation.";
	private String category = "Transformation";
	
	@Before
	public void prepare(){
		ConfigurationParameter.Builder builder = new Builder();
		
		expectedParameters.put("xslt", 
				builder
				.withType("string")
				.withSensitive(false)
				.withName("XSLT")
				.withDescription("XSLT used for outgoing messages")
				.withPropertyName("xslt")
				.build());
		
		expectedParameters.put("mimetype", 
				builder
				.withType("string")
				.withSensitive(false)
				.withDefaultValue("application/xml")
				.withName("MimeType for the response messages")
				.withDescription("MimeType for the transformed response messages")
				.withPropertyName("mimetype")
				.build());
		
		expectedParameters.put("encoding", 
				builder
				.withType("string")
				.withSensitive(false)
				.withDefaultValue("UTF-8")
				.withName("Encoding for the response messages")
				.withDescription("Encoding for the transformed response messages")
				.withPropertyName("encoding")
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
				assertEquals("sensitive should be set for " + parameter.get("propertyName"), 
						configParam.isSensitive(), Boolean.valueOf(parameter.get("sensitive").toString()));							
				assertEquals("name should be set for " + parameter.get("propertyName"), 
						configParam.getName(), parameter.get("name"));							
				assertEquals("description should be set for " + parameter.get("propertyName"), 
						configParam.getDescription(), parameter.get("description"));							
				
			}
		} catch (FileNotFoundException | YamlException e) {
			logger.error("Error reading YAML file: " + e);
			e.printStackTrace();
		}
        
	}
		
}
