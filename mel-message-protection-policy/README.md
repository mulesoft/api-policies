### MEL message protection policy ###

This policy specifies a MEL query expression which when evaluated against incoming messages and returning true, rejects the incoming message. This allows controlling the types of messages that get past the API gateway proxy.

#### Configuration

The policy configuration contains two input parameters:

+  MEL query - defines a query for filtering incoming messages
+  Violation message - defaults to "Access denied" message. The return HTTP status code is 403 Forbidden.