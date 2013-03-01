package org.projectodd.stilts.stomp.server.protocol;

import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.After;
import org.junit.Before;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.server.AbstractStompServerTestCase;
import org.projectodd.stilts.stomp.server.InsecureConnector;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;
import org.projectodd.stilts.stomp.spi.StompConnection;

public abstract class AbstractServerProtocolFrameHandlerTest<T extends ChannelUpstreamHandler> extends AbstractStompServerTestCase<MockStompProvider> {

    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        server.addConnector( new InsecureConnector() );
        return server;
    }

    public abstract T getHandler();

    @After
    public void after() throws Exception {
        ctx.getStompConnection().disconnect();
        handler.finish();
    }

    @Before
    public void before() throws Exception {
        ctx = new ConnectionContext();
        handler = new DecoderEmbedder<StompFrame>( getHandler() );
        StompConnection c = server.getStompProvider().createConnection( null, null, Version.VERSION_1_1 );
        ctx.setStompConnection( c );
    }

    protected ConnectionContext ctx;
    protected DecoderEmbedder<StompFrame> handler;

}
