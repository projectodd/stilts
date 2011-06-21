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
        System.err.println( "FIND_CLASS: " + name );
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
