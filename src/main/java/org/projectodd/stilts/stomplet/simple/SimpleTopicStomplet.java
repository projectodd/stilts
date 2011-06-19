package org.projectodd.stilts.stomplet.simple;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;

public class SimpleTopicStomplet extends SimpleSubscribableStomplet {

    @Override
    public void onMessage(StompMessage message) throws StompException {
        System.err.println( "+++++++ onMessage(" + message + ")" );
        sendToAllSubscribers( message );
    }

}