package org.mule.policies.commons;

import org.mule.transport.NullPayload;

public abstract class AbstractStandalonePolicyAwareTestCase extends AbstractPolicyAwareMultipleMuleServersTestCase
{
    public static final String NULL_PAYLOAD = NullPayload.getInstance().toString();

    @Override
    protected int getNumberOfNodes()
    {
        return 1;
    }

    protected void addPolicy(String policyName)
    {
        addPolicy(muleServers.get(0), policyName);
    }

    protected void removePolicy(String policyName)
    {
        removePolicy(muleServers.get(0), policyName);
    }

    protected void updatePolicy(String policyName, String updatedPolicyName)
    {
        updatePolicy(muleServers.get(0), policyName, updatedPolicyName);
    }
}
