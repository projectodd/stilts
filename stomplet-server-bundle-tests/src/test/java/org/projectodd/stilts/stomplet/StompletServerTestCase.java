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

package org.projectodd.stilts.stomplet;

import static org.junit.Assert.assertTrue;
import static org.projectodd.stilts.stomplet.bundle.SimpleTestStomplet.DESTINATION_QUEUE_ONE;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.osgi.testing.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.client.ClientSubscription;
import org.projectodd.stilts.stomp.client.MessageHandler;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.client.SubscriptionBuilder;
import org.projectodd.stilts.stomp.spi.StompSession;
import org.projectodd.stilts.stomplet.bundle.SimpleTestStomplet;
import org.projectodd.stilts.stomplet.bundle.StompletBundleActivator;
import org.projectodd.stilts.stomplet.helpers.AbstractStomplet;

/**
 * 
 * @author thomas.diesler@jboss.com
 * @since 07-Sep-2011
 */
@RunAsClient
@RunWith(Arquillian.class)
public class StompletServerTestCase {

    @Deployment(testable = false)
    public static Archive<?> deploy() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "simple-stomplet");
        archive.addClasses(StompletBundleActivator.class, SimpleTestStomplet.class);
        archive.setManifest(new Asset() {
            public InputStream openStream() {
                OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
                builder.addBundleSymbolicName(archive.getName());
                builder.addBundleManifestVersion(2);
                builder.addBundleActivator(StompletBundleActivator.class);
                builder.addImportPackages(StompMessage.class, StompSession.class, Stomplet.class, AbstractStomplet.class);
                builder.addImportPackages(BundleContext.class, Logger.class);
                return builder.openStream();
            }
        });
        return archive;
    }

    @Test
    public void testStompletRegistration() throws Exception {
        StompClient client = new StompClient("stomp://localhost");
        client.connect();

        final CountDownLatch latch = new CountDownLatch(3);
        SubscriptionBuilder builder = client.subscribe(DESTINATION_QUEUE_ONE);
        builder.withMessageHandler(new MessageHandler() {
            public void handle(StompMessage message) {
                latch.countDown();
            }
        });
        ClientSubscription subscription = builder.start();
        
        client.send(StompMessages.createStompMessage(DESTINATION_QUEUE_ONE, "start"));

        assertTrue("No latch timeout", latch.await(10, TimeUnit.SECONDS));

        client.send(StompMessages.createStompMessage(DESTINATION_QUEUE_ONE, "stop"));

        subscription.unsubscribe();
        client.disconnect();
    }
}
