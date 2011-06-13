package org.jboss.stilts.jms;

import javax.jms.Connection;
import javax.jms.Session;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.base.AbstractClientAgent;

public class JMSClientAgent extends AbstractClientAgent {

    public JMSClientAgent(JMSStompServer server, MessageSink messageSink, String sessionId) throws StompException {
        super( server, messageSink, sessionId );
    }

    protected JMSTransaction createTransaction(String transactionId) throws Exception {
        Session session = getServer().getConnection().createSession( false, Session.CLIENT_ACKNOWLEDGE );
        return new JMSTransaction( this, session, transactionId );
    }

    public JMSStompServer getServer() {
        return (JMSStompServer) super.getServer();
    }

    protected Connection getConnection() {
        return getServer().getConnection();
    }

}
