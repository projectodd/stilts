package org.jboss.stilts.stomplet.helpers;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.stomplet.Stomplet;
import org.jboss.stilts.stomplet.Subscriber;

public class DefaultStomplet extends AbstractStomplet implements Stomplet {

    @Override
    public void onMessage(StompMessage message) throws StompException {
    }

    @Override
    public void onSubscribe(Subscriber subscriber) throws StompException {
    }

    @Override
    public void onUnsubscribe(Subscriber subscriber) throws StompException {
    }


}
