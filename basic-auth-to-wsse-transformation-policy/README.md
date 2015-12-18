### Basic authentication to Web Service Security transformation policy ###

This policy transforms an incoming requests's Basic authentication based security context into a Web Service Security context. 

The basic authentication context is provided by *Authorization* header. The Authorization header is constructed as follows:

1. Username and password are combined into a string "username:password"
2. The resulting string is then encoded using the RFC2045-MIME variant of Base64
3. The authorization method and a space i.e. "Basic " is then put before the encoded string.

For example:

	Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==

For more information, please see [Basic access authentication](http://en.wikipedia.org/wiki/Basic_access_authentication) source.

The WS-Security context is provided as a SOAP header. See a sample below:

	<?xml version="1.0" encoding="UTF-8"?>
	<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://schemas.microsoft.com/sharepoint/soap/">
	   <soapenv:Header>
	      <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
	         <wsse:UsernameToken xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="UsernameToken-9619833">
	            <wsse:Username>user</wsse:Username>
	            <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">passwd</wsse:Password>
	         </wsse:UsernameToken>
	      </wsse:Security>	      
	   </soapenv:Header>
	   <soapenv:Body>
	      ....
	   </soapenv:Body> 

The policy logic lies in extracting the Authorization header value and decoding its part as it is encoded in Base64. Next, a username and a password are parsed. The policy parses the incoming SOAP message and injects a SOAP security header. If the processing fails, the message is let through unmodified.

#### Configuration

The configuration contains some input parameters: 

+ must undestand value - the value of the soapenv:mustUnderstand attribute in the wsse Security header element
+ The base for the randomly generated username token - the base for the randomly generated username token designed to prevent replay attacks. Leave blank for an empty token.