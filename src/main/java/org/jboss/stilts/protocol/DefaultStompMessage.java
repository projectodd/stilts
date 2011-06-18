package org.jboss.stilts.protocol;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.DefaultHeaders;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.Headers;

public class DefaultStompMessage implements StompMessage {

    private static final Charset UTF_8 = Charset.forName( "UTF-8" );
    
    public DefaultStompMessage() {
        this( new DefaultHeaders(), ChannelBuffers.EMPTY_BUFFER );
    }
    
    public DefaultStompMessage(Headers headers, ChannelBuffer content) {
        this( headers, content, false );
    }
    
    public DefaultStompMessage(Headers headers, String content) {
        this( headers, ChannelBuffers.copiedBuffer( content.getBytes() ), false );
    }
    
    public DefaultStompMessage(Headers headers, String content, boolean isError) {
        this( headers, ChannelBuffers.copiedBuffer( content.getBytes() ), isError );
    }
    
    public DefaultStompMessage(Headers headers, ChannelBuffer content, boolean isError) {
        this.headers = headers;
        this.content = content;
        this.isError = isError;
    }
    
    public String getId() {
        return this.headers.get( Header.MESSAGE_ID  );
    }
    
    @Override
    public boolean isError() {
        return this.isError;
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
    
    public void setContent(ChannelBuffer content) {
        this.content = content;
    }

    @Override
    public ChannelBuffer getContent() {
        return ChannelBuffers.wrappedBuffer( this.content );
    }
    
    @Override
    public String getContentAsString() {
        return this.content.toString( UTF_8 );
    }
    
    public void setContentAsString(String content) {
        this.content = ChannelBuffers.copiedBuffer( content.getBytes() );
    }
    
    public String toString() {
        return "[StompMessage: headers=" + this.headers + "\n  content=" + getContent() + "]";
    }
    
    @Override
    public void acknowledge() throws StompException {
        throw new UnsupportedOperationException("acknowledge");
    }
    
    @Override
    public StompMessage duplicate() {
        return new DefaultStompMessage( headers.duplicate(), ChannelBuffers.wrappedBuffer( content ), isError );
    }

    
    private Headers headers;
    private ChannelBuffer content;
    private boolean isError = false;


}
