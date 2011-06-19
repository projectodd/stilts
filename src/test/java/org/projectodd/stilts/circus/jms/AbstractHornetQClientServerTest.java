package org.projectodd.stilts.circus.jms;

import org.projectodd.stilts.circus.AbstractCircusClientServerTest;
import org.projectodd.stilts.circus.jms.DirectDestinationMapper;
import org.projectodd.stilts.circus.server.HornetQCircusServer;

public abstract class AbstractHornetQClientServerTest extends AbstractCircusClientServerTest<HornetQCircusServer> {

    public HornetQCircusServer createServer() throws Exception {
        HornetQCircusServer server = new HornetQCircusServer();
        server.setLoggerManager( this.serverLoggerManager );
        server.setDestinationMapper( DirectDestinationMapper.INSTANCE );
        return server;
    }
    
    public void prepareServer() throws Exception {
        getServer().addQueue( "/queues/foo" );
        getServer().addTopic( "/topics/foo" );
    }


}
