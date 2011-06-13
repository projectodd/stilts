package org.jboss.stilts.protocol;

public class StompControlFrame extends StompFrame {

    public StompControlFrame(Command command) {
        super( command );
    }
    
    public StompControlFrame(FrameHeader header) {
        super( header );
    }

}
