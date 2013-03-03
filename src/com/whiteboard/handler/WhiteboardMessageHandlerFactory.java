package com.whiteboard.handler;

import android.graphics.Canvas;
import com.digitalxyncing.communication.Endpoint;
import com.digitalxyncing.communication.MessageHandler;
import com.digitalxyncing.communication.MessageHandlerFactory;
import com.whiteboard.model.WhiteboardDocument;

public class WhiteboardMessageHandlerFactory implements MessageHandlerFactory<Canvas> {

    private WhiteboardDocument mWhiteboardDocument;

    public WhiteboardMessageHandlerFactory(WhiteboardDocument whiteboardDocument) {
        mWhiteboardDocument = whiteboardDocument;
    }

    @Override
    public MessageHandler build(Endpoint<Canvas> canvasEndpoint, byte[] bytes) {
        return new WhiteboardMessageHandler(mWhiteboardDocument, bytes);
    }

}
