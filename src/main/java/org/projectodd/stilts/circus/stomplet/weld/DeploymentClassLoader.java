package org.projectodd.stilts.circus.stomplet.weld;

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
