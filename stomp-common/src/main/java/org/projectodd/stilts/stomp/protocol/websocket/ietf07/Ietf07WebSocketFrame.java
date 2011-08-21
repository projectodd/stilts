package org.projectodd.stilts.stomp.protocol.websocket.ietf07;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.jboss.netty.util.CharsetUtil;

public class Ietf07WebSocketFrame implements WebSocketFrame {

    private int type;
    private ChannelBuffer binaryData;

    /**
     * Creates a new empty text frame.
     */
    public Ietf07WebSocketFrame() {
        this(0, ChannelBuffers.EMPTY_BUFFER);
    }

    /**
     * Creates a new text frame from with the specified string.
     */
    public Ietf07WebSocketFrame(String textData) {
        this(0x1, ChannelBuffers.copiedBuffer(textData, CharsetUtil.UTF_8));
    }

    /**
     * Creates a new frame with the specified frame type and the specified data.
     *
     * @param type
     *        the type of the frame. {@code 0} is the only allowed type currently.
     * @param binaryData
     *        the content of the frame.  If <tt>(type &amp; 0x80 == 0)</tt>,
     *        it must be encoded in UTF-8.
     *
     * @throws IllegalArgumentException
     *         if If <tt>(type &amp; 0x80 == 0)</tt> and the data is not encoded
     *         in UTF-8
     */
    public Ietf07WebSocketFrame(int type, ChannelBuffer binaryData) {
        setData(type, binaryData);
    }

    public int getType() {
        return type;
    }

    public boolean isText() {
        return this.type == 0x1;
    }

    public boolean isBinary() {
        return this.type == 0x2;
    }

    public ChannelBuffer getBinaryData() {
        return binaryData;
    }

    public String getTextData() {
        return getBinaryData().toString(CharsetUtil.UTF_8);
    }

    public void setData(int type, ChannelBuffer binaryData) {
        if (binaryData == null) {
            throw new NullPointerException("binaryData");
        }

        if (type == 0x1 ) {
            // If text, data should not contain 0xFF.
            int delimPos = binaryData.indexOf( binaryData.readerIndex(), binaryData.writerIndex(), (byte) 0xFF);
            if (delimPos >= 0) {
                throw new IllegalArgumentException( "a text frame should not contain 0xFF.");
            }
        }

        this.type = type;
        this.binaryData = binaryData;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "(type: " + getType() + ", " + "data: " + getBinaryData() + ')';
    }

}
