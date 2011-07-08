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

package org.projectodd.stilts.clownshoes.stomplet;

import java.util.Map;

public class RouteConfig {
    
    private String pattern;
    private String className;
    private Map<String, String> properties;

    public RouteConfig() {
        
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setProperties(Map<String,String> properties) {
        this.properties = properties;
    }
    
    public Map<String,String> getProperties() {
        return this.properties;
    }

}
