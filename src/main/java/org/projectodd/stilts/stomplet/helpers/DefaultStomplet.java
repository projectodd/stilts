package org.projectodd.stilts.stomplet.helpers;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.Subscriber;

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
