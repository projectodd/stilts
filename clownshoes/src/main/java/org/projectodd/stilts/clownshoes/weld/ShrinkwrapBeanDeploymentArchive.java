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
