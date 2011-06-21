package org.projectodd.stilts.clownshoes.weld;

import org.jboss.weld.resources.ClassLoaderResourceLoader;

public class CoreClassLoaderResourceLoader extends ClassLoaderResourceLoader {

    public CoreClassLoaderResourceLoader(ClassLoader classLoader) {
        super( classLoader );
    }
    
    public void cleanup() {
        // nothing
    }

}
