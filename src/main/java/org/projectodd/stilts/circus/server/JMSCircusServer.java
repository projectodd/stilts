package org.projectodd.stilts.circus.server;

import javax.jms.XAConnection;

import org.projectodd.stilts.circus.jms.DestinationMapper;
import org.projectodd.stilts.circus.jms.JMSMessageConduitFactory;

public class JMSCircusServer extends AbstractCircusServer {

    public JMSCircusServer() {
        super();
    }
    
    public JMSCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Exception {
        startConduitFactory();
        super.start();
    }
    
    protected void startConduitFactory() {
        JMSMessageConduitFactory factory = new JMSMessageConduitFactory( getConnection(), getDestinationMapper() );
        setMessageConduitFactory( factory );
    }
    
    public void setConnection(XAConnection connection) {
        this.connection = connection;
    }
    
    public XAConnection getConnection() {
        return this.connection;
    }
    
    public void setDestinationMapper(DestinationMapper destinationMapper) {
        this.destinationMapper = destinationMapper;
    }
    
    public DestinationMapper getDestinationMapper() {
        return this.destinationMapper;
    }

    private DestinationMapper destinationMapper;
    private XAConnection connection;

}
