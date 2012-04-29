package org.projectodd.stilts.stomp.protocol;

import java.nio.charset.Charset;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class StompFrameCodec {

    public static final StompFrameCodec INSTANCE = new StompFrameCodec();

    public StompFrameCodec() {

    }

    // ------------------------------------------------------------------------
    // Decode
    // ------------------------------------------------------------------------

    public StompFrame decode(ChannelBuffer buffer) throws Exception {
        FrameHeader header = decodeHeader( buffer );

        if (header == null) {
            return null;
        }

        int len = header.getContentLength();

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

        return frame;
    }

    protected ChannelBuffer readUntilNull(ChannelBuffer buffer) {
        int nonNullBytes = buffer.bytesBefore( (byte) 0x00 );

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

            if (nonNewLineBytes < 0) {
                break;
            }
            if (nonNewLineBytes == 0) {
                buffer.readByte();
                break;
            }
            if (nonNewLineBytes >= 0) {
                ChannelBuffer line = buffer.readBytes( nonNewLineBytes );
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
                bytes.append( "[" + buffer.getByte( buffer.readerIndex() + i )
                        + "] " );
            }
            log.debug( "* '" + name + "' bytes: " + bytes + " ## " );
        }
    }

    protected FrameHeader processHeaderLine(FrameHeader header, String line) {
        if (header == null) {
            header = new FrameHeader();
            Command command = Command.valueOf( line );
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

    // ------------------------------------------------------------------------
    // Encode
    // ------------------------------------------------------------------------

    private static final int HEADER_ESTIMATE = 1024;

    private static final byte[] HEADER_DELIM = ":".getBytes();
    private static final byte NEWLINE = (byte) '\n';
    private static final byte NULL = (byte) 0x00;

    public ChannelBuffer encode(StompFrame frame) throws Exception {
        ChannelBuffer buffer = newBuffer( frame );
        writeHeader( frame, buffer );
        writeContent( frame, buffer );
        return buffer;
    }

    protected ChannelBuffer newBuffer(StompFrame frame) {
        if (frame instanceof StompContentFrame) {
            return ChannelBuffers.dynamicBuffer( HEADER_ESTIMATE + ((StompContentFrame) frame).getContent().capacity() );
        }

        return ChannelBuffers.dynamicBuffer( HEADER_ESTIMATE );
    }

    protected void writeHeader(StompFrame frame, ChannelBuffer buffer) {
        buffer.writeBytes( frame.getCommand().getBytes() );
        buffer.writeByte( NEWLINE );
        Set<String> headerNames = frame.getHeaderNames();
        for (String name : headerNames) {
            if (name.equalsIgnoreCase( "content-length" )) {
                continue;
            }
            buffer.writeBytes( name.getBytes() );
            buffer.writeBytes( HEADER_DELIM );
            buffer.writeBytes( frame.getHeader( name ).getBytes() );
            buffer.writeByte( NEWLINE );
        }

        if (frame instanceof StompContentFrame) {
            int length = ((StompContentFrame) frame).getContent().readableBytes();
            buffer.writeBytes( Header.CONTENT_LENGTH.getBytes() );
            buffer.writeBytes( HEADER_DELIM );
            buffer.writeBytes( ("" + length).getBytes() );
            buffer.writeByte( NEWLINE );
        }

        buffer.writeByte( NEWLINE );
    }

    protected void writeContent(StompFrame frame, ChannelBuffer buffer) {
        if (frame instanceof StompContentFrame) {
            ChannelBuffer content = ((StompContentFrame) frame).getContent();
            buffer.writeBytes( content );
        }
        buffer.writeByte( NULL );
        return;
    }

    private static final Charset UTF_8 = Charset.forName( "UTF-8" );
    private static Logger log = Logger.getLogger( StompFrameCodec.class );

}
