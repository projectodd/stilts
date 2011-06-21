/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.clownshoes.weld;

import java.util.ArrayList;
import java.util.List;

public class DeploymentClassLoader extends ClassLoader {
    
    public DeploymentClassLoader() {
        
    }
    
    public void addClossLoader(ClassLoader classLoader) {
        this.classLoaders.add( classLoader );
    }
    
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for ( ClassLoader each : this.classLoaders ) {
            try {
                Class<?> result = each.loadClass( name );
                if ( result != null ) {
                    return result;
                }
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return super.findClass( name );
    }


    private final List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();

}
