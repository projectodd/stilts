package org.projectodd.stilts.stomp.client.js.websockets;

public class MessageEvent {
    
    public MessageEvent(Object data) {
        this.data = data;
    }
    
    public Object getData() {
        return this.data;
    }

    private Object data;

}
