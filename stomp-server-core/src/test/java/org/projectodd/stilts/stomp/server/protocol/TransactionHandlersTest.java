package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.*;

import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.server.MockStompConnection;
import org.projectodd.stilts.stomp.server.MockStompProvider;

public class TransactionHandlersTest {

    private MockStompProvider stompProvider;
    private MockStompConnection connection;
    private DecoderEmbedder<Object> handler;
    private ConnectionContext connectionContext;

    @Before
    public void setUp() throws StompException {
        this.stompProvider = new MockStompProvider();
        this.connectionContext = new ConnectionContext();
        this.connection = this.stompProvider.createConnection( null, null, Version.VERSION_1_1 );
        this.connectionContext.setStompConnection( this.connection );
        this.handler = new DecoderEmbedder<Object>(
                new BeginHandler( this.stompProvider, this.connectionContext ),
                new CommitHandler( this.stompProvider, this.connectionContext ),
                new AbortHandler( this.stompProvider, this.connectionContext )
                );
    }

    @Test
    public void testBeginAbort() {
        StompFrame frame = StompFrames.newBeginFrame( "transaction-bob" );
        this.handler.offer( frame );

        assertEquals( 1, this.connection.getBegins().size() );
        assertEquals( 0, this.connection.getCommits().size() );
        assertEquals( 0, this.connection.getAborts().size() );

        assertEquals( "transaction-bob", this.connection.getBegins().get( 0 ) );

        frame = StompFrames.newAbortFrame( "transaction-bob" );
        this.handler.offer( frame );

        assertEquals( 1, this.connection.getBegins().size() );
        assertEquals( 0, this.connection.getCommits().size() );
        assertEquals( 1, this.connection.getAborts().size() );

        assertEquals( "transaction-bob", this.connection.getBegins().get( 0 ) );
        assertEquals( "transaction-bob", this.connection.getAborts().get( 0 ) );

    }

    @Test
    public void testBeginCommit() {
        StompFrame frame = StompFrames.newBeginFrame( "transaction-bob" );
        this.handler.offer( frame );

        assertEquals( 1, this.connection.getBegins().size() );
        assertEquals( 0, this.connection.getCommits().size() );
        assertEquals( 0, this.connection.getAborts().size() );

        assertEquals( "transaction-bob", this.connection.getBegins().get( 0 ) );

        frame = StompFrames.newCommitFrame( "transaction-bob" );
        this.handler.offer( frame );

        assertEquals( 1, this.connection.getBegins().size() );
        assertEquals( 1, this.connection.getCommits().size() );
        assertEquals( 0, this.connection.getAborts().size() );

        assertEquals( "transaction-bob", this.connection.getBegins().get( 0 ) );
        assertEquals( "transaction-bob", this.connection.getCommits().get( 0 ) );

    }

}
