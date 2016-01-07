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

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class PolicyYamlTestCase {
	
	private Logger logger = LogManager.getLogger(PolicyYamlTestCase.class); 
	private static final String POLICY_NAME = "basic-auth-to-wsse-transformation-policy";
	private Map<String, Map<String, String>> expectedParameters = new HashMap<>();
	private String id = "basic-auth-to-wsse-transformation-policy";
	private String name = "Basic authentication to WS-Security transformation policy";
	private String description = "Transforms an incoming request's Basic authentication based security context into a WSSE context.";
	private String category = "Security";
	
	@Before
	public void prepare(){	
		expectedParameters.put("mustUnderstand", initParameter("string", "mustUnderstand", "1"));
		expectedParameters.put("useActor", initParameter("string", "useActor", "UsernameToken-"));
	}

	private Map<String, String> initParameter(String type, String name, String defaultValue) {
		Map<String, String> parameter = new HashMap<>();
		parameter.put("type", type);
		parameter.put("propertyName", name);
		parameter.put("defaultValue", defaultValue);
		return parameter;
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
			
			for (Map<String, Object> parameter : configMap.values()){
				testParameterAttribute(parameter, "type");
				testParameterAttribute(parameter, "propertyName");
				testParameterAttribute(parameter, "defaultValue");
			}
		} catch (FileNotFoundException | YamlException e) {
			logger.error("Error reading YAML file: " + e);
			e.printStackTrace();
		}
        
	}
	
	private void testParameterAttribute(Map<String, Object> parameter, String attributeName){
		assertEquals(attributeName + " should be set for " + parameter.get("propertyName"), 
				expectedParameters.get(parameter.get("propertyName")).get(attributeName), parameter.get(attributeName));
	}
}
