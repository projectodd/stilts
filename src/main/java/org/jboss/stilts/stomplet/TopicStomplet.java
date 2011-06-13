package org.jboss.stilts.stomplet;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public class TopicStomplet extends SubscribableStomplet {

    @Override
    public void onMessage(MessageRouter router, StompMessage message) throws StompException {
        sendToAllSubscribers( message );
    }

}