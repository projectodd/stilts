package org.projectodd.stilts.circus.xa;

import javax.transaction.xa.XAResource;

import org.projectodd.stilts.circus.MessageConduit;

public interface XAMessageConduit extends MessageConduit {

    XAResource getXAResource();
}
