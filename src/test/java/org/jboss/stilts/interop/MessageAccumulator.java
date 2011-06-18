package org.jboss.stilts.interop;

import java.util.ArrayList;
import java.util.List;

import org.jboss.stilts.StompMessage;
import org.jboss.stilts.client.MessageHandler;
import org.jboss.stilts.protocol.StompFrame.Header;

public class MessageAccumulator implements MessageHandler {
    private ArrayList<StompMessage> messages;

    MessageAccumulator() {
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
