package org.projectodd.stilts.stomp.client.protocol.websockets;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

public class WebSocketHttpResponseDecoder extends ReplayingDecoder<VoidEnum> {

    private final Pattern statusLineRegexp = Pattern.compile( "^HTTP/([0-9]+\\.[0-9]+) +([0-9][0-9][0-9]) +(.*)$" );

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, VoidEnum state) throws Exception {
        int nonNewlineBytes = buffer.bytesBefore( (byte) '\n' );

        ChannelBuffer firstLine = buffer.readBytes( nonNewlineBytes + 1 );
        String line = firstLine.toString( Charset.forName( "UTF-8" ) ).trim();

        Matcher matcher = statusLineRegexp.matcher( line );

        if (matcher.matches()) {
            int status = Integer.parseInt( matcher.group( 2 ) );

            HttpResponse response = new DefaultHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf( status ) );

            if (status == 101) {
                response = readHeaders( response, buffer );

                if (response == null) {
                    return null;
                }

                readChallengeSolution( response, buffer );
                return response;
            } else if (status == 407) {

            }
        }

        return null;
    }

    protected HttpResponse readHeaders(HttpResponse response, ChannelBuffer buffer) {
        int nonNewlineBytes = buffer.bytesBefore( (byte) '\n' );
        while (nonNewlineBytes > 0) {
            ChannelBuffer lineBuffer = buffer.readBytes( nonNewlineBytes + 1 );
            String line = lineBuffer.toString( Charset.forName( "UTF-8" ) ).trim();

            if (line.length() == 0) {
                return response;
            }

            int colonLoc = line.indexOf( ':' );

            if (colonLoc >= 0) {
                String name = line.substring( 0, colonLoc ).trim().toLowerCase();
                String value = line.substring( colonLoc + 1 ).trim();

                response.addHeader( name, value );
            }

            nonNewlineBytes = buffer.bytesBefore( (byte) '\n' );
        }

        return null;
    }

    protected void readChallengeSolution(HttpResponse response, ChannelBuffer buffer) {
        byte[] solution = new byte[16];

        buffer.readBytes( solution );

        response.setContent( ChannelBuffers.copiedBuffer( solution ) );
    }

}
