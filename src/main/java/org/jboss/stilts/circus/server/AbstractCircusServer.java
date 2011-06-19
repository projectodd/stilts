package org.jboss.stilts.circus.server;

import javax.transaction.TransactionManager;

import org.jboss.stilts.circus.CircusStompProvider;
import org.jboss.stilts.circus.MessageConduitFactory;
import org.jboss.stilts.circus.xa.XAMessageConduitFactory;
import org.jboss.stilts.circus.xa.psuedo.PsuedoXAMessageConduitFactory;
import org.jboss.stilts.server.BasicStompServer;

public abstract class AbstractCircusServer extends BasicStompServer {

    public AbstractCircusServer() {
        super();
    }
    
    /**
     * Construct with a port.
     * 
     * @param port The listen port to bind to.
     */
    public AbstractCircusServer(int port) {
        super( port );
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }
    
    public void setMessageConduitFactory(XAMessageConduitFactory messageConduitFactory) {
        this.messageConduitFactory = messageConduitFactory;
    }
    
    public XAMessageConduitFactory getMessageConduitFactory() {
        return this.messageConduitFactory;
    }
    
    @Override
    public void start() throws Exception {
        MessageConduitFactory factory = this.messageConduitFactory;
        XAMessageConduitFactory xaFactory = null;
        
        if ( factory instanceof XAMessageConduitFactory ) {
            xaFactory = (XAMessageConduitFactory) factory;
        } else {
            xaFactory = new PsuedoXAMessageConduitFactory( factory );
        }
        
        CircusStompProvider provider = new CircusStompProvider( this.transactionManager, xaFactory );
        setStompProvider( provider );
        super.start();
    }



    private TransactionManager transactionManager;
    private XAMessageConduitFactory messageConduitFactory;

}
