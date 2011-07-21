package org.projectodd.stilts.stomp.client.js.websockets;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;

public class WebSocketClientConnectionWaiter implements ChannelUpstreamHandler {
    
    private static final Callable<Void> NO_OP = new Callable<Void>() {
        public Void call() throws Exception {
            return null;
        } };
        
    private FutureTask<Void> futureTask;

    public WebSocketClientConnectionWaiter() {
        this.futureTask = new FutureTask<Void>( NO_OP );
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        System.err.println( "UPSTREAM: "+ e );
        if ( e instanceof ChannelStateEvent ) {
            ChannelStateEvent event = (ChannelStateEvent) e;
            if ( event.getState() == ChannelState.CONNECTED ) {
                ctx.getPipeline().remove( this );
                this.futureTask.run();
            }
        } else if ( e instanceof ExceptionEvent ) {
            ExceptionEvent event = (ExceptionEvent) e;
            event.getCause().printStackTrace();
        }
    }
    
    public void await(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        this.futureTask.get( timeout, TimeUnit.MILLISECONDS );
    }
    
    public void await() throws InterruptedException, ExecutionException {
        this.futureTask.get();
    }

}
