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
