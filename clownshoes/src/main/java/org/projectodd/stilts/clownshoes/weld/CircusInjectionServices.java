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

import org.jboss.weld.injection.spi.InjectionContext;
import org.jboss.weld.injection.spi.InjectionServices;

public class CircusInjectionServices implements InjectionServices {

    @Override
    public <T> void aroundInject(InjectionContext<T> injectionContext) {
        System.err.println( "injecting for:           " + injectionContext);
        System.err.println( "  annotated.type:        " + injectionContext.getAnnotatedType() );
        System.err.println( "  annotated.target:      " + injectionContext.getTarget() );
        System.err.println( "  annotated.inj-target:  " + injectionContext.getInjectionTarget() );
        injectionContext.proceed();
        System.err.println( "  proceeded.target:      " + injectionContext.getTarget() );
    }

    @Override
    public void cleanup() {
        System.err.println( "cleanup" );
    }

}
