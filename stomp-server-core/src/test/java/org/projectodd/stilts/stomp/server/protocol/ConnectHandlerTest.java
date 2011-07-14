package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.StompContentFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.server.AbstractStompServerTestCase;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;

public class ConnectHandlerTest extends AbstractStompServerTestCase<MockStompProvider> {

    private Channel channel;

    private ChannelHandlerContext channelHandlerContext;

    private ConnectHandler handler;

    private MockChannelWriteAnswer mockAnswer;

    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        return server;
    }

    @After
    public void after() throws Exception {
        channel.close();
    }

    @Before
    public void before() throws Exception {
        channel = mock( Channel.class );
        ConnectionContext ctx = new ConnectionContext();
        ChannelMessageSink messageSink = new ChannelMessageSink( channel, new AckManager() );
        ctx.setStompConnection( server.getStompProvider().createConnection( messageSink, null ) );
        handler = new ConnectHandler( server.getStompProvider(), ctx );
        channelHandlerContext = new MockChannelHandlerContext( channel, handler );
        mockAnswer = new MockChannelWriteAnswer();
        when( channel.write( anyObject() ) ).thenAnswer( mockAnswer );
    }

    @Test
    public void testBothVersionsAccepted() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.0,1.1" );
        handler.handleControlFrame( channelHandlerContext, stompFrame );
        assertEquals( 1, mockAnswer.getWriteBuffer().size() );
        StompFrame frame = mockAnswer.getWriteBuffer().get( 0 );
        assertEquals( Command.CONNECTED, frame.getCommand() );
        assertEquals( "1.1", frame.getHeader( Header.VERSION ) );
    }

    @Test
    public void testSpacesVersionString() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "1.0 1.1" );
        handler.handleControlFrame( channelHandlerContext, stompFrame );
        assertEquals( 1, mockAnswer.getWriteBuffer().size() );
        StompContentFrame frame = (StompContentFrame) mockAnswer.getWriteBuffer().get( 0 );
        Command command = frame.getCommand();
        assertEquals( Command.ERROR, command );
        assertEquals( "Accept-version header value must be an incrementing comma-separated list.", new String( frame.getContent().array() ) );
    }

    @Test
    public void testNoMatchingVersions() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        stompFrame.setHeader( Header.ACCEPT_VERSION, "beef" );
        handler.handleControlFrame( channelHandlerContext, stompFrame );
        assertEquals( 1, mockAnswer.getWriteBuffer().size() );
        StompContentFrame frame = (StompContentFrame) mockAnswer.getWriteBuffer().get( 0 );
        Command command = frame.getCommand();
        assertEquals( Command.ERROR, command );
        assertEquals( "Supported protocol versions are 1.0 1.1", new String( frame.getContent().array() ) );
    }

    @Test
    public void testNoAcceptsHeader() {
        StompFrame stompFrame = new StompFrame( Command.CONNECT );
        handler.handleControlFrame( channelHandlerContext, stompFrame );
        assertEquals( 1, mockAnswer.getWriteBuffer().size() );
        StompFrame frame = mockAnswer.getWriteBuffer().get( 0 );
        assertEquals( Command.CONNECTED, frame.getCommand() );
        assertNull( frame.getHeader( Header.VERSION ) );
    }

}
