/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomp.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.projectodd.stilts.stomp.StompMessage;

class ReceiptFuture {
    
    ReceiptFuture() {
        this( NO_OP );
    }
    
    ReceiptFuture(Callable<Void> receiptHandler) {
        if ( receiptHandler == null ) {
            receiptHandler = NO_OP;
        }
        this.future = new FutureTask<Void>( receiptHandler );
    }

    void received(StompMessage errorMessage) {
        this.errorMessage = errorMessage;
        this.future.run();
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
