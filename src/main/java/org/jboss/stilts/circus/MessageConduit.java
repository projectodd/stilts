package org.jboss.stilts.circus;

import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;

public interface MessageConduit {
    void send(StompMessage stompMessage) throws Exception;
    Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception;
}
