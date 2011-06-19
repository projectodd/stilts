package org.jboss.stilts.circus;

import org.jboss.stilts.spi.Subscription;

public abstract class CircusSubscription implements Subscription {

    public CircusSubscription(String id) {
        this.id = id;
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    private String id;

}
