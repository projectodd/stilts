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

import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.spi.StompSession;

/**
 * Defines methods that all Stomplets must implement.
 * 
 * <p>
 * A Stomplet is a small Java class that performs two functions related to
 * asynchronous message processing:
 * </p
 * .
 * 
 * <ol>
 * <li>Handle requests from clients to <b>subscribe</b> and <b>unsubscribe</b>
 * from matching destinations</li>
 * <li>Receive messages sent to the destination from clients, and handle
 * dispatching outbound messages to subscribers.</li>
 * </ol>
 * 
 * <p>
 * A single Stomplet may be configured to services multiple destinations based
 * upon <b>routing</b> rules.
 * </p>
 * 
 * @see http://stilts.projectodd.org/stomplet/
 * 
 * @author Bob McWhirter
 */
public interface Stomplet {

    /**
     * Initialize the Stomplet in its container environment
     * 
     * @param config The configuration for the stomplet.
     * @throws StompException If an error occurs during initialization.
     */
    void initialize(StompletConfig config) throws StompException;

    /**
     * Clean up any resources allocated by the stomplet before removing
     * it from the container.
     * 
     * @throws StompException If an error occurs during destruction.
     */
    void destroy() throws StompException;

    /**
     * Handle a message sent to a destination matched by this stomplet.
     * 
     * <p>
     * In the event the stomplet is servicing multiple destinations, the
     * stomplet may need to inspect the {@link StompMessage} for details as to
     * how to handle the incoming message.
     * </p>
     * 
     * <p>
     * If named-segments are present in the applicable routing rule, each named
     * segment is added to the message's header values, prefixed with
     * <code>stomplet.</code>.
     * </p>
     * 
     * <p>
     * For instance, the following routing rule:
     * </p>
     * 
     * <pre>
     * ROUTE /queues/:queue_name com.mycorp.MyStomplet
     * </pre>
     * 
     * <p>
     * Would result in all inbound messages with matching destinations to have a
     * header named <code>stomplet.queue_name</code> added to it, with the value
     * being the matching portion of the destination.
     * </p>
     * 
     * @param message The inbound message.
     * @param session The user session.
     * @throws StompException If an error occurs while processing the message.
     */
    void onMessage(StompMessage message, StompSession session) throws StompException;

    /**
     * Handle a subscription request.
     * 
     * <p>
     * For subscription requests matching destinations mapped to this
     * stomplet, the {@link #onSubscribe(Subscriber)} method will be invoked
     * with a {@link Subscriber} object.  If the stomplet chooses to 
     * allow the subscription, it may route further messages to the
     * <code>Subscriber</code> to satisfy the subscription.
     * </p>
     * 
     * <p>
     * If it chooses to deny the subscription request (for any reason),
     * throwing a <code>StompException</code> is appropriate.
     * </p>
     * 
     * @param subscriber The live subscriber making the request.
     * @throws StompException If an error occurs processing the subscription request.
     */
    void onSubscribe(Subscriber subscriber) throws StompException;

    /**
     * Handle the cancellation of a subscription.
     * 
     * <p>
     * When a client explicitly cancels a subscription, or disconnects,
     * the stomplet is notified through {@link #onUnsubscribe(Subscriber)},
     * having the same {@link Subscriber} instance passed to it.
     * </p>
     * 
     * @param subscriber The subscriber cancelling the subscription.
     * @throws StompException If an error occurs processing the subscription cancellation request.
     */
    void onUnsubscribe(Subscriber subscriber) throws StompException;

}
