package org.projectodd.stilts.circus.stomplet;

import org.projectodd.stilts.stomplet.MessageRouter;

public interface StompletContainer extends MessageRouter {

    
    RouteMatch match(String destination);
    void start() throws Exception;

    void stop() throws Exception;

}
