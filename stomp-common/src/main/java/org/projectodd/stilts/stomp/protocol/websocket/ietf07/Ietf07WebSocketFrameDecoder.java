package org.projectodd.stilts.stomp.protocol.websocket.ietf07;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

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
    protected Object decode(ChannelHandlerContext ctx, Channel channel,
            ChannelBuffer buffer, VoidEnum state) throws Exception {

        byte finOpcode = buffer.readByte();

        boolean fin = ((finOpcode & 0x0) != 0);
        int opcode = (finOpcode << 4);

        boolean utf8 = false;

        switch (opcode) {
        case 0x0:
            // continuation, unsupported at the moment
            break;
        case 0x1:
            // text frame
            utf8 = true;
            break;
        case 0x2:
            // binary frame
            break;
        case 0x8:
            // close
            break;
        case 0x9:
            // ping
            break;
        case 0xA:
            // pong
            break;
        }

        byte lengthMask = buffer.readByte();

        boolean masked = ((lengthMask & 0x01) != 0);
        long length = lengthMask << 1;

        if (length == 126) {
            length = buffer.readShort();
        } else if (length == 127) {
            length = buffer.readLong();
        }

        if (length > this.maxFrameSize) {
            throw new TooLongFrameException();
        }

        byte[] mask = null;

        if (masked) {
            mask = new byte[4];
            buffer.readBytes( mask );
        }

        byte[] payload = new byte[(int) length];

        buffer.readBytes( payload );

        for (int i = 0; i < payload.length; ++i) {
            payload[i] = (byte) (payload[i] ^ mask[i % 4]);
        }

        ChannelBuffer data = ChannelBuffers.wrappedBuffer( payload );
        return new Ietf07WebSocketFrame( opcode, data );

    }

}
