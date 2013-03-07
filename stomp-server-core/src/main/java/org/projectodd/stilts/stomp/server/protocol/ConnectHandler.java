/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomp.server.protocol;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.Heartbeat;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.server.protocol.websockets.SessionDecodedEvent;
import org.projectodd.stilts.stomp.spi.StompConnection;
import org.projectodd.stilts.stomp.spi.StompProvider;
import org.projectodd.stilts.stomp.spi.TransactionalAcknowledgeableMessageSink;

public class ConnectHandler extends AbstractControlFrameHandler {

    public ConnectHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.CONNECT );
        setRequiresClientIdentification( false );
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof HostDecodedEvent) {
            this.host = ((HostDecodedEvent) e).getHost();
        } else if (e instanceof SessionDecodedEvent) {
            this.sessionId = ((SessionDecodedEvent) e).getSessionId();
        } else {
            super.handleUpstream( ctx, e );
        }
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {
            Version version = checkVersion( frame );
            Heartbeat hb = checkHeartbeat( frame, version );
            Headers headers = frame.getHeaders();
            String hostHeader = headers.get( Header.HOST );

            if (hostHeader == null) {
                if (this.host != null) {
                    headers.put( Header.HOST, this.host );
                }
            } else {
                if (this.host != null) {
                    if (!(hostHeader).equals( this.host )) {
                        throw new HostMismatchException( this.host, hostHeader );
                    }
                }
            }

            headers.remove( Header.SESSION );

            if (this.sessionId != null) {
                headers.put( Header.SESSION, this.sessionId );
            }

            checkHost( headers, version );
            StompConnection stompConnection = getStompProvider().createConnection( createMessageSink( channelContext ), headers, version, hb );
            log.debugf(  "setting stomp connection", stompConnection );
            if (stompConnection != null) {
                getContext().setStompConnection( stompConnection );
                StompFrame connected = StompFrames.newConnectedFrame( stompConnection.getSession().getId(), version );
                if (hb != null) {
                    connected.setHeader( Header.HEARTBEAT, hb.getServerSend() + "," + hb.getServerReceive() );
                }
                log.debugf( "connected: %s", frame );
                sendFrame( channelContext, connected );
            } else {
                log.error( "unable to create STOMP connection" );
                sendErrorAndClose( channelContext, "unable to open STOMP connection", frame );
            }
        } catch (StompException e) {
            log.error( "Error connecting", e );
            sendErrorAndClose( channelContext, e.getMessage(), frame );
        }
    }
    
    protected TransactionalAcknowledgeableMessageSink createMessageSink(ChannelHandlerContext ctx) {
        return new ChannelMessageSink( ctx.getChannel(), getContext().getAckManager() );
    }

    private Heartbeat checkHeartbeat(StompFrame frame, Version version) throws StompException {
        Heartbeat hb = null;
        String heartBeat = frame.getHeader( Header.HEARTBEAT );
        if (StringUtils.isNotBlank( heartBeat ) && !version.isBefore( Version.VERSION_1_1 )) {
            if (!HEART_BEAT_PATTERN.matcher( heartBeat ).matches()) {
                throw new StompException( "Heartbeat must be specified in msec as two comma-separated values." );
            }
            String[] components = heartBeat.split( "," );
            try {
                hb = new Heartbeat();
                hb.setClientReceive( Integer.parseInt( components[0] ) );
                hb.setClientSend( Integer.parseInt( components[1] ) );
            } catch (Exception ex) {
                throw new StompException( "Heartbeat values must be integers." );
            }
        }
        return hb;
    }

    private String checkHost(Headers headers, Version version) throws StompException {
        String host = headers.get( Header.HOST );
        if (StringUtils.isBlank( host ) && version.isAfter( Version.VERSION_1_0 )) {
            throw new StompException( "Must specify host in STOMP protocol 1.1 and above." );
        }
        return host;
    }

    private Version checkVersion(StompFrame frame) throws StompException {
        String acceptVersion = frame.getHeader( Header.ACCEPT_VERSION );
        if (acceptVersion == null) {
            return Version.VERSION_1_0;
        } else if (!VERSION_PATTERN.matcher( acceptVersion ).matches()) {
            throw new StompException( "Accept-version header value must be an incrementing comma-separated list." );
        }
        String[] versions = acceptVersion.split( "," );
        Version selectedVersion = null;
        for (int i = versions.length - 1; i >= 0; i--) {
            if ((selectedVersion = Version.forVersionString( versions[i] )) != null)
                break;
        }
        if (selectedVersion == null) {
            // no matching version found - send error frame
            throw new StompException( "Supported protocol versions are " + Version.supportedVersions() );
        }
        return selectedVersion;

    }

    private static Logger log = Logger.getLogger( ConnectHandler.class );

    private static final Pattern HEART_BEAT_PATTERN = Pattern.compile( "^\\d+,\\d+$" );
    private static final Pattern VERSION_PATTERN = Pattern.compile( "^([^\\s]+,)*[^\\s]*$" );

    private String host;
    private String sessionId;
}
