package org.projectodd.stilts.stomp.server.protocol;

import org.jboss.logging.Logger;
import org.projectodd.stilts.stomp.Heartbeat;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.spi.StompConnection;

public class HeartbeatRunnable implements Runnable {

    private static final double TOLERANCE_PERCENTAGE = 0.05;
    
    private static final Logger log = Logger.getLogger( HeartbeatRunnable.class );

    public HeartbeatRunnable(Heartbeat hb, StompConnection connection) {
        this.heartbeat = hb;
        this.duration = heartbeat.calculateDuration( hb.getServerSend(), hb.getClientReceive() );
        this.connection = connection;
    }

    @Override
    public void run() {
        long diff = System.currentTimeMillis() - heartbeat.getLastUpdate();
        double tolerance = duration * TOLERANCE_PERCENTAGE;
        log.debug( "HEARTBEAT : " + diff + " / " + duration );

        if (diff > duration - tolerance) {
            StompMessage msg = StompMessages.createStompMessage();
            try {
                connection.send( msg, null );
                heartbeat.touch();
            } catch (StompException e) {
                log.error( "Could not send heartbeat message:", e );
            }
        }
    }

    private Heartbeat heartbeat;
    private int duration;
    private StompConnection connection;

}
