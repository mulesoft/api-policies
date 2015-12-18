### Spnego Kerberos policy

This policy enforces Kerberos ticket in the request message header. The header should take form of :

	Authorization: Negotiate <Kerberos ticket>

If there is no such header, the policy returns 

	HTTP status: 401
	WWW-Authenticate: Negoatiate

A keytab file is required for Kerberos ticket validation. This file needs to be present on the classpath prior to deploying the policy.

The validation is executed using a custom Java class, contained in the jar file attached to the sources. 
Other dependencies include:

+ spring-security-kerberos-web-1.0.1.RELEASE.jar
+ spring-security-kerberos-core-1.0.1.RELEASE.jar

The jar files should be added to Gateway, e.g. */lib/user* directory.

If the ticket is valid, LDAP lookup is performed using the configured LDAP connection.
If the ticket is invalid or LDAP lookup fails, the policy returns 403 HTTP Status and a JSON error payload:

	{
	    "error": "Policy 32463: Access denied"
	}


#### Configuration

The policy configuration contains several input parameters:

+ Service Principal - Service principal for the gateway
+ Keytab Location - Location of the keytab file
+ LDAP Server URL - URL and port for the LDAP server
+ LDAP User DN - The name of the user or user group with access to the LDAP
+ LDAP User Password - LDAP User password
+ LDAP Base - LDAP Base DN
+ LDAP Search Base - Starting point for search in the directory tree, appended to LDAP Base
+ LDAP Search Filter - Filtering criteria
