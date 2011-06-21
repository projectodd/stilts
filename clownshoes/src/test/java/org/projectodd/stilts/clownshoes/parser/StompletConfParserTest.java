/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.clownshoes.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.projectodd.stilts.clownshoes.parser.RouteConfiguration;
import org.projectodd.stilts.clownshoes.parser.StompletConfParser;

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
