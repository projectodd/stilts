package org.jboss.stilts.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.StompMessageFactory;

public class ClientStompMessageFactory implements StompMessageFactory {
    
    private AbstractStompClient client;

    public ClientStompMessageFactory(AbstractStompClient client) {
        this.client = client;
    }

    @Override
    public StompMessage createMessage(Headers headers, ChannelBuffer content, boolean isError) {
        ClientStompMessage message = new ClientStompMessage( headers, content, isError );
        message.setAcknowledger( new ClientMessageAcknowledger( this.client, message.getHeaders() ) );
        return message;
    }
    
}
