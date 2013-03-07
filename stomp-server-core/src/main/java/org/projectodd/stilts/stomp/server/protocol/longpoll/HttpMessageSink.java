package org.projectodd.stilts.stomp.server.protocol.longpoll;

import java.util.LinkedList;

import org.jboss.netty.channel.Channel;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.TransactionalAcknowledger;
import org.projectodd.stilts.stomp.server.protocol.AckManager;
import org.projectodd.stilts.stomp.spi.TransactionalAcknowledgeableMessageSink;

public class HttpMessageSink implements TransactionalAcknowledgeableMessageSink {

    public HttpMessageSink(AckManager ackManager) {
        this.ackManager = ackManager;
    }

    @Override
    public void send(StompMessage message) throws StompException {
        send( message, null );
    }

    @Override
    public synchronized void send(StompMessage message, TransactionalAcknowledger acknowledger) throws StompException {
        if (acknowledger != null) {
            this.ackManager.registerAcknowledger( message.getId(), acknowledger );
        }
        
        if ( this.channel != null ) {
           this.channel.write( message ); 
           this.channel = null;
        }
    }
    
    public synchronized void provideChannel(Channel channel) {
        if ( this.messages.isEmpty()) {
            this.channel = channel;
            return;
        }
        
        StompMessage message = messages.removeFirst();
        channel.write( message );
    }
    
    private AckManager ackManager;
    private Channel channel;
    private LinkedList<StompMessage> messages = new LinkedList<StompMessage>();


}
