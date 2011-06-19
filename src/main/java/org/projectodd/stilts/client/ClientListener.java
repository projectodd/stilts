package org.projectodd.stilts.client;

import org.projectodd.stilts.StompMessage;

public interface ClientListener {
    
    void connecting(AbstractStompClient client);
    void connected(AbstractStompClient client);
    void disconnecting(AbstractStompClient client);
    void disconnected(AbstractStompClient client);
    void error(AbstractStompClient client, StompMessage error);

}
