### Request/response logging policy ###

This policy enables a user to log request/response message data. The default behavior is to log the predefined data about each request and response message. By enabling payload logging, the policy logs both the request and response payload. Furthermore, it is possible to log analytic data and restrict logging only to the specific resource or the HTTP method.

**Note**: The endpoint settings for Cloudhub proxy must be of the RAML type in order for resource/method filtering to work.   

#### Configuration

The policy configuration contains several input parameters:

+  Log Payload - allows logging a message payload
+  Log Analytics - allows logging analytics data 
+  Resource filter - controls the specific resource pattern on which the policy should be applied
+  Method filter - controls the specific HTTP method pattern on which the policy should be applied
+  Log level - defines Logger level. Allowed values are:
	+   DEBUG
	+   WARN
	+   ERROR
	+   INFO

**Note:** Setting Log level requires log configuration on the application side as well. 