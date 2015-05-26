### Request XSLT message transformation policy ###

This policy modifies the API's requests based on the provided [XSLT transformation](http://developer.mulesoft.com/docs/display/current/XSLT+Transformer). [XSLT](http://en.wikipedia.org/wiki/XSLT) (Extensible Stylesheet Language Transformations) is a language for transforming XML documents into other XML documents or other formats such as HTML for web pages, plain text or into XSL Formatting Objects.
 
**Note**: It is not possible to configure the encoding and mime type of the transformed message because of how the Cloudhub proxy works. Even if these headers are set by the policy, they are overwritten by the proxy implementation. To overcome this, you would need to develop a custom proxy. You can find more information about the proxy default implementation [here](http://developer.mulesoft.com/docs/display/current/Proxying+Your+API).   

#### Configuration

The policy configuration contains three input parameters:

+ Path to a request XSLT file - specifies the path to a XSLT file used for transforming incoming messages.
+ The maximum active transformers - specifies the total number of XSLT transformers that will get pooled at any given time. 
+ The maximum idle transformers - transformers are pooled for better throughput, since performing and XSL transformation can be expensive. This parameter controls how many instances will remain idle in the transformer pool.