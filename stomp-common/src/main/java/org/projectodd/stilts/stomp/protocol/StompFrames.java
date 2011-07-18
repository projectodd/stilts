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

package org.projectodd.stilts.stomp.protocol;

import org.jboss.netty.buffer.ChannelBuffers;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;

public class StompFrames {

    public static StompFrame newAckFrame(Headers headers) {
        StompControlFrame frame = new StompControlFrame( Command.ACK );
        frame.setHeader( Header.MESSAGE_ID, headers.get( Header.MESSAGE_ID ) );
        frame.setHeader( Header.SUBSCRIPTION, headers.get( Header.SUBSCRIPTION ) );
        String transactionId = headers.get( Header.TRANSACTION );
        if (transactionId != null) {
            frame.setHeader( Header.TRANSACTION, transactionId );
        }
        return frame;
    }

    public static StompFrame newNackFrame(Headers headers) {
        StompControlFrame frame = new StompControlFrame( Command.NACK );
        frame.setHeader( Header.MESSAGE_ID, headers.get( Header.MESSAGE_ID ) );
        frame.setHeader( Header.SUBSCRIPTION, headers.get( Header.SUBSCRIPTION ) );
        String transactionId = headers.get( Header.TRANSACTION );
        if (transactionId != null) {
            frame.setHeader( Header.TRANSACTION, transactionId );
        }
        return frame;
    }

    public static StompFrame newSendFrame(StompMessage message) {
        StompContentFrame frame = new StompContentFrame( Command.SEND, message.getHeaders() );
        frame.setContent( ChannelBuffers.copiedBuffer( message.getContent() ) );
        return frame;
    }

    public static StompFrame newConnectedFrame(String sessionId, Version version) {
        StompControlFrame frame = new StompControlFrame( Command.CONNECTED );
        frame.setHeader( Header.SESSION, sessionId );
        String implVersion = StompFrames.class.getPackage().getImplementationVersion();
        frame.setHeader( Header.SERVER, "Stilts/" + implVersion );
        if (version.isAfter( Version.VERSION_1_0 )) {
            frame.setHeader( Header.VERSION, version.versionString() );
        }
        return frame;
    }

    public static StompFrame newErrorFrame(String message, StompFrame inReplyTo) {
        StompContentFrame frame = new StompContentFrame( Command.ERROR );
        if (inReplyTo != null) {
            String receiptId = inReplyTo.getHeader( Header.RECEIPT );
            if (receiptId != null) {
                frame.setHeader( Header.RECEIPT_ID, receiptId );
            }
        }
        byte[] bytes = message.getBytes();
        frame.setContent( ChannelBuffers.copiedBuffer( bytes ) );
        frame.setHeader( Header.CONTENT_LENGTH, String.valueOf( bytes.length ) );
        frame.setHeader( Header.CONTENT_TYPE, "text/plain" );
        return frame;
    }

    public static StompFrame newReceiptFrame(String receiptId) {
        StompControlFrame receipt = new StompControlFrame( Command.RECEIPT );
        receipt.setHeader( Header.RECEIPT_ID, receiptId );
        return receipt;
    }

    // ----------------------------------------
    // ----------------------------------------

    public static StompControlFrame newBeginFrame(String transactionId) {
        StompControlFrame frame = new StompControlFrame( Command.BEGIN );
        frame.setHeader( Header.TRANSACTION, transactionId );
        return frame;
    }

    public static StompControlFrame newCommitFrame(String transactionId) {
        StompControlFrame frame = new StompControlFrame( Command.COMMIT );
        frame.setHeader( Header.TRANSACTION, transactionId );
        return frame;
    }

    public static StompControlFrame newAbortFrame(String transactionId) {
        StompControlFrame frame = new StompControlFrame( Command.ABORT );
        frame.setHeader( Header.TRANSACTION, transactionId );
        return frame;
    }

}
