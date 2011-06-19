package org.projectodd.stilts.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.Headers;
import org.projectodd.stilts.spi.StompMessageFactory;

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
