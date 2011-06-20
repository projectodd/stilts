package org.projectodd.stilts.circus.stomplet.weld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.injection.spi.InjectionServices;
import org.jboss.weld.injection.spi.ResourceInjectionServices;
import org.jboss.weld.resources.ClassLoaderResourceLoader;
import org.jboss.weld.resources.spi.ResourceLoader;

public class SimpleCircusBeanDeploymentArchive implements CircusBeanDeploymentArchive {

    public SimpleCircusBeanDeploymentArchive(String id, ClassLoader classLoader) {
        this.id = id;
        this.classLoader = classLoader;
        this.serviceRegistry = new SimpleServiceRegistry();
        this.serviceRegistry.add( InjectionServices.class, new CircusInjectionServices() );
        this.serviceRegistry.add( ResourceInjectionServices.class, new CircusResourceInjectionServices() );
        ClassLoaderResourceLoader resourceLoader = new ClassLoaderResourceLoader( classLoader );
        System.err.println( "LOADER: " + resourceLoader + " // " + classLoader );
        this.serviceRegistry.add( ResourceLoader.class, resourceLoader );

        this.beanClasses = new ArrayList<String>();
    }

    protected void addBeanClass(String beanClass) {
        this.beanClasses.add( beanClass );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getBeanClasses() {
        System.err.println( "RETUNRING CLASSES: " + this.beanClasses );
        return this.beanClasses;
    }

    @Override
    public BeansXml getBeansXml() {
        return null;
    }

    @Override
    public Collection<EjbDescriptor<?>> getEjbs() {
        return Collections.emptyList();
    }

    @Override
    public ServiceRegistry getServices() {
        return this.serviceRegistry;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    private String id;
    private ClassLoader classLoader;
    private ArrayList<String> beanClasses;
    private SimpleServiceRegistry serviceRegistry;

}
