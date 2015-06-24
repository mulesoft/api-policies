### Response caching policy ###

This policy enables the following behavior:

Upon the application of the policy, the first request that hits the affected endpoint is executed fully and it's response is cached within the Mule object store. Subsequent requests then lead to the retrieval of of the cached value for the response (if there is a hit) instead of actually executing the endpoint's flow fully.

For more information about caching in Mule, please visit [Cache Scope](https://developer.mulesoft.com/docs/display/current/Cache+Scope) resource.

The policy is functional also in the cluster environment. To learn more, visit [Configuring Mule HA Clustering](https://developer.mulesoft.com/docs/display/current/Configuring+Mule+HA+Clustering).

#### Configuration

The policy configuration contains these input parameters:

+  Time to Live (TTL) - indicates the amount of time that the cached response is kept in the cache, specified in milliseconds. 
+  Expiration Interval - the interval for periodic bounded size enforcement and entry expiration, specified in milliseconds.
+  MEL Key expression - the MEL expression that is applied to the request in order to compute its key and perform a lookup.