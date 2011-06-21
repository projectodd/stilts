/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.clownshoes.stomplet;

import static org.junit.Assert.*;

import org.junit.Test;
import org.projectodd.stilts.clownshoes.stomplet.Route;
import org.projectodd.stilts.clownshoes.stomplet.RouteMatch;

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
