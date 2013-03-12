package org.projectodd.stilts.stomp.server.protocol.resource;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class ClasspathResourceManager implements ResourceManager {

    @Override
    public ChannelBuffer getResource(String uri) {
        log.debug(  "REQUESTED: " + uri  );
        InputStream stream = getClass().getClassLoader().getResourceAsStream( uri );
        if ( stream != null ) {
            try {
                ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
                byte[] bytes = new byte[4096];
                int numRead = -1;
                while ( ( numRead = stream.read( bytes ) ) >= 0 ) {
                    buffer.writeBytes( bytes, 0, numRead  );
                }
                return buffer;
            } catch (IOException e) {
                log.error( e );
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error( e );
                }
            }
        }
        return null;
    }
    
    private static Logger log = Logger.getLogger( ClasspathResourceManager.class );

}
