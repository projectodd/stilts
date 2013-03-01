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

import java.net.InetAddress;
import java.net.InetSocketAddress;
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
import org.projectodd.stilts.stomp.server.InsecureConnector;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.container.SimpleStompletContainer;
import org.projectodd.stilts.stomplet.server.StompletServer;

/**
 * An activator that starts the StompletServer
 *
 * The binding address can be configured by a binding spec
 * which is defined by
 *
 *  address[:]port
 *
 * e.g.
 *
 *  127.0.0.1:8675
 *
 * The activator first looks for the binding spec at a framework property
 *
 *      org.projectodd.stilts.stomplet.server
 *
 * if not found, it looks for a service of type InetSocketAddress
 * with a property socketBinding=stilts
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

        // Get the binding spec from the framework property
        InetSocketAddress socketAddress = null;
        String bindingspec = (String) context.getProperty(StompletServer.class.getPackage().getName());
        if (bindingspec != null) {
            String[] parts = bindingspec.split(":");
            InetAddress address = InetAddress.getByName(parts[0]);
            int port = Integer.parseInt(parts[1]);
            socketAddress = new InetSocketAddress(address, port);
        }

        // Get the InetSocketAddress as a service
        if (socketAddress == null) {
            String filter = "(socketBinding=stilts)";
            ServiceReference[] srefs = context.getServiceReferences(InetSocketAddress.class.getName(), filter);
            if (srefs != null && srefs.length == 1) {
                socketAddress = (InetSocketAddress) context.getService(srefs[0]);
            }
        }

        // Use the binding spec to construct the server
        if (socketAddress != null) {
            log.infof("create server using: %s", socketAddress);
            server = new StompletServer();
            server.addConnector( new InsecureConnector( socketAddress ));
        }

        // Fall back to the default server binding
        if (server == null) {
            server = new StompletServer();
        }

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
