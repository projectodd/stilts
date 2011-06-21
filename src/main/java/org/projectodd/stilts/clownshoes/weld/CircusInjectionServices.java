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
