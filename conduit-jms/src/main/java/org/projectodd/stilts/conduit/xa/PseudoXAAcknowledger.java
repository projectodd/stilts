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

package org.projectodd.stilts.conduit.xa;

import org.projectodd.stilts.stomp.spi.Acknowledger;

public class PseudoXAAcknowledger implements Acknowledger {

    public PseudoXAAcknowledger(PseudoXAResourceManager resourceManager, Acknowledger acknowledger) {
        this.resourceManager = resourceManager;
        this.acknowledger = acknowledger;
    }

    @Override
    public void ack() throws Exception {
        PseudoXATransaction tx = this.resourceManager.currentTransaction();
        if (tx != null) {
            tx.addAck( this.acknowledger );
        } else {
            this.acknowledger.ack();
        }
    }

    @Override
    public void nack() throws Exception {
        PseudoXATransaction tx = this.resourceManager.currentTransaction();
        if (tx != null) {
            tx.addNack(  this.acknowledger );
        } else {
            this.acknowledger.nack();
        }
    }
    
    private PseudoXAResourceManager resourceManager;
    private Acknowledger acknowledger;


}
