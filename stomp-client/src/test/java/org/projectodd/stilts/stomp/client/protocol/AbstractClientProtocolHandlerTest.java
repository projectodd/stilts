package org.projectodd.stilts.stomp.client.protocol;

import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.projectodd.stilts.stomp.client.MockClientContext;
import org.projectodd.stilts.stomp.protocol.StompFrame;

public abstract class AbstractClientProtocolHandlerTest<T extends ChannelUpstreamHandler> {

    public abstract T getHandler() throws Exception;

    @Before
    public void before() throws Exception {
        this.clientContext = new MockClientContext();
        this.handler = new DecoderEmbedder<StompFrame>( getHandler() );
    }

    protected MockClientContext clientContext;
    protected DecoderEmbedder<?> handler;

}
