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

package org.projectodd.stilts.stomp.client.protocol;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrames;

public abstract class AbstractClientHandler extends
		SimpleChannelUpstreamHandler {

	private static Logger log = Logger.getLogger(AbstractClientHandler.class);

	public AbstractClientHandler(ClientContext clientContext) {
		this.clientContext = clientContext;
	}

	public ClientContext getClientContext() {
		return this.clientContext;
	}

	protected ChannelFuture sendFrame(ChannelHandlerContext channelContext,
			StompFrame frame) {
		return channelContext.getChannel().write(frame);
	}

	protected ChannelFuture sendError(ChannelHandlerContext channelContext,
			String message) {
		return sendFrame(channelContext,
				StompFrames.newErrorFrame(message, null));
	}

	protected void sendErrorAndClose(ChannelHandlerContext channelContext,
			String message) {
		ChannelFuture future = sendError(channelContext, message);
		future.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		log.error("An error occurred", e.getCause());
	}

	private ClientContext clientContext;

}
