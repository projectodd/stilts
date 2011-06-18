package org.jboss.stilts.spi;

import org.jboss.stilts.StompException;

public interface Subscription {
    
    public static enum AckMode {
        AUTO("auto"),
        CLIENT("client"),
        CLIENT_INDIVIDUAL("client-individual"),;
        
        private String str;

        AckMode(String str) {
            this.str = str;
        }
        
        public String toString() {
            return this.str;
        }
    }
    
    String getId();
    void cancel() throws StompException;
}
