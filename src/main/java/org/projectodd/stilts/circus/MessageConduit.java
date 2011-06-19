package org.projectodd.stilts.circus;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.Headers;
import org.projectodd.stilts.spi.Subscription;

public interface MessageConduit {
    void send(StompMessage stompMessage) throws Exception;
    Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception;
}
