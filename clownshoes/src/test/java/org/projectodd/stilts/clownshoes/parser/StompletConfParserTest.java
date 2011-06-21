/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
