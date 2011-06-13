package org.jboss.stilts.protocol;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.DefaultHeaders;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.Headers;

public class DefaultStompServerMessage implements StompServerMessage, StompMessage {

    private static final Charset UTF_8 = Charset.forName( "UTF-8" );
    
    public DefaultStompServerMessage() {
        this.headers = new DefaultHeaders();
        this.content = ChannelBuffers.EMPTY_BUFFER;
    }
    
    public DefaultStompServerMessage(Headers headers, ChannelBuffer content) {
        this.headers = headers;
        this.content = content;
    }
    
    public DefaultStompServerMessage(Headers headers, String content) {
        this.headers = headers;
        this.content = ChannelBuffers.copiedBuffer( content.getBytes() );
    }
    
    @Override
    public String getDestination() {
        return this.headers.get(  Header.DESTINATION );
    }
    
    @Override
    public void setDestination(String destination) {
        this.headers.put( Header.DESTINATION, destination );
    }
    
    @Override
    public String getContentType() {
        return this.headers.get(  Header.CONTENT_TYPE );
    }
    
    @Override
    public void setContentType(String contentType) {
        this.headers.put( Header.CONTENT_LENGTH, contentType);
    }

    @Override
    public Headers getHeaders() {
        return this.headers;
    }

    @Override
    public ChannelBuffer getContentBuffer() {
        return this.content;
    }
    
    @Override
    public String getContent() {
        return this.content.toString( UTF_8 );
    }
    
    public void setContent(String content) {
        ChannelBuffers.copiedBuffer( content.getBytes() );
    }
    
    private Headers headers;
    private ChannelBuffer content;

}
