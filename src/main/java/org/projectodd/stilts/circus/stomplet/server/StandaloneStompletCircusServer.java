package org.projectodd.stilts.circus.stomplet.server;

import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.circus.server.StandaloneCircusServer;
import org.projectodd.stilts.circus.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.circus.stomplet.StompletContainer;
import org.projectodd.stilts.circus.stomplet.StompletMessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.circus.xa.psuedo.PsuedoXAMessageConduitFactory;

public class StandaloneStompletCircusServer extends StandaloneCircusServer<StompletCircusServer> {

    public StandaloneStompletCircusServer(StompletCircusServer server) {
        super( server );
    }

    public void configure() throws Throwable {
        super.configure();
        SimpleStompletContainer stompletContainer = new SimpleStompletContainer( );
        MessageConduitFactory conduitFactory = new StompletMessageConduitFactory( stompletContainer );
        XAMessageConduitFactory xaConduitFactory = new PsuedoXAMessageConduitFactory( conduitFactory );
        getServer().setStompletContainer( stompletContainer );
        getServer().setMessageConduitFactory( xaConduitFactory );
    }
    
    public StompletContainer getStompletContainer() {
        return getServer().getStompletContainer();
    }
    
    public void stop() throws Throwable {
        super.stop();
    }

}
