package org.projectodd.stilts.circus.server;

import org.projectodd.stilts.circus.stomplet.StompletContainer;
import org.projectodd.stilts.circus.stomplet.StompletMessageConduitFactory;
import org.projectodd.stilts.circus.xa.psuedo.PsuedoXAMessageConduitFactory;

public class StompletCircusServer extends AbstractCircusServer {

    public StompletCircusServer() {
    }
    
    public StompletCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Exception {
        startConduitFactory();
        this.stompletContainer.start();
        super.start();
    }
    
    protected void startConduitFactory() {
        StompletMessageConduitFactory factory = new StompletMessageConduitFactory( this.stompletContainer );
        PsuedoXAMessageConduitFactory xaFactory = new PsuedoXAMessageConduitFactory( factory );
        setMessageConduitFactory( xaFactory );
    }

    public void setStompletContainer(StompletContainer stompletContainer) {
        this.stompletContainer = stompletContainer;
    }
    
    public StompletContainer getStompletContainer() {
        return this.stompletContainer;
    }

    private StompletContainer stompletContainer;

}
