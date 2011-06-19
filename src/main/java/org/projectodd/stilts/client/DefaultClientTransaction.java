package org.projectodd.stilts.client;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.protocol.StompFrame.Header;

public class DefaultClientTransaction implements ClientTransaction {

    public DefaultClientTransaction(AbstractStompClient client, String id) {
        this( client, id, false );
    }

    public DefaultClientTransaction(AbstractStompClient client, String id, boolean isGlobal) {
        this.client = client;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void send(StompMessage message) {
        message.getHeaders().put( Header.TRANSACTION, this.id );
        this.client.send( message );
    }

    @Override
    public void commit() throws StompException {
        this.client.commit( this.id );
    }

    @Override
    public void abort() throws StompException {
        this.client.abort( this.id );
    }

    private AbstractStompClient client;
    private String id;
}
