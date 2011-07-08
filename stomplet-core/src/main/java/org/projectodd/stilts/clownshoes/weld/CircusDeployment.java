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

import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.resources.DefaultResourceLoader;
import org.jboss.weld.resources.spi.ResourceLoader;

public class CircusDeployment implements Deployment {

    public CircusDeployment() {
        this.serviceRegistry = new SimpleServiceRegistry();
        this.serviceRegistry.add( ResourceLoader.class, DefaultResourceLoader.INSTANCE );
        this.classLoader = new DeploymentClassLoader();
    }

    protected void addArchive(CircusBeanDeploymentArchive archive) {
        this.archives.add( archive );
        if (!(archive instanceof AggregatingBeanDeploymentArchive)) {
            this.classLoader.addClossLoader( archive.getClassLoader() );
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
        List<BeanDeploymentArchive> deploymentArchives = new ArrayList<BeanDeploymentArchive>();
        deploymentArchives.addAll( this.archives );
        return deploymentArchives;
    }

    @Override
    public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> beanClass) {
        return null;
    }

    @Override
    public ServiceRegistry getServices() {
        return this.serviceRegistry;
    }

    @Override
    public Iterable<Metadata<Extension>> getExtensions() {
        return this.extensions;
    }

    private DeploymentClassLoader classLoader;
    private final SimpleServiceRegistry serviceRegistry;
    private final List<CircusBeanDeploymentArchive> archives = new ArrayList<CircusBeanDeploymentArchive>();
    private final List<Metadata<Extension>> extensions = new ArrayList<Metadata<Extension>>();
}
