
package org.projectodd.stilts.stomplet;

import org.projectodd.stilts.stomp.Subscription.AckMode;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomp.spi.StompSession;

public interface Subscriber extends AcknowledgeableMessageSink {
    
    String getId();
    String getSubscriptionId();
    String getDestination();
    AckMode getAckMode();
    StompSession getSession();

}
