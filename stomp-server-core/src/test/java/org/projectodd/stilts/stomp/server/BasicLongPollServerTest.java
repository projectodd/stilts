package org.projectodd.stilts.stomp.server;

import java.io.IOException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BasicLongPollServerTest extends AbstractStompServerTestCase<MockStompProvider> {

    private CloseableHttpClient client;
    private HttpClientContext httpContext;

    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        server.addConnector( new InsecureConnector() );
        return server;
    }

    @Before
    public void createClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionReuseStrategy( new NoConnectionReuseStrategy() );
        this.httpContext = new HttpClientContext();
        this.client = builder.build();
    }

    @After
    public void closeClient() {
        try {
            this.client.close();
            this.client = null;
        } catch (Exception e) {
            // ignore;
        }
    }

    @Test
    public void testConnectDisconnect() throws Exception {
        send( "CONNECT" );

        Thread.sleep( 200 );

        send( "SUBSCRIBE",
                "id: 1",
                "destination: /foo" );

        Thread.sleep( 200 );

        send( "SUBSCRIBE",
                "id: 2",
                "destination: /bar" );

        send( "DISCONNECT" );
        Thread.sleep( 500 );

    }

    protected void send(String... payload) throws ClientProtocolException, IOException {

        StringBuffer fullPayload = new StringBuffer();
        for (String line : payload) {
            fullPayload.append( line );
            fullPayload.append( "\n" );
        }
        fullPayload.append( "\n" ).append( "\0" );
        HttpPost request = new HttpPost( "http://localhost:8675" );
        ByteArrayEntity entity = new ByteArrayEntity( fullPayload.toString().getBytes() );
        request.setEntity( entity );

        client.execute( request, new ResponseHandler<Void>() {

            @Override
            public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                System.err.println( "RESPONSE: " + response );
                return null;
            }
        }, httpContext );

    }
}
