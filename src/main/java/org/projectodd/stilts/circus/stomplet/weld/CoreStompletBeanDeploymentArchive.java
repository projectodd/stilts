package org.projectodd.stilts.circus.stomplet.weld;

import org.projectodd.stilts.stomplet.simple.SimpleQueueStomplet;
import org.projectodd.stilts.stomplet.simple.SimpleTopicStomplet;

public class CoreStompletBeanDeploymentArchive extends SimpleCircusBeanDeploymentArchive {

    public CoreStompletBeanDeploymentArchive() {
        this( "core-stomplet", CoreStompletBeanDeploymentArchive.class.getClassLoader() );
    }
    
    protected CoreStompletBeanDeploymentArchive(String id, ClassLoader classLoader) {
        super( id, classLoader );
        addBeanClass( SimpleQueueStomplet.class.getName() ); 
        addBeanClass( SimpleTopicStomplet.class.getName() ); 
    }
    
}