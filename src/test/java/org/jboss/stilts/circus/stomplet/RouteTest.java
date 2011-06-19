package org.jboss.stilts.circus.stomplet;

import static org.junit.Assert.*;

import org.junit.Test;

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
