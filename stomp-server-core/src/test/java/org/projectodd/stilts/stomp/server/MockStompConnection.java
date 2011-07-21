package org.projectodd.stilts.stomp.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.Heartbeat;
import org.projectodd.stilts.stomp.InvalidSubscriptionException;
import org.projectodd.stilts.stomp.MockSubscription;
import org.projectodd.stilts.stomp.NotConnectedException;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.Subscription;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.server.protocol.HeartbeatRunnable;
import org.projectodd.stilts.stomp.spi.StompConnection;

public class MockStompConnection implements StompConnection {

    public MockStompConnection(String sessionId, Version version, Heartbeat hb) {
        this.sessionId = sessionId;
        this.version = version;
        this.heartbeat = hb;

        if (this.heartbeat != null) {
            heartbeatMonitor = Executors.newSingleThreadScheduledExecutor();
            int duration = hb.calculateDuration( hb.getServerSend(), hb.getClientReceive() );
            heartbeatMonitor.scheduleAtFixedRate( new HeartbeatRunnable( hb, this ), 0L, duration, TimeUnit.MILLISECONDS );
        }

    }

    public Heartbeat getHeartbeat() {
        return this.heartbeat;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    public Map<String, Subscription> getSubscriptions() {
        return subscriptions;
    }

    public Version getVersion() {
        return this.version;
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
        Subscription sub = new MockSubscription( subscriptionId, destination, headers );
        this.subscriptions.put( subscriptionId, sub );
        return null;
    }

    @Override
    public void unsubscribe(String subscriptionId, Headers headers) throws StompException {
        Subscription sub = this.subscriptions.remove( subscriptionId );
        if (sub == null) {
            throw new InvalidSubscriptionException( subscriptionId );
        }
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
        if (heartbeatMonitor != null) {
            heartbeatMonitor.shutdown();
        }
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    private ScheduledExecutorService heartbeatMonitor;
    private Heartbeat heartbeat;
    private String sessionId;
    private Version version;
    private boolean disconnected;
    private List<Send> sends = new ArrayList<Send>();
    private List<String> begins = new ArrayList<String>();
    private List<String> commits = new ArrayList<String>();
    private List<String> aborts = new ArrayList<String>();
    private Map<String, Subscription> subscriptions = new HashMap<String, Subscription>();

    public static final class Send {

        public Send(StompMessage message, String transactionId) {
            this.message = message;
            this.transactionId = transactionId;
        }

        public StompMessage message;
        public String transactionId;
    }

}
