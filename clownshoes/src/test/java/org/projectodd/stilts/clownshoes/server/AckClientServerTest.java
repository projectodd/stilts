/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
