### Web Service Security to Basic authentication transformation policy ###

This policy transforms an incoming requests's WS-Security based security context into a basic authentication security context. 
The WS-Security context is provided as a SOAP header holding a [Username Token](https://www.oasis-open.org/committees/download.php/13392/wss-v1.1-spec-pr-UsernameTokenProfile-01.htm#_Toc104276211) - a username and a password. See a sample below:

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

The basic authentication context is provided by *Authorization* header.

The Authorization header is constructed as follows:

1. Username and password are combined into a string "username:password"
2. The resulting string is then encoded using the RFC2045-MIME variant of Base64
3. The authorization method and a space i.e. "Basic " is then put before the encoded string.

For example:

	Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==

The policy logic lies in extracting the username and password from the XML file. These values are used as the input parameters for the aforementioned algorithm. The endpoint configured with the basic authentication then grants or denies the access based on the algorithm output. 

For more information, please see [Basic access authentication](http://en.wikipedia.org/wiki/Basic_access_authentication) source.

#### Configuration

There is no configuration required before applying this policy.