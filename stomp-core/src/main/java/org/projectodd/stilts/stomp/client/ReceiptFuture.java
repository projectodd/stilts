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

package org.projectodd.stilts.stomp.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.projectodd.stilts.StompMessage;

public class ReceiptFuture {
    
    public ReceiptFuture() {
        this( NO_OP );
    }
    
    public ReceiptFuture(Callable<Void> receiptHandler) {
        if ( receiptHandler == null ) {
            receiptHandler = NO_OP;
        }
        this.future = new FutureTask<Void>( receiptHandler );
    }

    public void received() {
        this.future.run();
    }
    
    public void received(StompMessage errorMessage) {
        this.future.run();
        this.errorMessage = errorMessage;
    }
    
    public StompMessage await() throws InterruptedException, ExecutionException {
        this.future.get();
        return this.errorMessage;
    }
    
    public boolean isError() throws InterruptedException, ExecutionException {
        await();
        return (this.errorMessage!=null);
    }
    
    public StompMessage getErrorMessage() {
        return this.errorMessage;
    }
    
    private static final Callable<Void> NO_OP = new Callable<Void>() {
        public Void call() throws Exception {
            return null;
        }
    };


    private FutureTask<Void> future;
    private StompMessage errorMessage;
}
