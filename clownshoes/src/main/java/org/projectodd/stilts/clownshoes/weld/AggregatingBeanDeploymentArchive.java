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
