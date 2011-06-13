package org.jboss.stilts;

public class InvalidMessageException extends StompException {

    public InvalidMessageException(String messageId) {
        super( "Invalid message id: " + messageId );
        this.messageId = messageId;
    }
    
    public String getMessageId() {
        return this.messageId;
    }

    private static final long serialVersionUID = 1L;

    private String messageId;

}
