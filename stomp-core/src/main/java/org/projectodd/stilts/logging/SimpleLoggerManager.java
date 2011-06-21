/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
