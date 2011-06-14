package org.jboss.stilts.stomplet;

import org.jboss.stilts.MessageSink;

public interface Subscriber extends MessageSink {
    
    String getId();
    String getDestination();

}
