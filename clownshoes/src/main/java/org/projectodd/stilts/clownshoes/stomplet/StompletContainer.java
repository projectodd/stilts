/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.clownshoes.stomplet;

import org.projectodd.stilts.stomplet.MessageRouter;

public interface StompletContainer extends MessageRouter {

    
    RouteMatch match(String destination);
    void start() throws Exception;

    void stop() throws Exception;

}
