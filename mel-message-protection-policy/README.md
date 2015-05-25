### MEL message protection policy ###

This policy specifies a [Mule Expression Language](http://developer.mulesoft.com/docs/display/current/Mule+Expression+Language+MEL) (MEL) query expression which when evaluated against incoming messages and returning true, rejects the incoming message. This allows controlling the types of messages that get past the API gateway proxy. For instance, if the MEL is: 

	#[message.inboundProperties['http.method'] == 'POST']

the policy will reject all POST requests and return a JSON specifying the error message: 
	
	{
	    "error": "Policy 15214: Access denied"
	}

#### Configuration

The policy configuration contains two input parameters:

+  MEL query - defines a query for filtering incoming messages
+  Violation message - defaults to "Access denied" message. The return HTTP status code is 403 Forbidden.