### Request/response logging policy without filtering###

This policy enables a user to log request/response message data. The default behavior is to log the predefined data about each request and response message. By enabling payload logging, the policy logs both the request and response payload. Furthermore, it is possible to log analytic data. Using the following configuration:

	Log Payload	    true
	Log Analytics	true
	Log level	    DEBUG

you might get logs similar to these:

	DEBUG 2015-12-11 10:15:22,072 [[api-gateway].http-lc-0.0.0.0-8081.worker.01] org.mule.api.processor.LoggerMessageProcessor: #################################################
	DEBUG 2015-12-11 10:15:22,073 [[api-gateway].http-lc-0.0.0.0-8081.worker.01] org.mule.api.processor.LoggerMessageProcessor: Request [b4958df0-9fe7-11e5-acdf-3c970ee14ba5]: {
	"Inbound Properties" : {
	    "accept-encoding" : "gzip, deflate, sdch",
	    "http.query.string" : "",
	    "http.remote.address" : "/127.0.0.1:3541",
	    "http.version" : "HTTP/1.1",
	    "user-agent" : "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36",
	    "http.query.params" : { },
	    "http.scheme" : "http",
	    "connection" : "keep-alive",
	    "http.method" : "GET",
	    "accept" : "*/*",
	    "http.uri.params" : { },
	    "http.request.uri" : "/products",
	    "http.relative.path" : "/products",
	    "accept-language" : "sk-SK,sk;q=0.8,cs;q=0.6,en-US;q=0.4,en;q=0.2",
	    "host" : "localhost:8081",
	    "http.request.path" : "/products",
	    "http.listener.path" : "/*",
	    "cache-control" : "no-cache",
	    "cookie" : "jforumUserId=1",
	    "content-type" : "application/xml"
	  },
	"Outbound Properties" : { },
	"HTTP URL" : "localhost:8081/products",
	"API Analytics" : {
	    "API Name" : "raml-test",
	    "API Version" : "1",
	    "Client ID" : "null",
	    "Client IP" : "127.0.0.1",
	    "Method" : "GET",
	    "Resource" : "/products",
	    "Agent" : "'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36'",
	    "Request bytes" : "-1"	
	  }
	}
	DEBUG 2015-12-11 10:15:22,077 [[api-gateway].http-lc-0.0.0.0-8081.worker.01] org.mule.api.processor.LoggerMessageProcessor: Request Payload [b4958df0-9fe7-11e5-acdf-3c970ee14ba5]: null
	INFO  2015-12-11 10:15:22,124 [[api-gateway].http-lc-0.0.0.0-8081.worker.01] org.mule.api.processor.LoggerMessageProcessor: Can't pretty print Payload; XML is not valid. Logging raw paylaod ...
	DEBUG 2015-12-11 10:15:22,125 [[api-gateway].http-lc-0.0.0.0-8081.worker.01] org.mule.api.processor.LoggerMessageProcessor: Request Payload [b4958df0-9fe7-11e5-acdf-3c970ee14ba5]:
	null
	

	DEBUG 2015-12-11 10:15:22,385 [[raml-test-v1-2.x.x(3)].put:/info-gateway-wrapper.01] org.mule.api.processor.LoggerMessageProcessor: ##################################################
	DEBUG 2015-12-11 10:15:22,386 [[raml-test-v1-2.x.x(3)].put:/info-gateway-wrapper.01] org.mule.api.processor.LoggerMessageProcessor: Response [b4c57790-9fe7-11e5-acdf-3c970ee14ba5]: {
	  "Inbound Properties" : {
	    "content-length" : "210",
	    "http.status" : 200,
	    "date" : "Fri, 11 Dec 2015 09:15:22 GMT",
	    "connection" : "keep-alive",
	    "content-type" : "application/xml",
	    "http.reason" : "OK",
	    "vary" : "Accept-Encoding"
	  },
	  "Outbound Properties" : {
	    "http.status" : 200,
	    "date" : "Fri, 11 Dec 2015 09:15:22 GMT",
	    "content-type" : "application/xml",
	    "http.reason" : "OK",
	    "vary" : "Accept-Encoding"
	  }
	}
	DEBUG 2015-12-11 10:15:22,388 [[raml-test-v1-2.x.x(3)].put:/info-gateway-wrapper.01] org.mule.api.processor.LoggerMessageProcessor: Response Payload [b4c57790-9fe7-11e5-acdf-3c970ee14ba5] is of not of type org.mule.api.transport.OutputHandler ...
	INFO  2015-12-11 10:15:22,421 [[raml-test-v1-2.x.x(3)].put:/info-gateway-wrapper.01] org.mule.api.processor.LoggerMessageProcessor: Can't pretty print Payload; XML is not valid. Logging raw paylaod ...
	DEBUG 2015-12-11 10:15:22,422 [[raml-test-v1-2.x.x(3)].put:/info-gateway-wrapper.01] org.mule.api.processor.LoggerMessageProcessor: Response Payload [b4c57790-9fe7-11e5-acdf-3c970ee14ba5]:
	[
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
	
#### Configuration

The policy configuration contains several input parameters:

+  Log Payload - allows logging a message payload
+  Log Analytics - allows logging analytics data 
+  Log level - defines Logger level. Allowed values are:
	+   DEBUG
	+   WARN
	+   ERROR
	+   INFO
	+   TRACE

**Note:** Setting Log level requires log configuration on the application side as well:

	Log level	 DEBUG 
	Package		 org.mule.api.processor
