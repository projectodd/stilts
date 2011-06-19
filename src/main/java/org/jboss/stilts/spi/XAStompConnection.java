package org.jboss.stilts.spi;

import javax.transaction.xa.XAResource;

public interface XAStompConnection extends StompConnection {
    
    XAResource getXAResource();

}
