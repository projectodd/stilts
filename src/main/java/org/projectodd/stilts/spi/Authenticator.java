package org.projectodd.stilts.spi;


public interface Authenticator {
    
    boolean authenticate(Headers headers);

}
