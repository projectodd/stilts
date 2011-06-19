package org.projectodd.stilts;

public class StompException extends Exception {

    private static final long serialVersionUID = 6412092920015930823L;

    public StompException() {
        
    }
    
    public StompException(String message) {
        super( message );
    }

    public StompException(Throwable cause) {
        super( cause );
    }

}
