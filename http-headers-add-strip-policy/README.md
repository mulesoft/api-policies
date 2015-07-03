### HTTP headers add/strip policy ###

This policy adds or removes specific HTTP headers from the incoming requests or responses of an API version so that it is possible to control the shape of messages at the API gateway layer. 

#### Configuration

The policy configuration contains several input parameters:

+  Response headers to remove - Separator-separeted list of HTTP headers to be removed from the response, e.g. Date|Vary
+  1. Request header name to add
+  1. Request header value to add 
+  2. Request header name to add
+  2. Request header value to add
+  3. Request header name to add
+  3. Request header value to add
+  1. Response header name to add
+  1. Response header value to add
+  2. Response header name to add
+  2. Response header value to add
+  3. Response header name to add
+  3. Response header value to add

The current version of the policy allows adding three HTTP headers, structured in the name/value pairs, to the request and three HTTP headers to the response message.
All of the parameters are optional.
 
**Note**:The HTTP header will not be added if the header name is empty.