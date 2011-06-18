package org.jboss.stilts.spi;

public interface Acknowledger {
    
    void ack() throws Exception;
    void nack() throws Exception;

}
