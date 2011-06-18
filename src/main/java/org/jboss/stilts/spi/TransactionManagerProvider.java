package org.jboss.stilts.spi;

import javax.transaction.TransactionManager;

public interface TransactionManagerProvider {
    TransactionManager getTransactionManager();
}
