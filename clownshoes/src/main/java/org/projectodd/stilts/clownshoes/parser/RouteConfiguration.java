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

package org.projectodd.stilts.clownshoes.parser;

import java.util.HashMap;
import java.util.Map;

public class RouteConfiguration {
    
    public RouteConfiguration(String pattern, String className) {
       this.pattern = pattern;
       this.className = className;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public String getStompletClassName() {
        return this.className;
    }
    
    public Map<String,String> getProperties() {
        return this.properties;
    }
    
    public String toString() {
        return "[RouteConfiguration: pattern=" + this.pattern + "\n" 
            + "className=" + this.className + "\n"
            + "properties=" + this.properties + "]";
        
    }
    
    private final String pattern;
    private final String className;
    private final Map<String, String> properties = new HashMap<String,String>();


}
