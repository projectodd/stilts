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
