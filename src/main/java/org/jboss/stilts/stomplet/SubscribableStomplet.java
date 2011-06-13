package org.jboss.stilts.stomplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public abstract class SubscribableStomplet extends AbstractStomplet implements MessageSink {

    @Override
    public void send(StompMessage message) throws StompException {
        onMessage( null, message );
    }

    @Override
    public void onSubscribe(MessageSink consumer) throws StompException {
        synchronized ( this.subscribers ) {
            this.subscribers.add(  consumer  );
        }
    }

    @Override
    public void onUnsubscribe(MessageSink consumer) throws StompException {
        synchronized ( this.subscribers ) {
            while ( this.subscribers.remove(  consumer  ) ) {
                /// remove them all!
            }
        }
    }
    
    protected void sendToAllSubscribers(StompMessage message) throws StompException {
        synchronized ( this.subscribers ) {
            for ( MessageSink each : this.subscribers ) {
                each.send( message );
            }
        }
    }
    
    protected void sendToOneSubscriber(StompMessage message) throws StompException {
        synchronized ( this.subscribers ) {
            int luckyWinner = this.random.nextInt( this.subscribers.size() );
            this.subscribers.get(  luckyWinner ).send( message );
        }
    }
    
    private List<MessageSink> subscribers = new ArrayList<MessageSink>();
    private Random random = new Random( System.currentTimeMillis() );

}
