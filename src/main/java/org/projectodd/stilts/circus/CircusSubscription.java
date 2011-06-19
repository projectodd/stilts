package org.projectodd.stilts.circus;

import org.projectodd.stilts.spi.Subscription;

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
