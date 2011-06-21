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

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.helpers.ForwardingBeanDeploymentArchive;

public class ShrinkwrapBeanDeploymentArchive extends ForwardingBeanDeploymentArchive implements CircusBeanDeploymentArchive {

    public ShrinkwrapBeanDeploymentArchive(JavaArchive archive, ClassLoader parent) throws Exception {
        File archiveFile = File.createTempFile( "circus-", ".tmp.jar" );
        archive.as(ZipExporter.class).exportTo( archiveFile, true );
        this.realBDA = new FileBeanDeploymentArchive( archiveFile, parent );
    }

    @Override
    public ServiceRegistry getServices() {
        return this.realBDA.getServices();
    }

    @Override
    public String getId() {
        return this.realBDA.getId();
    }
    

    @Override
    public ClassLoader getClassLoader() {
        return this.realBDA.getClassLoader();
    }

    @Override
    protected BeanDeploymentArchive delegate() {
        return this.realBDA;
    }
    
    private FileBeanDeploymentArchive realBDA;
}
