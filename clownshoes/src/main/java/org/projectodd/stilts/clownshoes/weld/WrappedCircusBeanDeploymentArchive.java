/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
