package org.projectodd.stilts.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.protocol.DefaultStompMessage;
import org.projectodd.stilts.spi.Acknowledger;
import org.projectodd.stilts.spi.Headers;

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
