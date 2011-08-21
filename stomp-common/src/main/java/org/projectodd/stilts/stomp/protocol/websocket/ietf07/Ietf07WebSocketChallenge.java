package org.projectodd.stilts.stomp.protocol.websocket.ietf07;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

public class Ietf07WebSocketChallenge {

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
        return Base64.encodeBase64String( this.rawNonce );
    }

    public static String solve(String nonceBase64) throws NoSuchAlgorithmException {
        String concat = nonceBase64 + GUID;
        
        MessageDigest digest = MessageDigest.getInstance( SHA1 );
        
        digest.update( concat.getBytes() );
        
        byte[] hashed = digest.digest();
        
        return Base64.encodeBase64String( hashed ).trim();
    }

    public boolean verify(String solution) throws NoSuchAlgorithmException {
        if ( solution == null ) {
            return false;
        }
        String localSolution = solve( getNonceBase64() );
        return localSolution.equals( solution );
    }
}
