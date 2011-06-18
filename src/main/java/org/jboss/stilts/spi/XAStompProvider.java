package org.jboss.stilts.spi;

import javax.transaction.xa.XAResource;

public interface XAStompProvider extends StompProvider {
    
    XAResource getXAResource();

}
