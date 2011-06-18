package org.jboss.stilts.protocol.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.protocol.DefaultStompMessage;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.StompMessageFactory;

public class DefaultStompMessageFactory implements StompMessageFactory {
    
    public final static DefaultStompMessageFactory INSTANCE = new DefaultStompMessageFactory();

    @Override
    public StompMessage createMessage(Headers headers, ChannelBuffer content, boolean isError) {
        return new DefaultStompMessage( headers, content, isError); 
    }

}
