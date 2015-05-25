### Request/response logging policy ###

This policy enables a user to log request/response message data. The default behavior is to log the predefined data about each request and response message. By enabling payload logging, the policy logs both the request and response payload. Furthermore, it is possible to log analytic data and restrict logging only to the specific resource or the HTTP method. Using the following configuration:

	Log Payload	    true
	Log Analytics	true
	Resource filter	.*
	Method filter	.*
	Log level	    DEBUG

you might get logs similar to these:

	10:17:44.135 05/25/2015 DEBUG #################################################
	10:17:44.136 05/25/2015 DEBUG Request [84d36ca0-02b6-11e5-90ad-125ea089bf73]: {
	  "Host" : "sampleapi123.cloudhub.io",
	  "X-Forwarded-Proto" : "http",
	  "User-Agent" : "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36",
	  "Origin" : null,
	  "Cache-Control" : null,
	  "Accept-Language" : "sk-SK,sk;q=0.8,cs;q=0.6,en-US;q=0.4,en;q=0.2",
	  "Accept-Encoding" : "gzip, deflate, sdch",
	  "X-Real-IP" : "88.212.28.50",
	  "X-Forwarded-For" : "88.212.28.50",
	  "X-Forwarded-Port" : "80",
	  "Accept" : "*/*",
	  "Target HTTP Path" : "/products",
	  "Content-Type" : null,
	  "Content-Length" : null,
	  "IP Address" : "/54.86.74.181:35933",
	  "Target HTTP URL" : "http://0.0.0.0:8081/products",
	  "Target HTTP Method" : "GET",
	  "API Analytics" : {
	    "API Name" : "sampleApi",
	    "API Version" : "1.0.0",
	    "Client ID" : "null",
	    "Client IP" : "88.212.28.50",
	    "Method" : "GET",
	    "Resource" : "/products",
	    "Agent" : "'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36'",
	    "Request bytes" : "-1"
	  }
	}
	10:17:44.149 05/25/2015 DEBUG Request Payload [84d36ca0-02b6-11e5-90ad-125ea089bf73]: /products
	10:17:44.153 05/25/2015 INFO  Loading default response transformer: org.mule.transport.http.transformers.MuleMessageToHttpResponse
	10:17:44.175 05/25/2015 DEBUG ##################################################
	10:17:44.175 05/25/2015 DEBUG Response [84e6a680-02b6-11e5-90ad-125ea089bf73]: {
	  "Content-Type" : "application/xml",
	  "Content-Length" : "210",
	  "IP Address" : "mocksvc.mulesoft.com/54.235.65.148:80",
	  "Date" : "Mon, 25 May 2015 08:17:44 GMT",
	  "Vary" : "Accept-Encoding",
	  "HTTP Status" : "200"
	}
	10:17:44.179 05/25/2015 DEBUG Response Payload [84e6a680-02b6-11e5-90ad-125ea089bf73]: [
	  {
	    "productCode": "TS",
	    "size": "S",
	    "description": "Small T-shirt",
	    "count": 30
	  },
	  {
	    "productCode": "TS",
	    "size": "M",
	    "description": "Medium T-shirt",
	    "count": 22
	  }
	]
	

**Note**: The endpoint settings for Cloudhub proxy must be of the RAML type in order for resource/method filtering to work.   

#### Configuration

The policy configuration contains several input parameters:

+  Log Payload - allows logging a message payload
+  Log Analytics - allows logging analytics data 
+  Resource filter - controls the specific resource pattern on which the policy should be applied, e.g. */products* to log requests related to products resource exclusively.
+  Method filter - controls the specific HTTP method pattern on which the policy should be applied, e.g. *GET* to log only GET requests.
+  Log level - defines Logger level. Allowed values are:
	+   DEBUG
	+   WARN
	+   ERROR
	+   INFO

**Note:** Setting Log level requires log configuration on the application side as well. 