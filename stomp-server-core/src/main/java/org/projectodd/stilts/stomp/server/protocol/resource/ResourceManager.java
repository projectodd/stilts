package org.projectodd.stilts.stomp.server.protocol.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class ResourceManager {

    private static final Set<String> KNOWN_RESOURCES = new HashSet<String>() {
        {
            add( "/stomp.js" );
            add( "/WebSocketMain.swf" );
        }
    };

    public ChannelBuffer getResource(String uri) {
        if (KNOWN_RESOURCES.contains( uri )) {
            InputStream in = getClass().getClassLoader().getResourceAsStream( uri );

            if (in != null) {
                byte[] bytes = new byte[4096];

                int numRead = 0;

                ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

                try {
                    while ((numRead = in.read( bytes )) >= 0) {
                        buffer.writeBytes( bytes, 0, numRead );
                    }
                } catch (IOException e) {
                    return null;
                }
                return buffer;
            }
        }
        return null;
    }
    private static Logger log = Logger.getLogger( ResourceManager.class );

}
