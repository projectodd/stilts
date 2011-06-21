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
