package org.mule.util;


import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

/**
 * Adds an authorization header to HTTP request
 * @author Miroslav Rusin
 *
 */
public class AddAuthorizationHeaderFilter implements ClientRequestFilter {

	    private final String authToken;

	    public AddAuthorizationHeaderFilter(String token) {
	        authToken = token;
	    }
	    
		public void filter(ClientRequestContext requestContext) throws IOException {
			requestContext.getHeaders().add("Authorization", "Bearer " + authToken);
		}
	
}
