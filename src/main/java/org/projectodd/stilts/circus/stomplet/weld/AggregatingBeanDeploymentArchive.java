package org.projectodd.stilts.circus.stomplet.weld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.ejb.spi.EjbDescriptor;

public class AggregatingBeanDeploymentArchive implements CircusBeanDeploymentArchive {


    public AggregatingBeanDeploymentArchive(String id, ClassLoader classLoader) {
        this.id = id;
        this.classLoader = classLoader;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    public void addMemberArchive(BeanDeploymentArchive archive) {
        this.archives.add( archive) ;
    }
    
    @Override
    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
        return this.archives;
    }

    @Override
    public Collection<String> getBeanClasses() {
        return Collections.emptyList();
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


    private String id;
    private ClassLoader classLoader;
    private ServiceRegistry serviceRegistry = new SimpleServiceRegistry();
    private List<BeanDeploymentArchive> archives = new ArrayList<BeanDeploymentArchive>();
    
}
