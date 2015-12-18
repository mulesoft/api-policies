### JWT policy ###

This policy enables a user to verify validity of JSON Web Token contained in Authorization header of HTTP request. Policy checks correctness of JWT claims Issuer, Audience, Expiration time and verifies the signature. In case of any of these are invalid, the message is discarded.

Policy supports these algorithms for digital signature verification: 

- HmacSHA256, HmacSHA384 and HmacSHA512
- SHA256withRSA, SHA384withRSA and SHA512withRSA

HTTP Authorization header must have following form:

    Bearer <jwt>

like this example:

    Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ

#### Configuration

The policy configuration contains several input parameters:

+  Secret - specifies a secret (in case of HmacSHA algorithms) or a public key (SHAwithRSA algorithms) for JWT digital signature algorithm. A public key is inserted without BEGIN and END parts, i.e.:

		MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArLrCUilXj+ggjh/kq/EhEsAXWhyF511k
		tdddFxuOts6ZEcq9u9OP1WnXO14qvZwsd4RBJ3RVm9bMQQzlproMlhkjihsz4ETQS8Ko3e3N0j6+
		is+jwX5hOVRu7WrD+iqE8AcoNtkTf8YwntHqWGMxzQSl57VQ7NSf4a/VSSBKW3oZy0tYQMZECZow
		aLlfjgPibrw9TGwYPceF0e203HuF9fSfqvlGSrr8QPDmwT3Tvp96yF3nwTDiTGdL1YTSUI8SFjzF
		STNVCTKc9P0e9MMdE28nZL9NDPWmi/DnYs6t32uolMc2erEd5OjSQ7Rry58Jt6IaURC93xuN9wir
		mxp8UQIDAQAB
	  
+  Issuer - defines an issuer of JWT token
+  Audience - requested audience of JWT token.

**Note**: A JWT token can be signed with any of the supported algorithms but the policy will use a parameter *Secret* in all cases so only one algorithm group will work.

