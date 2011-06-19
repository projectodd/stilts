package org.projectodd.stilts.circus.jms;

import javax.jms.XAConnection;
import javax.jms.XASession;

import org.projectodd.stilts.circus.xa.AbstractXAMessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;

public class JMSMessageConduitFactory extends AbstractXAMessageConduitFactory {

    public JMSMessageConduitFactory(XAConnection connection, DestinationMapper destinationMapper) {
        this.connection = connection;
        this.destinationMapper = destinationMapper;
    }
    
    @Override
    public XAMessageConduit createXAMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        XASession session = connection.createXASession();
        return new JMSMessageConduit( session, messageSink, this.destinationMapper );
    }
    
    private XAConnection connection;
    private DestinationMapper destinationMapper;

}
