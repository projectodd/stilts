/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.circus.stomplet.weld.server;

import static org.junit.Assert.*;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.projectodd.stilts.MessageAccumulator;
import org.projectodd.stilts.StompMessages;
import org.projectodd.stilts.circus.stomplet.weld.CircusBeanDeploymentArchive;
import org.projectodd.stilts.circus.stomplet.weld.ShrinkwrapBeanDeploymentArchive;
import org.projectodd.stilts.circus.stomplet.weld.TacoStomplet;
import org.projectodd.stilts.circus.stomplet.weld.WeldStompletContainer;
import org.projectodd.stilts.client.ClientSubscription;
import org.projectodd.stilts.client.ClientTransaction;
import org.projectodd.stilts.logging.SimpleLoggerManager.Level;
import org.projectodd.stilts.stomplet.simple.SimpleQueueStomplet;
import org.projectodd.stilts.stomplet.simple.SimpleTopicStomplet;

public class BasicStompletClientServerTest extends AbstractWeldStompletClientServerTest {

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
