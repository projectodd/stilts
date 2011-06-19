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

import java.io.PrintStream;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class SimpleLoggerManager implements LoggerManager {

    public static final SimpleLoggerManager DEFAULT_INSTANCE = new SimpleLoggerManager( System.err );

    public enum Level {
        TRACE("TRACE"),
        DEBUG("DEBUG"),
        INFO("INFO "),
        WARN("WARN "),
        ERROR("ERROR"),
        FATAL("FATAL"),
        NONE("NONE "), ;

        Level(String paddedString) {
            this.paddedString = paddedString;
        }

        public String toString() {
            return this.paddedString;
        }

        private String paddedString;

    }

    public SimpleLoggerManager(PrintStream out) {
        this( out, null );
    }

    public SimpleLoggerManager(PrintStream out, String prefix) {
        this.out = out;
        this.prefix = prefix;
        setRootLevel( Level.INFO );
    }

    public void setRootLevel(Level level) {
        this.levels.put( "", level );
    }

    public void setLevel(String prefix, Level level) {
        this.levels.put( prefix, level );
    }

    public boolean isEnabled(Level level, String name) {
        Level enabledLevel = null;
        for (String eachPrefix : this.levels.keySet()) {
            if (name.startsWith( eachPrefix )) {
                enabledLevel = this.levels.get( eachPrefix );
                break;
            }
        }

        if (level == null) {
            return false;
        }

        if (level.ordinal() >= enabledLevel.ordinal()) {
            return true;
        }

        return false;
    }

    @Override
    public Logger getLogger(String name) {
        return new SimpleLogger( this, name );
    }

    protected void log(Level level, String name, Object message, Throwable t) {
        if (isEnabled( level, name )) {
            String fullName = name;
            if (this.prefix != null) {
                fullName = this.prefix + "." + name;
            }
            synchronized (this.out) {
                this.out.println( level + " [" + fullName + "] " + message );
                while (t != null) {
                    this.out.println( level + " [" + fullName + "] " + t.getClass().getName() + ": " + t.getMessage() );
                    for (StackTraceElement stackLine : t.getStackTrace()) {
                        this.out.println( level + " [" + fullName + "]    " + stackLine.toString() );
                    }
                    t = t.getCause();
                    if (t != null) {
                        this.out.println( level + " [" + fullName + "] caused by: " );
                    }
                }
            }
        }
    }

    private PrintStream out;
    private String prefix;

    private Map<String, Level> levels = new TreeMap<String, Level>( new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            if (o1.length() > o2.length()) {
                return -1;
            }

            if (o1.length() < o2.length()) {
                return 1;
            }

            return 0;
        }
    } );

}
