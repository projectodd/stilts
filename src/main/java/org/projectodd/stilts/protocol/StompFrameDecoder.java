package org.projectodd.stilts.protocol;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.protocol.StompFrame.Command;

public class StompFrameDecoder extends ReplayingDecoder<VoidEnum> {

    private static final Charset UTF_8 = Charset.forName( "UTF-8" );
    private Logger log;

    public StompFrameDecoder(Logger log) {
        this.log = log;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, VoidEnum state) throws Exception {
        log.trace( "decode: " + buffer + " // " + buffer.readableBytes() );
        FrameHeader header = decodeHeader( buffer );

        if (header == null) {
            return null;
        }

        int len = header.getContentLength();

        log.trace( "content-length: " + len );

        ChannelBuffer content = null;

        if (len <= 0) {
            content = readUntilNull( buffer );
        } else {
            content = readUntil( buffer, len );
        }

        StompFrame frame = null;

        if (content != null) {
            if (header.isContentFrame()) {
                frame = new StompContentFrame( header, content );
            } else {
                frame = new StompControlFrame( header );
            }
        }

        log.trace( "decoded to frame: " + frame );
        
        return frame;
    }

    protected ChannelBuffer readUntilNull(ChannelBuffer buffer) {
        int nonNullBytes = buffer.bytesBefore( (byte) 0x00 );
        log.trace( "reading until null: " + nonNullBytes );

        ChannelBuffer content = null;
        if (nonNullBytes == 0) {
            content = ChannelBuffers.EMPTY_BUFFER;
        } else {
            content = buffer.readBytes( nonNullBytes );
        }

        buffer.readByte();
        return content;
    }

    protected ChannelBuffer readUntil(ChannelBuffer buffer, int len) {
        if (buffer.readableBytes() < (len + 1)) {
            return null;
        }

        ChannelBuffer content = buffer.readBytes( len );
        buffer.readByte();
        return content;
    }

    protected FrameHeader decodeHeader(ChannelBuffer buffer) {
        FrameHeader header = null;

        while (buffer.readable()) {
            int nonNewLineBytes = buffer.bytesBefore( (byte) '\n' );

            if (nonNewLineBytes == 0) {
                buffer.readByte();
                break;
            }
            if (nonNewLineBytes >= 0) {
                ChannelBuffer line = buffer.readBytes( nonNewLineBytes );
                logBytes( "line", line );
                buffer.readByte();
                header = processHeaderLine( header, line.toString( UTF_8 ) );
            }
        }

        return header;
    }

    protected void logBytes(String name, Object o) {
        StringBuilder bytes = new StringBuilder();
        if (o instanceof ChannelBuffer) {
            ChannelBuffer buffer = (ChannelBuffer) o;
            int readable = buffer.readableBytes();
            for (int i = 0; i < readable; ++i) {
                bytes.append( "[" + buffer.getByte( buffer.readerIndex() + i ) + "] " );
            }
            log.debug( "* '" + name + "' bytes: " + bytes + " ## " );
        }
    }

    protected FrameHeader processHeaderLine(FrameHeader header, String line) {
        log.trace( "line = " + line );
        if (header == null) {
            header = new FrameHeader();
            Command command = Command.valueOf( line );
            log.trace( "command =" + command );
            header.setCommand( command );
            return header;
        }

        int colonLoc = line.indexOf( ":" );
        if (colonLoc > 0) {
            String name = line.substring( 0, colonLoc );
            String value = line.substring( colonLoc + 1 );
            header.set( name, value );
        }

        return header;
    }

}
