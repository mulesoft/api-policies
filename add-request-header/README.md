# Description

This policy can be used by API Gateway to add certain headers to the request, before hitting the backend service. You can add as much headers as you want, or you can overwrite a header in case it already exists in the message (probably provided by the client).

Example use case:

Suppose you have a backend service that handles the requests in a different way depending on the headers that receives. You can intercept the message and add/overwrite some headers to give it a different priority, group it, or even discard it.

# Configuration

After adding the custom policy to API Platform, you will have the possibility to include it in your API.
If you apply it, you will see a key-value tuple where you can include the header name and its value. If your client is already sending that header, its value will change by the one you write in that map. 
After completing those text areas, you have to press the plus button. This action will include your header in the policy and will let you include another one.
Once you finished including your headers, you can press Apply and the policy will start working (if you have an API Gateway already paired to your API).
You can also edit it whenever you want, by clicking the Edit button.
