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

package org.projectodd.stilts.stomplet.cdi.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.container.NoSuchStompletException;
import org.projectodd.stilts.stomplet.container.SimpleStompletContainer;

public class WeldStompletContainer extends SimpleStompletContainer {

    public WeldStompletContainer(ClassLoader classLoader, BeanManager beanManager) {
        this.classLoader = classLoader;
        this.beanManager = beanManager;
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

        Class<?> stompletImplClass = this.classLoader.loadClass( className );
        Set<Bean<?>> beans = this.beanManager.getBeans( stompletImplClass );

        if (beans.isEmpty()) {
            throw new NoSuchStompletException( className );
        }

        Bean<? extends Object> bean = this.beanManager.resolve( beans );

        CreationalContext<?> creationalContext = this.beanManager.createCreationalContext( bean );
        Stomplet stomplet = (Stomplet) beanManager.getReference( bean, stompletImplClass, creationalContext );

        return stomplet;
    }
    
    private ClassLoader classLoader;
    private BeanManager beanManager;
}
