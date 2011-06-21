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

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.projectodd.stilts.MessageAccumulator;
import org.projectodd.stilts.StompMessages;
import org.projectodd.stilts.clownshoes.stomplet.weld.TacoStomplet;
import org.projectodd.stilts.clownshoes.weld.CircusBeanDeploymentArchive;
import org.projectodd.stilts.clownshoes.weld.ShrinkwrapBeanDeploymentArchive;
import org.projectodd.stilts.clownshoes.weld.WeldStompletContainer;
import org.projectodd.stilts.logging.SimpleLoggerManager.Level;
import org.projectodd.stilts.stomp.client.ClientSubscription;
import org.projectodd.stilts.stomp.client.ClientTransaction;
import org.projectodd.stilts.stomplet.simple.SimpleQueueStomplet;
import org.projectodd.stilts.stomplet.simple.SimpleTopicStomplet;

public class BasicStompletWeldClientServerTest extends AbstractWeldStompletClientServerTest {

    static {
        SERVER_ROOT_LEVEL = Level.TRACE;
        CLIENT_ROOT_LEVEL = Level.TRACE;
    }

    @Override
    public CircusBeanDeploymentArchive getBeanDeploymentArchive() throws Exception {
        JavaArchive archive = ShrinkWrap.create( JavaArchive.class );
        archive.addClass( TacoStomplet.class );
        return new ShrinkwrapBeanDeploymentArchive( archive, getClass().getClassLoader() );
    }
    
    protected WeldStompletContainer getStompletContainer() {
        return (WeldStompletContainer) getServer().getStompletContainer();
    }
    
    public void prepareServer() throws Exception {
        super.prepareServer();
        getStompletContainer().addStomplet( "/queues/:destination", SimpleQueueStomplet.class.getName() );
        getStompletContainer().addStomplet( "/topics/:destination", SimpleTopicStomplet.class.getName() );
    }

    @Test
    public void testClient() throws Exception {
        client.connect();
        assertTrue( client.isConnected() );

        ClientSubscription subscription1 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator("one") ).start();
        ClientSubscription subscription2 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator("two") ).start();

        assertNotNull( subscription1 );
        assertTrue( subscription1.isActive() );

        assertNotNull( subscription2 );
        assertTrue( subscription2.isActive() );

        for (int i = 0; i < 10; ++i) {
            client.send( StompMessages.createStompMessage( "/topics/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );

        subscription1.unsubscribe();
        assertFalse( subscription1.isActive() );

        subscription2.unsubscribe();
        assertFalse( subscription2.isActive() );

        client.disconnect();
        assertTrue( client.isDisconnected() );

        assertEquals( 10, accumulator("one").size() );
        assertEquals( 10, accumulator("two").size() );
    }

    @Test
    public void testClientTransaction() throws Exception {
        client.connect();
        assertTrue( client.isConnected() );

        MessageAccumulator accumulator1 = new MessageAccumulator();
        ClientSubscription subscription1 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator1 ).start();

        MessageAccumulator accumulator2 = new MessageAccumulator();
        ClientSubscription subscription2 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator2 ).start();

        assertNotNull( subscription1 );
        assertTrue( subscription1.isActive() );

        assertNotNull( subscription2 );
        assertTrue( subscription2.isActive() );

        ClientTransaction tx = client.begin();

        for (int i = 0; i < 10; ++i) {
            tx.send( StompMessages.createStompMessage( "/topics/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );

        assertTrue( accumulator1.isEmpty() );
        assertTrue( accumulator2.isEmpty() );

        tx.commit();

        Thread.sleep( 1000 );

        subscription1.unsubscribe();
        assertFalse( subscription1.isActive() );

        subscription2.unsubscribe();
        assertFalse( subscription2.isActive() );

        client.disconnect();
        assertTrue( client.isDisconnected() );

        assertEquals( 10, accumulator1.size() );
        assertEquals( 10, accumulator2.size() );
    }

    @Test
    public void testClientTransactionAborted() throws Exception {
        client.connect();
        assertTrue( client.isConnected() );

        MessageAccumulator accumulator1 = new MessageAccumulator();
        ClientSubscription subscription1 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator1 ).start();

        MessageAccumulator accumulator2 = new MessageAccumulator();
        ClientSubscription subscription2 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator2 ).start();

        assertNotNull( subscription1 );
        assertTrue( subscription1.isActive() );

        assertNotNull( subscription2 );
        assertTrue( subscription2.isActive() );

        ClientTransaction tx = client.begin();

        for (int i = 0; i < 10; ++i) {
            tx.send( StompMessages.createStompMessage( "/topics/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );
        tx.abort();
        Thread.sleep( 1000 );

        subscription1.unsubscribe();
        assertFalse( subscription1.isActive() );

        subscription2.unsubscribe();
        assertFalse( subscription2.isActive() );

        client.disconnect();
        assertTrue( client.isDisconnected() );

        assertTrue( accumulator1.isEmpty() );
        assertTrue( accumulator2.isEmpty() );
    }
}
