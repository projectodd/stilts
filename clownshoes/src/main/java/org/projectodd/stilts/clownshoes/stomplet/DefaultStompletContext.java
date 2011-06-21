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

package org.projectodd.stilts.clownshoes.stomplet;

import java.util.HashMap;
import java.util.Map;

import org.projectodd.stilts.stomplet.MessageRouter;
import org.projectodd.stilts.stomplet.StompletContext;

public class DefaultStompletContext implements StompletContext {

    public DefaultStompletContext(MessageRouter messageRouter) {
        this.messageRouter = messageRouter;
    }
    @Override
    public String[] getAttributeNames() {
        return this.attributes.keySet().toArray( new String[this.attributes.size()] );
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(  name );
    }

    @Override
    public MessageRouter getMessageRouter() {
        return this.messageRouter;
    }

    private MessageRouter messageRouter;
    private Map<String, Object> attributes = new HashMap<String, Object>();

}
