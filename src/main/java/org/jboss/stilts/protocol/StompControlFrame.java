package org.jboss.stilts.protocol;

import org.jboss.stilts.spi.Headers;

public class StompControlFrame extends StompFrame {

    public StompControlFrame(Command command) {
        super( command );
    }
    
    public StompControlFrame(Command command, Headers headers) {
        super( command, headers );
    }
    
    public StompControlFrame(FrameHeader header) {
        super( header );
    }

}
