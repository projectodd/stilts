package org.jboss.stilts.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class StompContentFrame extends StompFrame {

    public StompContentFrame(Command command) {
        super( command );
    }
    
    public StompContentFrame(FrameHeader header) {
        super( header );
    }
    
    public StompContentFrame(FrameHeader header, ChannelBuffer content) {
        super( header );
        this.content = content;
    }
    
    public StompContentFrame(FrameHeader header, String content) {
        super( header);
        this.content = ChannelBuffers.copiedBuffer( content.getBytes() );
    }
    
    public void setContent(ChannelBuffer content) {
        this.content = content;
    }

    public ChannelBuffer getContent() {
        return ChannelBuffers.wrappedBuffer( this.content );
    }
    
    private ChannelBuffer content;
}
