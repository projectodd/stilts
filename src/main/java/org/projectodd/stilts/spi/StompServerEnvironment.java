package org.projectodd.stilts.spi;

import javax.transaction.TransactionManager;

public interface StompServerEnvironment {
    
    TransactionManager getTransactionManager();

}
