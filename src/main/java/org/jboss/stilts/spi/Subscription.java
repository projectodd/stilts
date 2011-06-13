package org.jboss.stilts.spi;

import org.jboss.stilts.StompException;

public interface Subscription {
    String getId();
    void cancel() throws StompException;
}
