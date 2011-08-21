package org.projectodd.stilts.stomp.protocol.websocket.ietf07;

import java.util.Arrays;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.projectodd.stilts.stomp.protocol.DebugHandler;

public class Ietf07WebSocketFrameDecoder extends ReplayingDecoder<VoidEnum> {

    public static final int DEFAULT_MAX_FRAME_SIZE = 16384;

    private final int maxFrameSize;

    public Ietf07WebSocketFrameDecoder() {
        this( DEFAULT_MAX_FRAME_SIZE );
    }

    /**
     * Creates a new instance of {@code WebSocketFrameDecoder} with the
     * specified {@code maxFrameSize}. If the client
     * sends a frame size larger than {@code maxFrameSize}, the channel will be
     * closed.
     * 
     * @param maxFrameSize the maximum frame size to decode
     */
    public Ietf07WebSocketFrameDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, VoidEnum state) throws Exception {

        log.info( "READABLE! " + actualReadableBytes() );

        byte finOpcode = buffer.readByte();

        boolean fin = ((finOpcode & 0x1) != 0);
        int opcode = (finOpcode >> 4);

        byte lengthMask = buffer.readByte();

        boolean masked = ((lengthMask & 0x80) != 0);
        
        log.info( "masked=" + masked );
        long length = ( lengthMask & 0x7F ); 
        log.info( "length.1=" + length );

        if (length == 126) {
            length = buffer.readShort();
        } else if (length == 127) {
            length = buffer.readLong();
        }

        log.info( "length.2=" + length );

        if (length > this.maxFrameSize) {
            throw new TooLongFrameException();
        }

        log.info( "HI -AA" );

        byte[] mask = null;
        log.info( "HI -BB" );

        if (masked) {
            mask = new byte[4];
            buffer.readBytes( mask );
        }

        log.info( "HI -CC " + actualReadableBytes() );

        byte[] payload = new byte[(int) length];

        log.info( "reading payload" );
        buffer.readBytes( payload );
        log.info( "read payload" );

        if (masked) {
            for (int i = 0; i < payload.length; ++i) {
                payload[i] = (byte) (payload[i] ^ mask[i % 4]);
            }
        }

        log.info( "Payload unmasked: " + new String( payload ) );

        ChannelBuffer data = ChannelBuffers.wrappedBuffer( payload );
        return new Ietf07WebSocketFrame( opcode, data );

    }

    private static final Logger log = Logger.getLogger( Ietf07WebSocketFrameDecoder.class );

}
