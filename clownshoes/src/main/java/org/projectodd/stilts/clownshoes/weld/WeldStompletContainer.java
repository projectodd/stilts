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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.manager.BeanManagerImpl;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.clownshoes.stomplet.NoSuchStompletException;
import org.projectodd.stilts.clownshoes.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.stomplet.Stomplet;

public class WeldStompletContainer extends SimpleStompletContainer {

    public WeldStompletContainer(boolean includeCore) {
        this.includeCore = includeCore;
    }

    public void addStomplet(String pattern, String className) throws StompException {
        addStomplet( pattern, className, new HashMap<String,String>() );
    }
    
    public void addStomplet(String pattern, String className, Map<String,String> properties) throws StompException {
        try {
            Stomplet stomplet = newStomplet( className );
            addStomplet( pattern, stomplet, properties );
        } catch (ClassNotFoundException e) {
            throw new StompException( e );
        }
    }

    protected Stomplet newStomplet(String className) throws ClassNotFoundException, NoSuchStompletException {

        Class<?> stompletImplClass = this.deployment.getClassLoader().loadClass( className );
        Set<Bean<?>> beans = this.beanManager.getBeans( stompletImplClass );

        if (beans.isEmpty()) {
            throw new NoSuchStompletException( className );
        }

        Bean<? extends Object> bean = this.beanManager.resolve( beans );

        CreationalContext<?> creationalContext = this.beanManager.createCreationalContext( bean );
        Stomplet stomplet = (Stomplet) beanManager.getReference( bean, stompletImplClass, creationalContext );

        return stomplet;
    }
    
    public void addBeanDeploymentArchive(CircusBeanDeploymentArchive archive) {
        this.archives.add( archive );
    }

    public void start() throws Exception {
        super.start();
        startWeld();
    }

    protected void startWeld() {
        this.bootstrap = new WeldBootstrap();
        this.deployment = new CircusDeployment();

        this.aggregationArchive = new AggregatingBeanDeploymentArchive( "things", this.deployment.getClassLoader() );
        for (CircusBeanDeploymentArchive each : this.archives) {
            this.aggregationArchive.addMemberArchive( each );
        }
        if (this.includeCore) {
            CoreStompletBeanDeploymentArchive core = new CoreStompletBeanDeploymentArchive();
            this.aggregationArchive.addMemberArchive( core );
        }
        this.deployment.addArchive( this.aggregationArchive );
        this.bootstrap.startContainer( Environments.SE, this.deployment );
        this.bootstrap.startInitialization();
        this.bootstrap.deployBeans();
        this.bootstrap.validateBeans();

        this.beanManager = this.bootstrap.getManager( this.aggregationArchive );
    }

    public void stop() throws Exception {
        stopWeld();
        super.stop();
    }

    protected void stopWeld() {
        this.bootstrap.shutdown();
    }

    private boolean includeCore;
    private CircusDeployment deployment;
    private List<CircusBeanDeploymentArchive> archives = new ArrayList<CircusBeanDeploymentArchive>();;
    private AggregatingBeanDeploymentArchive aggregationArchive;
    private WeldBootstrap bootstrap;
    private BeanManagerImpl beanManager;
}
