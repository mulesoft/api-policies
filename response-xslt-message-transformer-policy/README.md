### Response XSLT message transformation policy ###

This policy modifies the API's responses based on the provided [XSLT transformation](http://developer.mulesoft.com/docs/display/current/XSLT+Transformer). [XSLT](http://en.wikipedia.org/wiki/XSLT) (Extensible Stylesheet Language Transformations) is a language for transforming XML documents into other XML documents or other formats such as HTML for web pages, plain text or into XSL Formatting Objects. The mime type and the encoding is configurable.

#### Configuration

The policy configuration contains three input parameters:

+ Path to a response XSLT file - specifies the path to a XSLT file used for transforming outgoing messages.
+ MimeType for the response messages - specifies the mime type for the transformed response messages. The default value is *application/xml*.
+ Encoding for the response messages - specifies the encoding for the transformed response messages. The default value is *UTF-8*.