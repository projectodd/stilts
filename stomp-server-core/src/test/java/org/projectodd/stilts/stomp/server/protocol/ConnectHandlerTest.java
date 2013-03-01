package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.Heartbeat;
import org.projectodd.stilts.stomp.protocol.StompContentFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.server.AbstractStompServerTestCase;
import org.projectodd.stilts.stomp.server.InsecureConnector;
import org.projectodd.stilts.stomp.server.MockStompConnection;
import org.projectodd.stilts.stomp.server.MockStompConnection.Send;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;

public class ConnectHandlerTest extends AbstractStompServerTestCase<MockStompProvider> {

    private DecoderEmbedder<StompFrame> handler;

    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.addConnector( new InsecureConnector() );
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
        stompFrame.setHeader( Header.HOST, "localhost" );
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
        assertEquals( "Supported protocol versions are 1.0,1.1", new String( resultFrame.getContent().array() ) );
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
    public void testV10NoHostsHeaderOk() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        handler.offer( stompFrame );
        StompFrame resultFrame = (StompFrame) handler.poll();
        assertEquals( Command.CONNECTED, resultFrame.getCommand() );
        Version version = server.getStompProvider().getConnections().get( 0 ).getVersion();
        assertEquals( Version.VERSION_1_0, version );
    }    
    
    @Test
    public void testV11NoHostsHeader() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, Version.supportedVersions() );
        handler.offer( stompFrame );
        StompContentFrame resultFrame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, resultFrame.getCommand() );
        assertEquals( "Must specify host in STOMP protocol 1.1 and above.", new String( resultFrame.getContent().array() ) );
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
        stompFrame.setHeader( Header.HOST, "localhost" );
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
        stompFrame.setHeader( Header.HOST, "localhost" );
        handler.offer( stompFrame );
        StompContentFrame resultFrame = (StompContentFrame) handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.ERROR, command );
        assertEquals( "Heartbeat values must be integers.", new String( resultFrame.getContent().array() ) );
    }

    @Test
    public void testBadHeartbeatNegativeValues() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.1" );
        stompFrame.setHeader( Header.HOST, "localhost" );
        stompFrame.setHeader( Header.HEARTBEAT, "-30000,20000" );
        handler.offer( stompFrame );
        StompContentFrame resultFrame = (StompContentFrame) handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.ERROR, command );
        assertEquals( "Heartbeat must be specified in msec as two comma-separated values.", new String( resultFrame.getContent().array() ) );
        assertEquals( resultFrame.getHeader( Header.CONTENT_LENGTH ), "66" );
        assertEquals( resultFrame.getHeader( Header.CONTENT_TYPE ), "text/plain" );
    }

    @Test
    public void testValidHeartbeatValues() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.1" );
        stompFrame.setHeader( Header.HOST, "localhost" );
        stompFrame.setHeader( Header.HEARTBEAT, "30000,30000" );
        handler.offer( stompFrame );
        StompFrame resultFrame = handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.CONNECTED, command );
        assertEquals( "1000,1000", resultFrame.getHeader( Header.HEARTBEAT ) );
        Heartbeat hb = server.getStompProvider().getConnections().get( 0 ).getHeartbeat();
        assertEquals( 30000, hb.getClientReceive() );
        assertEquals( 30000, hb.getClientSend() );
        assertTrue( hb.getLastUpdate() > 0 );
    }

    @Test
    public void testCheckServerHeartbeat() throws Exception {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.1" );
        stompFrame.setHeader( Header.HOST, "localhost" );
        stompFrame.setHeader( Header.HEARTBEAT, "1000,1000" );
        handler.offer( stompFrame );
        StompFrame resultFrame = handler.poll();
        Command command = resultFrame.getCommand();
        assertEquals( Command.CONNECTED, command );
        assertEquals( "1000,1000", resultFrame.getHeader( Header.HEARTBEAT ) );
        MockStompConnection connection = (MockStompConnection) server.getStompProvider().getConnections().get( 0 );
        Heartbeat hb = connection.getHeartbeat();
        assertEquals( 1000, hb.getClientReceive() );
        assertEquals( 1000, hb.getClientSend() );
        long lastUpdate = hb.getLastUpdate();
        assertTrue( lastUpdate > 0 );
        Thread.sleep( 2000L );
        List<Send> sends = connection.getSends();
        assertTrue( "Sent " + sends.size() + " instead of 1.", sends.size() >= 1 );
        assertTrue( hb.getLastUpdate() > lastUpdate );
    }

}
