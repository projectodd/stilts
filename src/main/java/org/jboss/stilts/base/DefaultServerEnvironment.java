package org.jboss.stilts.base;

import javax.transaction.TransactionManager;

import org.jboss.stilts.spi.StompServerEnvironment;

public class DefaultServerEnvironment implements StompServerEnvironment {

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    private TransactionManager transactionManager;
}
