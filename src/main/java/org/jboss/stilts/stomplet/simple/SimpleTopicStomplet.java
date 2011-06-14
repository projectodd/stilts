package org.jboss.stilts.stomplet.simple;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.stomplet.MessageRouter;

public class SimpleTopicStomplet extends SimpleSubscribableStomplet {

    @Override
    public void onMessage(MessageRouter router, StompMessage message) throws StompException {
        sendToAllSubscribers( message );
    }

}