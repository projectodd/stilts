package org.projectodd.stilts.stomp.server.protocol.resource;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class SimpleResourceManager implements ResourceManager {

    public void putResource(String uri, String content) {
        this.resources.put( uri, content );
    }
    
    @Override
    public ChannelBuffer getResource(String uri) {
        String content = this.resources.get( uri );
        if ( content == null ) {
            return null;
        }
        
        return ChannelBuffers.wrappedBuffer( content.getBytes() );
    }
    
    private Map<String,String> resources = new HashMap<String,String>();

}
