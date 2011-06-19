package org.projectodd.stilts.protocol;

import org.projectodd.stilts.spi.Headers;

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
