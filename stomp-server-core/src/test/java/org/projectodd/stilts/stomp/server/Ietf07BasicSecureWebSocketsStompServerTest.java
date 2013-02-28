package org.projectodd.stilts.stomp.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.junit.BeforeClass;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.protocol.websocket.Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.ietf07.Ietf07Handshake;

public class Ietf07BasicSecureWebSocketsStompServerTest extends AbstractWebSocketsStompServerTestCase {
    private static SSLContext SSL_CONTEXT;
    
    @BeforeClass 
    public static void setUpCrypto() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
        
        KeyStore keyStore = KeyStore.getInstance( "JKS" );
        InputStream stream = BasicStompServerTest.class.getClassLoader().getResourceAsStream( "keystore.jks" );
        keyStore.load( stream, "password".toCharArray() );
        
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509" );
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        
        kmf.init(keyStore, "password".toCharArray() );
        tmf.init( keyStore );
        
        SSL_CONTEXT = SSLContext.getInstance("TLS");
        SSL_CONTEXT.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }
    
    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>( SSL_CONTEXT );
        server.setStompProvider(new MockStompProvider());
        return server;
    }

    public String getConnectionUrl() {
        return "stomp+wss://localhost/";
    }
    
    public StompClient createClient() throws URISyntaxException {
        StompClient client = new StompClient( getConnectionUrl(), SSL_CONTEXT );
        client.setWebSocketHandshakeClass( getWebSocketHandshakeClass() );
        return client;
    }
    
    @Override
    public Class<? extends Handshake> getWebSocketHandshakeClass() {
        return Ietf07Handshake.class;
    }
    

}