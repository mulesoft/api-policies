### Web Service Security Username Token validation policy ###

The policy validates WSS Username Token assertions that are attached to inbound SOAP requests. It heavily relies on [CXF component](http://www.mulesoft.org/documentation/display/current/CXF+Module+Reference) which can be configured for Web security functionality. The *UsernameToken Signature* is specified as a WS configuration action. Specifying *UsernameToken* performs a Username Token validation and *Signature* message signature validation.

**Note**: This policy, as it requires advanced configuration, is usable only with your local API Gateway.

You can find more information about CXF framework [here](http://cxf.apache.org/docs/ws-security.html).  

You need to specify a path to the crypto file that contains some Web Security configuration data. A sample of the crypto file:

	org.apache.ws.security.crypto.provider=org.apache.ws.security.components.crypto.Merlin
	org.apache.ws.security.crypto.merlin.keystore.type=jks
	org.apache.ws.security.crypto.merlin.keystore.password=![IEGP6Z0S7jyluhQm/eYJqDa6eeYBGMPvCeGJXaaCpX8=]
	org.apache.ws.security.crypto.merlin.keystore.alias=joe
	org.apache.ws.security.crypto.merlin.file=resources/keystore.jks

**Note**: To prevent storing the keystore password in plain text, it was encrypted using Mule Credentials Vault, an Anypoint Enterprise Security module. You can find reference to this module [here](http://www.mulesoft.org/documentation/display/current/Mule+Credentials+Vault).  

The *org.apache.ws.security.crypto.merlin.file* specifies a path to a keystore. You need to place it there in order for a policy to function.

After a message passed this validation, you can be sure of this:

1. The policy will parse the XML. If parsing fails then it will raise a fault.
2. The policy will validate the XML digital signature.
3. The policy will check the current timestamp against the NotBefore and NotOnOrAfter elements in the assertion.
4. The policy will validate a username and a password. If invalid, it will raise a fault. 

The signed Username assertion takes a following form:

	<?xml version="1.0" encoding="UTF-8"?>
	<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   	 <soap:Header>
      <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" soap:mustUnderstand="1">
         <wsu:Timestamp wsu:Id="TS-26">
            <wsu:Created>2015-05-12T08:49:16.319Z</wsu:Created>
            <wsu:Expires>2015-05-12T08:54:16.319Z</wsu:Expires>
         </wsu:Timestamp>
         <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" Id="SIG-25">
            <ds:SignedInfo>
               <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#">
                  <ec:InclusiveNamespaces xmlns:ec="http://www.w3.org/2001/10/xml-exc-c14n#" PrefixList="soap" />
               </ds:CanonicalizationMethod>
               <ds:SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1" />
               <ds:Reference URI="#id-24">
                  <ds:Transforms>
                     <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#">
                        <ec:InclusiveNamespaces xmlns:ec="http://www.w3.org/2001/10/xml-exc-c14n#" PrefixList="" />
                     </ds:Transform>
                  </ds:Transforms>
                  <ds:DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1" />
                  <ds:DigestValue>Pt9u7O39YmaPa6RmS4a8vxtNAaU=</ds:DigestValue>
               </ds:Reference>
            </ds:SignedInfo>
            <ds:SignatureValue>cApym/4Wokk1w4y6Nii/lHb0kmKz5C9zG9tf7ISKhgYPl4KGcO7Hn0HBjiSGafy+OAblRUbhUtkET7JSCAXxqJnDLLP4Pq+NnjI75TX4HOU96oMyQ9Wb/EbbTu15+RTPcWNkWllVWycAg/MvOfenWZ1KA39PF7Zl5MyB5QM6MgE=</ds:SignatureValue>
            <ds:KeyInfo Id="KI-FFDE2A6B757BD3E654143142055631529">
               <wsse:SecurityTokenReference wsu:Id="STR-FFDE2A6B757BD3E654143142055631530">
                  <ds:X509Data>
                     <ds:X509IssuerSerial>
                        <ds:X509IssuerName>CN=joe,OU=joe,O=joe,L=joe,ST=joe,C=US</ds:X509IssuerName>
                        <ds:X509SerialNumber>1262035674</ds:X509SerialNumber>
                     </ds:X509IssuerSerial>
                  </ds:X509Data>
               </wsse:SecurityTokenReference>
            </ds:KeyInfo>
         </ds:Signature>
         <wsse:UsernameToken wsu:Id="UsernameToken-23">
            <wsse:Username>joe</wsse:Username>
            <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest">S/BKJramD5JTmAgB83gKt66FXfM=</wsse:Password>
            <wsse:Nonce EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary">oJkROGHlzpOs4+UHVhj8QQ==</wsse:Nonce>
            <wsu:Created>2015-05-12T08:49:16.315Z</wsu:Created>
         </wsse:UsernameToken>
      </wsse:Security>
	   </soap:Header>
	   <soap:Body xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="id-24">
	      ...
	   </soap:Body>
	</soap:Envelope>

For more information on SAML, please visit [Wiki of the OASIS Security Services (SAML) Technical Committee](https://wiki.oasis-open.org/security/FrontPage).

This policy requires one additional installation step: to include a jar file on the API gateway class path. This file should contain a Java class implementing *javax.security.auth.callback.CallbackHandler* interface as it is a mandatory configuration parameter for Web security configuration, see the sample below. You might copy the jar file in *lib/user* directory of your API gateway installation. 
	
	import java.io.IOException;	
	import javax.security.auth.callback.Callback;
	import javax.security.auth.callback.CallbackHandler;
	import javax.security.auth.callback.UnsupportedCallbackException;
	
	import org.apache.ws.security.WSPasswordCallback;
	
	public class PasswordCallback implements CallbackHandler
	{
	    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
	    {
	        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
	
	        if (pc.getIdentifier().equals("joe"))
	        {
	            pc.setPassword("secret");
	        }	        
	    }
	}

For more information on Username Token Authentication, please refer to [this section](http://cxf.apache.org/docs/ws-security.html#WS-Security-UsernameTokenAuthentication).

#### Configuration

The policy configuration contains three input parameters:

+  Path to the crypto file - a path to the crypto properties file. This file needs to be on the classpath in order to be loaded by the platform, e.g. under *conf* directory in your local API gateway. 
+  Password to the crypto file - specifies a password to decrypt the encrypted properties in the crypto file. If the file is not encrypted, you can leave the field blank. 
+  Password Callback name - the fully qualified name of a Java class implementing *javax.security.auth.callback.CallbackHandler*
