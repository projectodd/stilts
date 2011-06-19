package org.projectodd.stilts.spi;

public interface Acknowledger {
    
    void ack() throws Exception;
    void nack() throws Exception;

}
