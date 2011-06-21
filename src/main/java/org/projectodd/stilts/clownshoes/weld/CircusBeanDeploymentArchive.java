package org.projectodd.stilts.clownshoes.weld;

import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;

public interface CircusBeanDeploymentArchive extends BeanDeploymentArchive {
    
    ClassLoader getClassLoader();

}
