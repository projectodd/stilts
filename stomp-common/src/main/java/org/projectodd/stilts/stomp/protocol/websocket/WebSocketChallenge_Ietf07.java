package org.projectodd.stilts.stomp.protocol.websocket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class WebSocketChallenge_Ietf07 {

    public static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    public static final String SHA1 = "SHA1";
    
    private byte[] rawNonce;

    public WebSocketChallenge_Ietf07() throws NoSuchAlgorithmException {
        generateNonce();
    }

    protected void generateNonce() {
        this.rawNonce = new byte[16];
        
        Random random = new SecureRandom();
        random.nextBytes( this.rawNonce );
    }
    
    public String getNonceBase64() {
        return DatatypeConverter.printBase64Binary( this.rawNonce );
    }

    public static String solve(String nonceBase64) throws NoSuchAlgorithmException {
        String concat = nonceBase64 + GUID;
        
        MessageDigest digest = MessageDigest.getInstance( SHA1 );
        
        digest.update( concat.getBytes() );
        
        byte[] hashed = digest.digest();
        
        return DatatypeConverter.printBase64Binary( hashed );
    }

    public boolean verify(String solution) throws NoSuchAlgorithmException {
        String localSolution = solve( getNonceBase64() );
        
        return localSolution.equals( solution );
    }
}
