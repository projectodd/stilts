package org.projectodd.stilts;

import org.projectodd.stilts.protocol.DefaultStompMessage;
import org.projectodd.stilts.spi.Headers;

public class StompMessages {

    private StompMessages() {

    }

    public static StompMessage createStompMessage() {
        return new DefaultStompMessage();
    }
    
    public static StompMessage createStompMessage(String destination, String content) {
        DefaultStompMessage message = new DefaultStompMessage();
        message.setDestination( destination );
        message.setContentAsString( content );
        return message;
    }

    public static StompMessage createStompMessage(String destination, Headers headers, String content) {
        DefaultStompMessage message = new DefaultStompMessage( headers, content );
        message.setDestination( destination );
        return message;
    }

}
