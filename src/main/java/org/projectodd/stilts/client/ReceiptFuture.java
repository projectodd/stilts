/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.client;

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
