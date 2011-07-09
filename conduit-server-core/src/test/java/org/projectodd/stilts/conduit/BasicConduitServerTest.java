package org.projectodd.stilts.conduit;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class BasicConduitServerTest extends AbstractConduitServerTestCase<MockMessageConduitFactory> {

    @Override
    protected ConduitServer<MockMessageConduitFactory> createServer() throws Exception {
        ConduitServer<MockMessageConduitFactory> server = new ConduitServer<MockMessageConduitFactory>();
        server.setMessageConduitFactory( new MockMessageConduitFactory() );
        return server;
    }
    
    @Test
    public void testStuff() {
        assertNotNull( this.server );
        assertNotNull( this.server.getMessageConduitFactory() );
        assertNotNull( this.server.getTransactionManager() );
    }

}
