package org.projectodd.stilts.stomplet;

import java.util.Set;

import javax.transaction.xa.XAResource;

public interface XAStomplet extends Stomplet {
    
    Set<XAResource> getXAResources();

}
