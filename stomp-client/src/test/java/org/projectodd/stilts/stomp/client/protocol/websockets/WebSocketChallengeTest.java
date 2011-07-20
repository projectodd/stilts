package org.projectodd.stilts.stomp.client.protocol.websockets;

import static org.junit.Assert.*;

import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketChallenge;

public class WebSocketChallengeTest {

    @Test
    public void testChallengeConstruction() throws Exception {
        WebSocketChallenge challenge = new WebSocketChallenge();

        assertTrue( challenge.getKey1() > 0 );
        assertTrue( challenge.getKey2() > 0 );

        assertTrue( challenge.getSpaces1() >= 1 );
        assertTrue( challenge.getSpaces1() >= 1 );

        WebSocketChallenge challenge2 = new WebSocketChallenge();

        assertTrue( challenge2.getKey1() > 0 );
        assertTrue( challenge2.getKey2() > 0 );

        assertTrue( challenge2.getSpaces1() >= 1 );
        assertTrue( challenge2.getSpaces1() >= 1 );

        assertFalse( challenge.getKey1() == challenge2.getKey1() );
        assertFalse( challenge.getKey2() == challenge2.getKey2() );
    }

    @Test
    public void testKeyCodec() throws Exception {
        for (long key = 0; key < 4294967295L; key += 10000) {
            if (key > 0) {
                String encoded = WebSocketChallenge.encodeKey( key, 4 );
                assertEquals( key, WebSocketChallenge.decodeKey( encoded ) );
            }
        }
    }

    @Test
    public void testSolve() throws Exception {

        WebSocketChallenge challenge = new WebSocketChallenge();

        String key1 = challenge.getKey1String();
        String key2 = challenge.getKey2String();
        byte[] key3 = challenge.getKey3();

        assertTrue( challenge.verify( WebSocketChallenge.solve( key1, key2, key3 ) ) );

    }

}
