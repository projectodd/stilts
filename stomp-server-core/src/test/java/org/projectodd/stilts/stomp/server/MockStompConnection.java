package org.projectodd.stilts.stomp.server;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.stilts.stomp.Acknowledger;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.NotConnectedException;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.Subscription;
import org.projectodd.stilts.stomp.spi.StompConnection;

public class MockStompConnection implements StompConnection {

    public MockStompConnection(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    @Override
    public void send(StompMessage message, String transactionId) throws StompException {
        this.sends.add( new Send( message, transactionId ) );
    }
    
    public List<Send> getSends() {
        return this.sends;
    }

    @Override
    public Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException {
        return null;
    }

    @Override
    public void unsubscribe(String subscriptionId, Headers headers) throws StompException {
        
    }

    @Override
    public void begin(String transactionId, Headers headers) throws StompException {
        this.begins.add( transactionId );
    }
    
    public List<String> getBegins() {
        return this.begins;
    }

    @Override
    public void commit(String transactionId) throws StompException {
        this.commits.add( transactionId );
    }
    
    public List<String> getCommits() {
        return this.commits;
    }

    @Override
    public void abort(String transactionId) throws StompException {
        this.aborts.add( transactionId );
    }
    
    public List<String> getAborts() {
        return this.aborts;
    }

    @Override
    public void disconnect() throws NotConnectedException {
        this.disconnected = true;
    }
    
    public boolean isDisconnected() {
        return this.disconnected;
    }
    
    private String sessionId;
    private boolean disconnected;
    private List<Send> sends = new ArrayList<Send>();
    private List<String> begins = new ArrayList<String>();
    private List<String> commits = new ArrayList<String>();
    private List<String> aborts = new ArrayList<String>();
    
    public static final class Send {

        public Send(StompMessage message, String transactionId) {
            this.message = message;
            this.transactionId = transactionId;
        }
        
        public StompMessage message;
        public String transactionId;
    }

}
