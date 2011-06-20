package org.projectodd.stilts.circus.stomplet.weld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.helpers.ForwardingBeanDeploymentArchive;

public class WrappedCircusBeanDeploymentArchive extends ForwardingBeanDeploymentArchive implements CircusBeanDeploymentArchive {

    private CircusBeanDeploymentArchive delegate;

    public WrappedCircusBeanDeploymentArchive(CircusBeanDeploymentArchive delegate) {
        this.delegate = delegate;
    }
    
    public void addExtraArchive(CircusBeanDeploymentArchive archive) {
        this.extraArchives.add( archive );
    }
    
    @Override
    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
        List<BeanDeploymentArchive> full = new ArrayList<BeanDeploymentArchive>();
        full.addAll( this.extraArchives );
        return full;
    }


    @Override
    public ServiceRegistry getServices() {
        return delegate().getServices();
    }

    @Override
    public String getId() {
        return delegate().getId();
    }

    @Override
    protected CircusBeanDeploymentArchive delegate() {
        return this.delegate;
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate().getClassLoader();
    }

    
    private List<CircusBeanDeploymentArchive> extraArchives = new ArrayList<CircusBeanDeploymentArchive>();
}
