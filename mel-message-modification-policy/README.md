### MEL message modification policy ###

This policy specifies a [Mule Expression Language](http://developer.mulesoft.com/docs/display/current/Mule+Expression+Language+MEL) (MEL) message modification expression which when evaluated against incoming messages transforms them. For instance, if the MEL expression is:

	#[message.outboundProperties['key'] = 'value']

a new outbound message property will be added to each incoming message.

#### Configuration

The policy configuration contains a single input parameter:

+  MEL modification expression - defines a MEL expression to be executed upon incoming messages