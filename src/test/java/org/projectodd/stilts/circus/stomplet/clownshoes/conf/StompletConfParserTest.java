package org.projectodd.stilts.circus.stomplet.clownshoes.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class StompletConfParserTest {

    @Test
    public void testParse() throws Exception {
        List<RouteConfiguration> result = parse( "valid-stomplet.conf" );

        assertNotNull( result );
        assertFalse( result.isEmpty() );
        
        System.err.println( result );
    }

    protected List<RouteConfiguration> parse(String name) throws IOException {
        InputStream in = getClass().getResourceAsStream( name );

        try {
            StompletConfParser parser = new StompletConfParser( in );

            return parser.parse();
        } finally {
            in.close();
        }

    }

}
