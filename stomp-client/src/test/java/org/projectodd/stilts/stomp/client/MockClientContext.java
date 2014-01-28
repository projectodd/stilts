package org.projectodd.stilts.stomp.client;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.client.StompClient.State;
import org.projectodd.stilts.stomp.client.protocol.ClientContext;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;

public class MockClientContext implements ClientContext {

    public void setServerAddress(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public InetSocketAddress getServerAddress() {
        return this.serverAddress;
    }
    
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	public boolean isSecure() {
		return this.secure;
	}
	
	public void setSSLContext(SSLContext sslContext) {
		this.sslContext = sslContext;
	}
	
	public SSLContext getSSLContext() {
		return this.sslContext;
	}

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public String getUsername() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public State getConnectionState() {
        return this.state;
    }
    
    @Override
    public Version getVersion() {
        return this.version;
    }

    @Override
    public void setConnectionState(State state) {
        this.state = state;
    }

    @Override
    public void receiptReceived(String receiptId) {
        this.receipts.add( receiptId );
    }
    
    public List<String> getReceipts() {
        return this.receipts;
    }

    @Override
    public void messageReceived(StompMessage message) {
        this.messages.add( message );
    }
    
    public List<StompMessage> getMessages() {
        return this.messages;
    }

    @Override
    public void errorReceived(StompMessage message) {
        this.errors.add( message );
    }

    
    public List<StompMessage> getErrors() {
        return this.errors;
    }

    @Override
    public void setVersion(Version version) {
        this.version = version;
    }    
    
    private InetSocketAddress serverAddress;
    private State state;
    private Version version = Version.VERSION_1_0;
    private boolean secure = false;
    private SSLContext sslContext;
    private boolean authenticated = false;
    private String user;
    private String password;
    
    
    private List<String> receipts = new ArrayList<String>();
    private List<StompMessage> messages = new ArrayList<StompMessage>();
    private List<StompMessage> errors = new ArrayList<StompMessage>();


}
