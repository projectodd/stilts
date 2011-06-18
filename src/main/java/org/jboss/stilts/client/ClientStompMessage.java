package org.jboss.stilts.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.DefaultStompMessage;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.Headers;

public class ClientStompMessage extends DefaultStompMessage {

    public ClientStompMessage(Headers headers, ChannelBuffer content, boolean isError) {
        super( headers, content, isError);
    }
    
    void setAcknowledger(Acknowledger acknowledger) {
        this.acknowledger = acknowledger;
    }

    @Override
    public void ack() throws StompException {
        if ( this.acknowledger != null ) {
            try {
                this.acknowledger.ack();
            } catch (Exception e) {
                throw new StompException( e );
            }
        } else {
            super.ack();
        }
    }
    
    @Override
    public void nack() throws StompException {
        if ( this.acknowledger != null ) {
            try {
                this.acknowledger.nack();
            } catch (Exception e) {
                throw new StompException( e );
            }
        } else {
            super.ack();
        }
    }

    private Acknowledger acknowledger;

}
