package org.projectodd.stilts.logging;

public interface Logger {

    void fatal(Object message);
    void fatal(Object message, Throwable t);
    
    void error(Object message);
    void error(Object message, Throwable t);
    
    void warn(Object message);
    
    void info(Object message);
    
    void debug(Object message);
    
    void trace(Object message);
}
