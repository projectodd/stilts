package org.projectodd.stilts.stomp.server.protocol.longpoll;

import java.util.LinkedList;

import org.jboss.logging.Logger;
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
        log.debug( "someone sent a message: " + message );
        if (acknowledger != null) {
            this.ackManager.registerAcknowledger( message.getId(), acknowledger );
        }

        if (this.channel != null) {
            log.debug( "write message to channel : " + message );
            this.channel.write( message );
            this.channel = null;
        }
    }

    public synchronized void provideChannel(Channel channel) {
        log.debug( "someone provided a channel: " + channel );
        if (this.messages.isEmpty()) {
            this.channel = channel;
            return;
        }

        StompMessage message = messages.removeFirst();
        log.debug( "write message to channel : " + message );
        channel.write( message );
    }

    public synchronized void clearChannel() {
        this.channel = null;
    }

    private static Logger log = Logger.getLogger( HttpMessageSink.class );
    
    private AckManager ackManager;
    private Channel channel;
    private LinkedList<StompMessage> messages = new LinkedList<StompMessage>();

}
