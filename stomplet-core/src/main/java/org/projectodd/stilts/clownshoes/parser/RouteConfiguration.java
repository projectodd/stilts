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
