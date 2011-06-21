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