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
	private static final String POLICY_NAME = "basic-auth-to-wsse-transformation-policy";
	private Map<String, ConfigurationParameter> expectedParameters = new HashMap<>();
	private String id = "basic-auth-to-wsse-transformation-policy";
	private String name = "Basic authentication to WS-Security transformation policy";
	private String description = "Transforms an incoming request's Basic authentication based security context into a WSSE context.";
	private String category = "Security";
	
	@Before
	public void prepare(){
		ConfigurationParameter.Builder builder = new Builder();
		
		expectedParameters.put("mustUnderstand", 
				builder
				.withType("string")
				.withDefaultValue("1")
				.withSensitive(false)
				.withName("must understand value")
				.withDescription("the value of the soapenv:mustUnderstand attribute in the wsse Security header element")
				.withPropertyName("mustUnderstand")
				.build());
		expectedParameters.put("useActor", 
				builder
				.withType("string")
				.withDefaultValue("UsernameToken-")
				.withPropertyName("useActor")
				.withName("The base for the randomly generated username token")
				.withDescription("The base for the randomly generated username token designed to prevent replay attacks. Leave blank for an empty token.")
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
			
			for (ConfigurationParameter configParam : expectedParameters.values()){
				Map<String, Object> parameter = configMap.get(configParam.getPropertyName());
				assertEquals("type should be set for " + parameter.get("propertyName"), 
						expectedParameters.get(parameter.get("propertyName")).getType(), parameter.get("type"));
				assertEquals("propertyName should be set for " + parameter.get("propertyName"), 
						expectedParameters.get(parameter.get("propertyName")).getPropertyName(), parameter.get("propertyName"));
				assertEquals("sensitive should be set for " + parameter.get("propertyName"), 
						expectedParameters.get(parameter.get("propertyName")).isSensitive(), Boolean.valueOf(parameter.get("sensitive").toString()));							
				assertEquals("name should be set for " + parameter.get("propertyName"), 
						expectedParameters.get(parameter.get("propertyName")).getName(), parameter.get("name"));							
				assertEquals("description should be set for " + parameter.get("propertyName"), 
						expectedParameters.get(parameter.get("propertyName")).getDescription(), parameter.get("description"));							
				
			}
		} catch (FileNotFoundException | YamlException e) {
			logger.error("Error reading YAML file: " + e);
			e.printStackTrace();
		}
        
	}
		
}
