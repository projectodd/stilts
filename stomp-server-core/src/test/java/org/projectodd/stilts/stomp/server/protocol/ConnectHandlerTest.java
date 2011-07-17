package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.StompContentFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.server.AbstractStompServerTestCase;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;

public class ConnectHandlerTest extends AbstractStompServerTestCase<MockStompProvider> {

    private DecoderEmbedder<StompFrame> handler;

    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        return server;
    }

    @After
    public void after() throws Exception {
        handler.finish();
    }

    @Before
    public void before() throws Exception {
        ConnectionContext ctx = new ConnectionContext();
        handler = new DecoderEmbedder<StompFrame>( new ConnectHandler( server.getStompProvider(), ctx ) );
    }

    @Test
    public void testBothVersionsAccepted() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.0,1.1" );
        handler.offer( stompFrame );
        StompFrame frame = handler.poll();
        assertEquals( Command.CONNECTED, frame.getCommand() );
        assertEquals( "1.1", frame.getHeader( Header.VERSION ) );
        Version version = server.getStompProvider().getConnections().get( 0 ).getVersion();
        assertEquals( Version.VERSION_1_1, version );
    }

    @Test
    public void testSpacesVersionString() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.0 1.1" );
        handler.offer( stompFrame );
        StompContentFrame resultFrame = (StompContentFrame) handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.ERROR, command );
        assertEquals( "Accept-version header value must be an incrementing comma-separated list.", new String( resultFrame.getContent().array() ) );
    }

    @Test
    public void testNoMatchingVersions() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "beef" );
        handler.offer( stompFrame );
        StompContentFrame resultFrame = (StompContentFrame) handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.ERROR, command );
        assertEquals( "Supported protocol versions are 1.0 1.1", new String( resultFrame.getContent().array() ) );
    }

    @Test
    public void testNoAcceptsHeader() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        handler.offer( stompFrame );
        StompFrame resultFrame = handler.poll();
        assertEquals( Command.CONNECTED, resultFrame.getCommand() );
        assertNull( resultFrame.getHeader( Header.VERSION ) );
        Version version = server.getStompProvider().getConnections().get( 0 ).getVersion();
        assertEquals( Version.VERSION_1_0, version );
    }

    @Test
    public void testHeartbeatIgnoredOnV10() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.HEARTBEAT, "burns_omninet" );
        handler.offer( stompFrame );
        StompFrame resultFrame = handler.poll();
        assertEquals( Command.CONNECTED, resultFrame.getCommand() );
        assertNull( resultFrame.getHeader( Header.VERSION ) );
        Version version = server.getStompProvider().getConnections().get( 0 ).getVersion();
        assertEquals( Version.VERSION_1_0, version );
    }

    @Test
    public void testHeartbeatInvalidValues() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.1" );
        stompFrame.setHeader( Header.HEARTBEAT, "ahoy,hoy" );
        handler.offer( stompFrame );
        StompContentFrame resultFrame = (StompContentFrame) handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.ERROR, command );
        assertEquals( "Heartbeat must be specified in msec as two comma-separated values.", new String( resultFrame.getContent().array() ) );
    }

    @Test
    public void testBadHeartbeatNonIntValues() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.1" );
        stompFrame.setHeader( Header.HEARTBEAT, "91895259821759871827598127598715987182975,42" );
        handler.offer( stompFrame );
        StompContentFrame resultFrame = (StompContentFrame) handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.ERROR, command );
        assertEquals( "Heartbeat values must be integers.", new String( resultFrame.getContent().array() ) );
    }

    @Test
    public void testBadHeartbeatValidValues() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.1" );
        stompFrame.setHeader( Header.HEARTBEAT, "30000,30000" );
        handler.offer( stompFrame );
        StompFrame resultFrame = handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.CONNECTED, command );
        assertEquals( "60000,60000", resultFrame.getHeader( Header.HEARTBEAT ) );
    }

}
