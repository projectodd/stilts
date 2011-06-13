package org.jboss.stilts.spi;


public interface Authenticator {
    
    boolean authenticate(Headers headers);

}
