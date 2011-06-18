package org.jboss.stilts.stomplet.simple;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public class SimpleTopicStomplet extends SimpleSubscribableStomplet {

    @Override
    public void onMessage(StompMessage message) throws StompException {
        sendToAllSubscribers( message );
    }

}