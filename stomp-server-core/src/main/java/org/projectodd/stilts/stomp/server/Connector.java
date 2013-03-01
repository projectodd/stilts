package org.projectodd.stilts.stomp.server;

import java.net.InetSocketAddress;

public interface Connector {
    
    InetSocketAddress getBindAddress();
    void setServer(Server server);
    void start() throws Exception;
    void stop() throws Exception;

}
