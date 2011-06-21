/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.helpers;

import javax.transaction.TransactionManager;

import org.projectodd.stilts.stomp.spi.StompServerEnvironment;

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
