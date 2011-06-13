package org.jboss.stilts.stomplet;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public class DefaultStomplet extends AbstractStomplet implements Stomplet {

    @Override
    public void onMessage(MessageRouter router, StompMessage message) throws StompException {
    }

    @Override
    public void onSubscribe(MessageSink consumer) throws StompException {
    }

    @Override
    public void onUnsubscribe(MessageSink consumer) throws StompException {
    }

}
