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

package org.projectodd.stilts.stomplet.container;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.projectodd.stilts.stomplet.container.Route;
import org.projectodd.stilts.stomplet.container.RouteMatch;

public class RouteTest {
    
    @Test
    public void testStraightMatch() throws Exception {
        Route route = new Route( "/queues/foo", null );
        
        RouteMatch match = route.match( "/queues/bar" );
        assertNull( match );
        
        match = route.match( "/queues/foo" );
        assertNotNull( match );
    }
    
    @Test
    public void testMatchWithSegments() throws Exception {
        Route route = new Route( "/stocks/:stock", null );
        
        RouteMatch match = route.match( "/stocks/AAPL" );
        assertNotNull( match );
        assertEquals( "AAPL", match.get( "stock" ) );
        
        match = route.match( "/stocks/RHT" );
        assertNotNull( match );
        assertEquals( "RHT", match.get( "stock" ) );
        
        match = route.match( "/bonds/AAPL" );
        assertNull( match );
    }
    
    @Test
    public void testMatchWithSeveralSegments() throws Exception {
        Route route = new Route( "/stocks/:exchange/:stock", null );
        
        RouteMatch match = route.match( "/stocks/NASDAQ/AAPL" );
        assertNotNull( match );
        assertEquals( "NASDAQ", match.get( "exchange" ) );
        assertEquals( "AAPL", match.get( "stock" ) );
        
        match = route.match( "/stocks/NYSE/RHT" );
        assertNotNull( match );
        assertEquals( "NYSE", match.get( "exchange" ) );
        assertEquals( "RHT", match.get( "stock" ) );
        
        match = route.match( "/bonds/AAPL" );
        assertNull( match );
        
        match = route.match( "/bonds/whut/AAPL" );
        assertNull( match );
    }

}
