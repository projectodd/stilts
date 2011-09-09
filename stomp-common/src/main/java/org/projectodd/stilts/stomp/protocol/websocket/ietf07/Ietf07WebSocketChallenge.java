package org.projectodd.stilts.stomp.protocol.websocket.ietf07;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.jboss.logging.Logger;

public class Ietf07WebSocketChallenge {

    private static Logger log = Logger.getLogger(Ietf07WebSocketChallenge.class);

    public static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    public static final String SHA1 = "SHA1";

    private byte[] rawNonce;

    public Ietf07WebSocketChallenge() throws NoSuchAlgorithmException {
        generateNonce();
    }

    protected void generateNonce() {
        this.rawNonce = new byte[16];

        Random random = new SecureRandom();
        random.nextBytes( this.rawNonce );
    }

    public String getNonceBase64() {
        return Base64.encodeBase64String( this.rawNonce ).trim();
    }

    public static String solve(String nonceBase64) throws NoSuchAlgorithmException {
        String concat = nonceBase64 + GUID;

        MessageDigest digest = MessageDigest.getInstance( SHA1 );

        digest.update( concat.getBytes() );

        byte[] hashed = digest.digest();

        return Base64.encodeBase64String( hashed ).trim();
    }

    public boolean verify(String solution) throws NoSuchAlgorithmException {
        log.errorf( "VERIFY VERIFY " + solution );
        if ( solution == null ) {
            return false;
        }
        String localSolution = solve( getNonceBase64() );
        log.errorf( "verify [" + solution + "] vs [" + localSolution + "]" );
        return localSolution.equals( solution );
    }
}
