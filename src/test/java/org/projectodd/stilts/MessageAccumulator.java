package org.projectodd.stilts;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.client.MessageHandler;
import org.projectodd.stilts.protocol.StompFrame.Header;

public class MessageAccumulator implements MessageHandler {
    private ArrayList<StompMessage> messages;
    private boolean shouldAck;
    private boolean shouldNack;

    public MessageAccumulator() {
        this(false, false);
    }
    
    public MessageAccumulator(boolean shouldAck, boolean shouldNack) {
        this.shouldAck = shouldAck;
        this.shouldNack = shouldNack;
        this.messages = new ArrayList<StompMessage>();
    }

    public List<String> messageIds() {
        List<String> messageIds = new ArrayList<String>();
        for (StompMessage each : this.messages) {
            messageIds.add( each.getHeaders().get( Header.MESSAGE_ID ) );
        }
        return messageIds;
    }

    public void handle(StompMessage message) {
        this.messages.add( message );
        if ( shouldAck ) {
            try {
                System.err.println( "Send ACK" );
                message.ack();
            } catch (StompException e) {
                e.printStackTrace();
            }
        } else if ( shouldNack ) {
            try {
                System.err.println( "Send NACK" );
                message.nack();
            } catch (StompException e) {
                e.printStackTrace();
            }
        }
    }

    public List<StompMessage> getMessage() {
        return this.messages;
    }

    public int size() {
        return this.messages.size();
    }

    public boolean isEmpty() {
        return this.messages.isEmpty();
    }

    public void clear() {
        this.messages.clear();
    }

    public String toString() {
        return this.messages.toString();
    }

}
