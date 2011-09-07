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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.spi.StompSession;
import org.projectodd.stilts.stomplet.Subscriber;
import org.projectodd.stilts.stomplet.helpers.AbstractStomplet;

public class SimpleTestStomplet extends AbstractStomplet {

    public static final String DESTINATION_QUEUE_ONE = "/queue/one";

    static Logger log = Logger.getLogger(SimpleTestStomplet.class);

    private ExecutorService executor = Executors.newCachedThreadPool();
    private Set<Subscriber> subscribers = new HashSet<Subscriber>();

    @Override
    public void onMessage(StompMessage message, StompSession session) throws StompException {
        log.infof("onMessage: %s", message);
        String content = message.getContentAsString();
        if ("start".equals(content)) {
            final AtomicInteger count = new AtomicInteger();
            executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (!executor.isShutdown()) {
                        for (Subscriber subscriber : subscribers) {
                            try {
                                StompMessage countmsg = StompMessages.createStompMessage();
                                countmsg.setContentAsString("count" + count.getAndIncrement());
                                log.infof("send: %s -> %s", countmsg, subscriber);
                                subscriber.send(countmsg);
                            } catch (StompException ex) {
                                log.errorf(ex, "Cannot send message to: %s", subscriber);
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            });
        } else if ("stop".equals(content)) {
            executor.shutdownNow();
        }
    }

    @Override
    public void onSubscribe(Subscriber subscriber) throws StompException {
        log.infof("onSubscribe: %s", subscriber);
        subscribers.add(subscriber);
    }

    @Override
    public void onUnsubscribe(Subscriber subscriber) throws StompException {
        log.infof("onUnsubscribe: %s", subscriber);
        subscribers.remove(subscriber);
    }

}