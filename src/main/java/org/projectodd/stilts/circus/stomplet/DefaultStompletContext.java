package org.projectodd.stilts.circus.stomplet;

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
