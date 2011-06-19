package org.jboss.stilts.circus.jms;

import javax.jms.XAConnection;
import javax.jms.XASession;

import org.jboss.stilts.circus.xa.AbstractXAMessageConduitFactory;
import org.jboss.stilts.circus.xa.XAMessageConduit;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;

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
