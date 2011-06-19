package org.projectodd.stilts;

public class InvalidSubscriptionException extends StompException {

    public InvalidSubscriptionException(String subscriptionId) {
        super( "Invalid subscription id: " + subscriptionId );
        this.subscriptionId = subscriptionId;
    }
    
    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    private static final long serialVersionUID = 1L;

    private String subscriptionId;

}
