package org.jboss.stilts.base;

import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Subscription;

public abstract class AbstractSubscription<T extends ClientAgent> implements Subscription {

    public AbstractSubscription(T clientAgent, String id) {
        this.clientAgent = clientAgent;
        this.id = id;
    }
    
    @Override
    public String getId() {
        return this.id;
    }

    protected T getClientAgent() {
        return this.clientAgent;
    }
    
    private T clientAgent;
    private String id;

}
