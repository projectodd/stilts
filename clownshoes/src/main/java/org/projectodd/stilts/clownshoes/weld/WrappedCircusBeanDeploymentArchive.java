/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.clownshoes.weld;

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
