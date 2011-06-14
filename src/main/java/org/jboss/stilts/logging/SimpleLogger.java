package org.jboss.stilts.logging;

import org.jboss.stilts.logging.SimpleLoggerManager.Level;


public class SimpleLogger implements Logger {

    public static final SimpleLogger DEFAULT = new SimpleLogger( SimpleLoggerManager.DEFAULT_INSTANCE, "LOG" );

    public SimpleLogger(SimpleLoggerManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    private SimpleLoggerManager manager;
    private String name;

    protected void log(Level level, Object message, Throwable t) {
        this.manager.log( level, this.name, message, t );
    }

    @Override
    public void fatal(Object message) {
        log( Level.FATAL, message, null);
    }

    @Override
    public void fatal(Object message, Throwable t) {
        log( Level.FATAL, message, t);
    }

    @Override
    public void error(Object message) {
        log( Level.ERROR, message, null);
    }

    @Override
    public void error(Object message, Throwable t) {
        log( Level.ERROR, message, t);

    }

    @Override
    public void warn(Object message) {
        log( Level.WARN, message, null);
    }

    @Override
    public void info(Object message) {
        log( Level.INFO, message, null);
    }

    @Override
    public void debug(Object message) {
        log( Level.DEBUG, message, null);
    }

    @Override
    public void trace(Object message) {
        log( Level.TRACE, message, null);
    }

}
