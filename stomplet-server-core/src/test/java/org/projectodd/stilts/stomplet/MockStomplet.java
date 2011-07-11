package org.projectodd.stilts.stomplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;

public class MockStomplet implements Stomplet {

    @Override
    public void initialize(StompletConfig config) throws StompException {
        this.config = config;
    }
    
    public StompletConfig getStompletConfig() {
        return this.config;
    }

    @Override
    public void destroy() throws StompException {
        this.destroyed = true;
    }
    
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    public void onMessage(StompMessage message) throws StompException {
        this.messages.add( message );
    }
    
    public List<StompMessage> getMessages() {
        return this.messages;
    }

    @Override
    public void onSubscribe(Subscriber subscriber) throws StompException {
        this.subscribers.add( subscriber );
    }
    
    public List<Subscriber> getSubscribers() {
        return this.subscribers;
    }

    @Override
    public void onUnsubscribe(Subscriber subscriber) throws StompException {
        this.unsubscribers.add( subscriber );
        
    }
    
    public List<Subscriber> getUnsubscribers() {
        return this.unsubscribers;
    }
    
    public void send(StompMessage message) throws StompException {
        for ( Subscriber each : getCurrentSubscribers() ) {
            each.send( message );
        }
    }
    
    public Set<Subscriber> getCurrentSubscribers() {
        Set<Subscriber> current = new HashSet<Subscriber>();
        current.addAll( this.subscribers );
        current.removeAll(  this.unsubscribers );
        return current;
    }
    
    private StompletConfig config;
    private boolean destroyed;
    
    private List<StompMessage> messages = new ArrayList<StompMessage>();
    private List<Subscriber> subscribers = new ArrayList<Subscriber>();
    private List<Subscriber> unsubscribers = new ArrayList<Subscriber>();

}
