package org.projectodd.stilts.stomplet.container.xa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.transaction.xa.XAResource;

import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.spi.StompSession;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.StompletConfig;
import org.projectodd.stilts.stomplet.Subscriber;
import org.projectodd.stilts.stomplet.XAStomplet;
import org.projectodd.stilts.stomplet.container.SubscriberImpl;

public class PseudoXAStomplet implements XAStomplet {

    public PseudoXAStomplet(Stomplet stomplet) {
        this.stomplet = stomplet;
        this.resourceManager = new PseudoXAStompletResourceManager( stomplet );
        this.xaResources = new HashSet<XAResource>();
        this.xaResources.add( resourceManager );
    }

    @Override
    public Set<XAResource> getXAResources() {
        return this.xaResources;
    }

    @Override
    public void initialize(StompletConfig config) throws StompException {
        this.stomplet.initialize( config );
    }

    @Override
    public void destroy() throws StompException {
        this.stomplet.destroy();
    }

    @Override
    public void onMessage(StompMessage message, StompSession session) throws StompException {
        PseudoXAStompletTransaction tx = this.resourceManager.currentTransaction();
        if (tx == null) {
            this.stomplet.onMessage( message, session );
        } else {
            tx.addSentMessage( message, session );
        }
    }

    @Override
    public void onSubscribe(Subscriber subscriber) throws StompException {
        String subscriptionId = subscriber.getSubscriptionId();
        Subscriber xaSubscriber = new SubscriberImpl( subscriber.getSession(), stomplet, subscriptionId, subscriber.getDestination(), new PseudoXAStompletAcknowledgeableMessageSink( this.resourceManager, subscriber ),
                subscriber.getAckMode() );
        this.subscribers.put( subscriber.getId(), xaSubscriber );
        this.stomplet.onSubscribe( xaSubscriber );
    }

    @Override
    public void onUnsubscribe(Subscriber subscriber) throws StompException {
        Subscriber xaSubscriber = this.subscribers.remove( subscriber.getId() );
        if (xaSubscriber != null) {
            this.stomplet.onUnsubscribe( xaSubscriber );
        }
    }

    private Stomplet stomplet;
    private PseudoXAStompletResourceManager resourceManager;
    private Set<XAResource> xaResources;

    private Map<String, Subscriber> subscribers = new HashMap<String, Subscriber>();

}
