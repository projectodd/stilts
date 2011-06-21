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

package org.projectodd.stilts.clownshoes.weld;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.injection.spi.ResourceInjectionServices;

public class CircusResourceInjectionServices implements ResourceInjectionServices {


    private int counter;
    @Override
    public Object resolveResource(InjectionPoint injectionPoint) {
        //Resource resourceAnno = injectionPoint.getAnnotated().getAnnotation( Resource.class );
        //System.err.println( "resource: " + resourceAnno.name() );
        return null;
    }

    @Override
    public Object resolveResource(String jndiName, String mappedName) {
        return null;
    }
    
    @Override
    public void cleanup() {
        
    }

}
