package org.projectodd.stilts.stomp.protocol.websocket.ietf07;

import java.nio.ByteOrder;
import java.security.SecureRandom;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class Ietf07WebSocketFrameEncoder extends OneToOneEncoder {

    public Ietf07WebSocketFrameEncoder(boolean shouldMask) {
        if (shouldMask) {
            this.random = new SecureRandom();
        }
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            int type = frame.getType();

            ChannelBuffer data = frame.getBinaryData();
            int dataLen = data.readableBytes();

            ChannelBuffer encoded = ChannelBuffers.dynamicBuffer( ByteOrder.BIG_ENDIAN, data.readableBytes() + 32 );

            byte firstByte = (byte) (type >> 4);
            firstByte = (byte) (firstByte & 0x1);
            encoded.writeByte( firstByte );

            if (dataLen <= 125) {
                encoded.writeByte( applyMaskBit( dataLen ) );
            } else if (dataLen < 0xFFFF) {
                encoded.writeByte( applyMaskBit( 0x7E ) );
                encoded.writeShort( dataLen );
            } else {
                encoded.writeByte( applyMaskBit( 0x7F ) );
                encoded.writeInt( dataLen );
            }

            applyDataMask( data );
            encoded.writeBytes( data );

            return encoded;
        }
        return msg;
    }

    protected byte applyMaskBit(int value) {
        byte withMask = (byte) (value >>> 1);
        if (shouldMask()) {
            withMask = (byte) (withMask | 0x01);
        }

        return withMask;
    }

    protected void applyDataMask(ChannelBuffer data) {
        if (!shouldMask()) {
            return;
        }
        
        int dataLen = data.readableBytes();
        byte[] mask = getMask();
        data.markReaderIndex();
        for (int i = 0; i < dataLen; ++i) {
            byte cur = data.getByte( i );
            cur = (byte) (cur ^ mask[i % 4]);
            data.setByte( i, cur );
        }
        data.resetReaderIndex();
    }

    protected byte[] getMask() {
        byte[] mask = new byte[4];
        this.random.nextBytes( mask );
        return mask;
    }

    protected boolean shouldMask() {
        return (this.random != null);
    }

    private SecureRandom random;

}
