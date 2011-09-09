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

package org.projectodd.stilts.stomplet.bundle;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.projectodd.stilts.conduit.stomp.SimpleStompSessionManager;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.container.SimpleStompletContainer;
import org.projectodd.stilts.stomplet.server.StompletServer;

/**
 *
 * @author thomas.diesler@jboss.com
 * @since 07-Sep-2011
 */
public class StompletServerActivator implements BundleActivator {

    private static Logger log = Logger.getLogger(StompletServerActivator.class);

    private SimpleStompletContainer container;
    private StompletTracker tracker;
    private StompletServer server;

    @Override
    public void start(BundleContext context) throws Exception {
        log.infof("start: %s", context);

        server = new StompletServer();

        // Setup the {@link TransactionManager}
        ServiceReference sref = context.getServiceReference(TransactionManager.class.getName());
        if (sref != null) {
            TransactionManager transactionManager = (TransactionManager) context.getService(sref);
            log.infof("adding transaction manager: %s", transactionManager);
            server.setTransactionManager(transactionManager);
        }

        // Start the stomplet server
        container = new SimpleStompletContainer();
        server.setDefaultSessionManager(new SimpleStompSessionManager());
        server.setDefaultContainer(container);
        server.start();

        // Start tracking {@link Stomplet} services
        tracker = new StompletTracker(context);
        tracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.infof("stop: %s", context);
        if (tracker != null) {
            tracker.close();
        }
        if (server != null) {
            server.stop();
        }
    }

    private class StompletTracker extends ServiceTracker {

        StompletTracker(BundleContext context) {
            super(context, Stomplet.class.getName(), null);
        }

        @Override
        public Object addingService(ServiceReference reference) {
            Stomplet stomplet = (Stomplet) super.addingService(reference);
            log.infof("adding: %s", stomplet);
            try {
                // Copy string properties
                Map<String, String> props = new HashMap<String, String>();
                for (String key : reference.getPropertyKeys()) {
                    Object value = reference.getProperty(key);
                    if (value instanceof String) {
                        props.put(key, (String) value);
                    }
                }
                String destinationPattern = props.get("destinationPattern");
                log.infof("adding: %s -> %s", destinationPattern, stomplet);
                container.addStomplet(destinationPattern, stomplet, props);
            } catch (StompException ex) {
                log.errorf(ex, "Cannot add stomplet: %s", stomplet);
            }
            return stomplet;
        }

        @Override
        public void removedService(ServiceReference reference, Object service) {
            super.removedService(reference, service);
            Stomplet stomplet = (Stomplet) service;
            try {
                String destinationPattern = (String) reference.getProperty("destinationPattern");
                log.infof("removing: %s -> %s", destinationPattern, stomplet);
                container.removeStomplet(destinationPattern);
            } catch (StompException ex) {
                log.errorf(ex, "Cannot remove stomplet: %s", stomplet);
            }
        }
    }
}
