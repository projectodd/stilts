/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.logging;

import org.projectodd.stilts.logging.SimpleLoggerManager.Level;


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
