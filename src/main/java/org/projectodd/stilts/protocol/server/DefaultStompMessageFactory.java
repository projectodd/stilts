package org.projectodd.stilts.protocol.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.protocol.DefaultStompMessage;
import org.projectodd.stilts.spi.Headers;
import org.projectodd.stilts.spi.StompMessageFactory;

public class DefaultStompMessageFactory implements StompMessageFactory {
    
    public final static DefaultStompMessageFactory INSTANCE = new DefaultStompMessageFactory();

    @Override
    public StompMessage createMessage(Headers headers, ChannelBuffer content, boolean isError) {
        return new DefaultStompMessage( headers, content, isError); 
    }

}
