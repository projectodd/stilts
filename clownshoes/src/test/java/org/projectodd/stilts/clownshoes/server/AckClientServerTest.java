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

package org.projectodd.stilts.clownshoes.server;

import static org.junit.Assert.*;

import org.junit.Test;
import org.projectodd.stilts.StompMessages;
import org.projectodd.stilts.logging.SimpleLoggerManager.Level;
import org.projectodd.stilts.stomp.client.ClientSubscription;
import org.projectodd.stilts.stomp.client.ClientTransaction;
import org.projectodd.stilts.stomp.spi.Subscription.AckMode;

public class AckClientServerTest extends AbstractStompletClientServerTest {

    static {
        SERVER_ROOT_LEVEL = Level.TRACE;
        CLIENT_ROOT_LEVEL = Level.NONE;
    }

    @Test
    public void testClientTransaction() throws Exception {
        client.connect();

        ClientSubscription subscription1 = client.subscribe( "/queues/foo" ).withMessageHandler( accumulator( "one", false, true ) ).withAckMode( AckMode.CLIENT_INDIVIDUAL ).start();
        ClientSubscription subscription2 = client.subscribe( "/queues/foo" ).withMessageHandler( accumulator( "two" ) ).withAckMode( AckMode.AUTO ).start();
        
        ClientTransaction tx = client.begin();

        for (int i = 0; i < 10; ++i) {
            tx.send( StompMessages.createStompMessage( "/queues/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );
        assertTrue( accumulator( "one" ).isEmpty() );
        assertTrue( accumulator( "two" ).isEmpty() );
        tx.commit();
        Thread.sleep( 1000 );
        subscription1.unsubscribe();
        Thread.sleep( 1000 );
        subscription2.unsubscribe();
        client.disconnect();
        
        System.err.println( "===========================================" );
        System.err.println( "===========================================" );
        System.err.println( accumulator("one").messageIds() );
        System.err.println( accumulator("two").messageIds() );
        System.err.println( "===========================================" );
        System.err.println( "===========================================" );
        assertEquals( 10, accumulator( "two" ).size() );
        
        
    }
}
