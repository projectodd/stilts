package org.jboss.stilts.circus.xa;

import javax.transaction.xa.XAResource;

import org.jboss.stilts.circus.MessageConduit;

public interface XAMessageConduit extends MessageConduit {

    XAResource getXAResource();
}
