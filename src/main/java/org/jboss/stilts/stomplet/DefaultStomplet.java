package org.jboss.stilts.stomplet;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public class DefaultStomplet extends AbstractStomplet implements Stomplet {

    @Override
    public void onMessage(MessageRouter router, StompMessage message) throws StompException {
    }

    @Override
    public void onSubscribe(Subscriber subscriber) throws StompException {
    }

    @Override
    public void onUnsubscribe(Subscriber subscriber) throws StompException {
    }


}
