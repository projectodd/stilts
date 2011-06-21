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

import javax.annotation.Resource;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.injection.spi.ResourceInjectionServices;

public class CircusResourceInjectionServices implements ResourceInjectionServices {


    private int counter;
    @Override
    public Object resolveResource(InjectionPoint injectionPoint) {
        System.err.println( "resolveResource: " + injectionPoint );
        System.err.println( "resolveResource.annotated: " + injectionPoint.getAnnotated() );
        System.err.println( "resolveResource.annotated.resource: " + injectionPoint.getAnnotated().getAnnotation( Resource.class ) );
                
        Resource resourceAnno = injectionPoint.getAnnotated().getAnnotation( Resource.class );
        System.err.println( "resource: " + resourceAnno.name() );
        return null;
    }

    @Override
    public Object resolveResource(String jndiName, String mappedName) {
        System.err.println( "resolveResource: " + jndiName + ", " + mappedName );
        return jndiName;
    }
    
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub
        
    }

}
