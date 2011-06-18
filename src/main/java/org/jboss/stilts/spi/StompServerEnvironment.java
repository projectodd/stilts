package org.jboss.stilts.spi;

import javax.transaction.TransactionManager;

public interface StompServerEnvironment {
    
    TransactionManager getTransactionManager();

}
