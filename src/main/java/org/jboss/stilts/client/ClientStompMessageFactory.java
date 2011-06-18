package org.jboss.stilts.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.StompMessageFactory;

public class ClientStompMessageFactory implements StompMessageFactory {
    
    public static final ClientStompMessageFactory INSTANCE = new ClientStompMessageFactory();
    
    @Override
    public StompMessage createMessage(Headers headers, ChannelBuffer content, boolean isError) {
        return new ClientStompMessage( headers, content, isError );
    }
    
}
