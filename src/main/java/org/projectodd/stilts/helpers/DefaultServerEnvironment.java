package org.projectodd.stilts.helpers;

import javax.transaction.TransactionManager;

import org.projectodd.stilts.spi.StompServerEnvironment;

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
