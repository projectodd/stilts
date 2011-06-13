package org.jboss.stilts;

import org.jboss.stilts.protocol.DefaultStompServerMessage;
import org.jboss.stilts.spi.Headers;

public class StompMessages {
    
    private StompMessages() {
        
    }
    
    public static StompMessage createStompMessage() {
        return new DefaultStompServerMessage();
    }
    
    public static StompMessage createStompMessage(String destination, String content) {
        DefaultStompServerMessage message = new DefaultStompServerMessage();
        message.setDestination( destination );
        message.setContent( content );
        return message;
    }
    
    public static StompMessage createStompMessage(String destination, Headers headers, String content) {
        DefaultStompServerMessage message = new DefaultStompServerMessage( headers, content );
        message.setDestination( destination );
        return message;
    }

}
