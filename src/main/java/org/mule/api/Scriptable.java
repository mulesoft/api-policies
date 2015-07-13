package org.mule.api;

import java.util.Map;

/**
 * Interface defining a single method that all EDG scripts must implement
 * @author Miroslav Rusin
 *
 */
public interface Scriptable {
	
	/**
	 * Executs some logic based on input parameters
	 * @param input map of input parameters
	 * @throws Exception if any error occurs
	 */
	void process(Map<String, String> input) throws Exception;
	
}
