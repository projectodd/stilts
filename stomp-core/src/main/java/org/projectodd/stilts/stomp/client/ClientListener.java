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

package org.projectodd.stilts.stomp.client;

import org.projectodd.stilts.StompMessage;

public interface ClientListener {
    
    void connecting(AbstractStompClient client);
    void connected(AbstractStompClient client);
    void disconnecting(AbstractStompClient client);
    void disconnected(AbstractStompClient client);
    void error(AbstractStompClient client, StompMessage error);

}
