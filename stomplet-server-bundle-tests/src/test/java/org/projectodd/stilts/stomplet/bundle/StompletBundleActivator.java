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


import static org.projectodd.stilts.stomplet.bundle.SimpleTestStomplet.DESTINATION_QUEUE_ONE;

import java.util.Dictionary;
import java.util.Hashtable;

import org.jboss.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.projectodd.stilts.stomplet.Stomplet;


/**
 * 
 * @author thomas.diesler@jboss.com
 * @since 07-Sep-2011
 */
public class StompletBundleActivator implements BundleActivator {

    static Logger log = Logger.getLogger(StompletBundleActivator.class);
    
    private ServiceRegistration registration;
    
    @Override
    public void start(BundleContext context) throws Exception {
        log.infof("start: %s", context);
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put("destinationPattern", DESTINATION_QUEUE_ONE);
        registration = context.registerService(Stomplet.class.getName(), new SimpleTestStomplet(), props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.infof("stop: %s", context);
        if (registration != null)
            registration.unregister();
    }
}
