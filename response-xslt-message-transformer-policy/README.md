### Response XSLT message transformation policy ###

This policy modifies the API's responses based on the provided XSLT transformation. The mime type and the encoding is configurable as well.   

#### Configuration

The policy configuration contains three input parameters:

+ Path to a response XSLT file - specifies the path to a XSLT file used for transforming outgoing messages.
+ MimeType for the response messages - specifies the mime type for the transformed response messages. The default value is *application/xml*.
+ Encoding for the response messages - specifies the encoding for the transformed response messages. The default value is *UTF-8*.
+ The maximum active transformers - specifies the total number of XSLT transformers that will get pooled at any given time.
+ The maximum idle transformers - transformers are pooled for better throughput, since performing and XSL transformation can be expensive. This parameter controls how many instances will remain idle in the transformer pool.