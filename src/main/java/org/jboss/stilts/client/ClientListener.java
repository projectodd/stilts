package org.jboss.stilts.client;

import org.jboss.stilts.StompMessage;

public interface ClientListener {
    
    void connecting(AbstractStompClient client);
    void connected(AbstractStompClient client);
    void disconnecting(AbstractStompClient client);
    void disconnected(AbstractStompClient client);
    void error(AbstractStompClient client, StompMessage error);

}
