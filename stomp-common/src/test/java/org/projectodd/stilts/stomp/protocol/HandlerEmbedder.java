package org.projectodd.stilts.stomp.protocol;

import static org.jboss.netty.channel.Channels.fireChannelBound;
import static org.jboss.netty.channel.Channels.fireChannelClosed;
import static org.jboss.netty.channel.Channels.fireChannelConnected;
import static org.jboss.netty.channel.Channels.fireChannelDisconnected;
import static org.jboss.netty.channel.Channels.fireChannelOpen;
import static org.jboss.netty.channel.Channels.fireChannelUnbound;

import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Queue;

import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.embedder.CodecEmbedderException;

public class HandlerEmbedder {

    private final Channel channel;
    private final ChannelPipeline pipeline;
    private final EmbeddedChannelSink sink = new EmbeddedChannelSink();

    final Queue<Object> productQueue = new LinkedList<Object>();

    /**
     * Creates a new embedder whose pipeline is composed of the specified
     * handlers.
     */
    public HandlerEmbedder(ChannelHandler... handlers) {
        this( true, handlers );
    }

    /**
     * Creates a new embedder whose pipeline is composed of the specified
     * handlers.
     */
    public HandlerEmbedder(boolean fireInitialEvents, ChannelHandler... handlers) {
        pipeline = new EmbeddedChannelPipeline();
        configurePipeline( handlers );
        channel = new EmbeddedChannel( pipeline, sink );
        if (fireInitialEvents) {
            fireInitialEvents();
        }
    }

    /**
     * Creates a new embedder whose pipeline is composed of the specified
     * handlers.
     * 
     * @param bufferFactory the {@link ChannelBufferFactory} to be used when
     *            creating a new buffer.
     */
    public HandlerEmbedder(ChannelBufferFactory bufferFactory, ChannelHandler... handlers) {
        this( handlers );
        getChannel().getConfig().setBufferFactory( bufferFactory );
    }

    public void sendUpstream(Object message) {
        if (message instanceof ChannelEvent) {
            this.pipeline.sendUpstream( (ChannelEvent) message );
        } else {
            Channels.fireMessageReceived( this.channel, message );
        }
    }

    public void sendDownstream(Object message) {
        if (message instanceof ChannelEvent) {
            this.pipeline.sendDownstream( (ChannelEvent) message );
        } else {
            Channels.write( this.channel, message );
        }
    }

    private void fireInitialEvents() {
        // Fire the typical initial events.
        fireChannelOpen( channel );
        fireChannelBound( channel, channel.getLocalAddress() );
        fireChannelConnected( channel, channel.getRemoteAddress() );
    }

    private void configurePipeline(ChannelHandler... handlers) {
        if (handlers == null) {
            throw new NullPointerException( "handlers" );
        }

        if (handlers.length == 0) {
            throw new IllegalArgumentException(
                    "handlers should contain at least one " +
                            ChannelHandler.class.getSimpleName() + '.' );
        }

        for (int i = 0; i < handlers.length; i++) {
            ChannelHandler h = handlers[i];
            if (h == null) {
                throw new NullPointerException( "handlers[" + i + "]" );
            }
            pipeline.addLast( String.valueOf( i ), handlers[i] );
        }
        pipeline.addLast( "SINK", sink );
    }

    public boolean finish() {
        fireChannelDisconnected( channel );
        fireChannelUnbound( channel );
        fireChannelClosed( channel );
        return !productQueue.isEmpty();
    }

    /**
     * Returns the virtual {@link Channel} which will be used as a mock
     * during encoding and decoding.
     */
    public final Channel getChannel() {
        return channel;
    }

    public ChannelPipeline getPipeline() {
        return this.pipeline;
    }

    /**
     * Returns {@code true} if and only if the produce queue is empty and
     * therefore {@link #poll()} will return {@code null}.
     */
    protected final boolean isEmpty() {
        return productQueue.isEmpty();
    }

    public final Object poll() {
        return productQueue.poll();
    }

    public final Object peek() {
        return productQueue.peek();
    }

    public final Object[] pollAll() {
        final int size = size();
        Object[] a = new Object[size];
        for (int i = 0; i < size; i++) {
            Object product = poll();
            if (product == null) {
                throw new ConcurrentModificationException();
            }
            a[i] = product;
        }
        return a;
    }

    @SuppressWarnings("unchecked")
    public final <T> T[] pollAll(T[] a) {
        if (a == null) {
            throw new NullPointerException( "a" );
        }

        final int size = size();

        // Create a new array if the specified one is too small.
        if (a.length < size) {
            a = (T[]) Array.newInstance( a.getClass().getComponentType(), size );
        }

        for (int i = 0;; i++) {
            T product = (T) poll();
            if (product == null) {
                break;
            }
            a[i] = product;
        }

        // Put the terminator if necessary.
        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    public final int size() {
        return productQueue.size();
    }

    private final class EmbeddedChannelSink implements ChannelSink, ChannelUpstreamHandler {
        EmbeddedChannelSink() {
            super();
        }

        public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) {
            handleEvent( e );
        }

        public void eventSunk(ChannelPipeline pipeline, ChannelEvent e) {
            handleEvent( e );
        }

        private void handleEvent(ChannelEvent e) {
            if (e instanceof ExceptionEvent) {
                throw new CodecEmbedderException( ((ExceptionEvent) e).getCause() );
            } else if (e instanceof MessageEvent) {
                boolean offered = productQueue.offer( ((MessageEvent) e).getMessage() );
                assert offered;
            } else {
                boolean offered = productQueue.offer( e );
                assert offered;
            }

            // Swallow otherwise.
        }

        public void exceptionCaught(
                ChannelPipeline pipeline, ChannelEvent e,
                ChannelPipelineException cause) throws Exception {
            Throwable actualCause = cause.getCause();
            if (actualCause == null) {
                actualCause = cause;
            }

            throw new CodecEmbedderException( actualCause );
        }

        public ChannelFuture execute(ChannelPipeline pipeline, Runnable task) {
            try {
                task.run();
                return Channels.succeededFuture(pipeline.getChannel());
            } catch (Throwable t) {
                return Channels.failedFuture(pipeline.getChannel(), t);
            }
        }
    }

    private static final class EmbeddedChannelPipeline extends DefaultChannelPipeline {

        EmbeddedChannelPipeline() {
            super();
        }

        @Override
        protected void notifyHandlerException(ChannelEvent e, Throwable t) {
            while (t instanceof ChannelPipelineException && t.getCause() != null) {
                t = t.getCause();
            }
            if (t instanceof CodecEmbedderException) {
                throw (CodecEmbedderException) t;
            } else {
                throw new CodecEmbedderException( t );
            }
        }
    }

}
