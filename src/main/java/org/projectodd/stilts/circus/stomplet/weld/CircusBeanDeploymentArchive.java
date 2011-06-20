package org.projectodd.stilts.circus.stomplet.weld;

import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;

public interface CircusBeanDeploymentArchive extends BeanDeploymentArchive {
    
    ClassLoader getClassLoader();

}
