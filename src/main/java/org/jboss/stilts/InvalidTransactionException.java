package org.jboss.stilts;

public class InvalidTransactionException extends StompException {
    
    
    public InvalidTransactionException(String transactionId) {
        super( "Invalid transaction: " + transactionId);
        this.transactionId = transactionId;
    }
    
    public String getTransactionId() {
        return this.transactionId;
    }
    
    private static final long serialVersionUID = 1L;
    private String transactionId;

}
