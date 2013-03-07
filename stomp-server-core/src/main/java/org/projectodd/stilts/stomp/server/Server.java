package org.projectodd.stilts.stomp.server;

import java.util.List;
import java.util.concurrent.Executor;

import org.projectodd.stilts.stomp.server.protocol.resource.ResourceManager;
import org.projectodd.stilts.stomp.spi.StompProvider;

public interface Server {
    
    void addConnector(Connector connector) throws Exception;
    List<Connector> getConnectors();
    void removeConnector(Connector connector) throws Exception;
    
    StompProvider getStompProvider();
    Executor getMessageHandlingExecutor();
    ResourceManager getResourceManager();

}
