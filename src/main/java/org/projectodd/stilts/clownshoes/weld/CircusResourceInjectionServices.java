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
