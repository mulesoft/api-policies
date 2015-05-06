### Web Service Security SAML assertion validation policy ###

The policy validates WSS Security Assertion Markup Language (SAML) assertions that are attached to inbound SOAP requests. It heavily relies on [CXF component](http://www.mulesoft.org/documentation/display/current/CXF+Module+Reference) which can be configured for Web security functionality. The following configuration properties are put to use:

	action 						SAMLTokenUnsigned Signature		   
	signaturePropFile			<Path to the crypto file>

Specifying *SAMLTokenUnsigned* performs an unsigned SAML Token validation and *Signature* message signature validation.

You can find more information about CXF framework [here](http://cxf.apache.org/docs/ws-security.html).  

After a message passed this validation, you can be sure of this:

1. The policy will parse the XML. If parsing fails then it will raise a fault.
2. The policy will validate the XML digital signature.
3. The policy will check the current timestamp against the NotBefore and NotOnOrAfter elements in the assertion.

The SAML assertion takes a following form:

	<?xml version="1.0" encoding="UTF-8"?>
	<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
	   <soap:Header>
	      <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" soap:mustUnderstand="1">
	         <wsse:BinarySecurityToken EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary" ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3" wsu:Id="CertId-976589B45E238CB33D14309144503724">...</wsse:BinarySecurityToken>
	         <saml2:Assertion xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" ID="_976589B45E238CB33D14309144502841" IssueInstant="2015-05-06T12:14:10.303Z" Version="2.0" xsi:type="saml2:AssertionType">
	            <saml2:Issuer>www.example.com</saml2:Issuer>
	            <saml2:Subject>
	               <saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified" NameQualifier="www.example.com">AllowGreetingServices</saml2:NameID>
	               <saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:sender-vouches" />
	            </saml2:Subject>
	            <saml2:Conditions NotBefore="2015-05-06T12:14:10.337Z" NotOnOrAfter="2015-05-06T12:19:10.337Z" />
	            <saml2:AuthnStatement AuthnInstant="2015-05-06T12:14:10.331Z">
	               <saml2:AuthnContext>
	                  <saml2:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</saml2:AuthnContextClassRef>
	               </saml2:AuthnContext>
	            </saml2:AuthnStatement>
	         </saml2:Assertion>
	         <wsse:SecurityTokenReference xmlns:wsse11="http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd" wsse11:TokenType="http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0" wsu:Id="STRSAMLId-976589B45E238CB33D14309144503725">
	            <wsse:KeyIdentifier ValueType="http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLID">_976589B45E238CB33D14309144502841</wsse:KeyIdentifier>
	         </wsse:SecurityTokenReference>
	         <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" Id="SIG-2">
	            <ds:SignedInfo>
	               <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#">
	                  <ec:InclusiveNamespaces xmlns:ec="http://www.w3.org/2001/10/xml-exc-c14n#" PrefixList="soap" />
	               </ds:CanonicalizationMethod>
	               <ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1" />
	               <ds:Reference URI="#id-1">
	                  <ds:Transforms>
	                     <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#">
	                        <ec:InclusiveNamespaces xmlns:ec="http://www.w3.org/2001/10/xml-exc-c14n#" PrefixList="" />
	                     </ds:Transform>
	                  </ds:Transforms>
	                  <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" />
	                  <ds:DigestValue>XtsXdFCVHmHYz3EGE+wyodqCoZQ=</ds:DigestValue>
	               </ds:Reference>
	               <ds:Reference URI="#STRSAMLId-976589B45E238CB33D14309144503725">
	                  <ds:Transforms>
	                     <ds:Transform Algorithm="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#STR-Transform">
	                        <wsse:TransformationParameters>
	                           <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" />
	                        </wsse:TransformationParameters>
	                     </ds:Transform>
	                  </ds:Transforms>
	                  <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" />
	                  <ds:DigestValue>Yvpm7DFNSC0cH8vUq/t7KakpTyo=</ds:DigestValue>
	               </ds:Reference>
	            </ds:SignedInfo>
	            <ds:SignatureValue>BwLNWaGew2MWSgBpnMzT8n4Au9Ztl7kuztqkKp0u9TTZpgQWoHmxdGDhHrQq+7IcC4YNk315KBJg4Z6NGcrwtQYMgYIJOp7BobTmS3Wz24dBamo6k4dTn2wRoj/WnN1Xbr1KkhSTRcdL/shMfXbDGXi7Inpkn57+3zGs0Bq8CVQ=</ds:SignatureValue>
	            <ds:KeyInfo Id="KeyId-976589B45E238CB33D14309144503702">
	               <wsse:SecurityTokenReference wsu:Id="STRId-976589B45E238CB33D14309144503723">
	                  <wsse:Reference URI="#CertId-976589B45E238CB33D14309144503724" ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3" />
	               </wsse:SecurityTokenReference>
	            </ds:KeyInfo>
	         </ds:Signature>
	      </wsse:Security>
	   </soap:Header>
	   <soap:Body xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="id-1">
	      ....
	   </soap:Body>
	</soap:Envelope> 

You need to specify a path to the crypto file that contains some Web Security configuration data.
For more information on SAML, please visit [Wiki of the OASIS Security Services (SAML) Technical Committee](https://wiki.oasis-open.org/security/FrontPage).

#### Configuration

The policy configuration contains a single input parameter:

+  Path to the crypto file - a path to the crypto properties file. This file needs to be on the classpath in order to be loaded by the platform, e.g. under *conf* directory in your local API gateway. 

