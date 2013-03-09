package com.whiteboard.handler;

import com.digitalxyncing.communication.Endpoint;
import com.digitalxyncing.communication.MessageHandler;
import com.digitalxyncing.communication.MessageHandlerFactory;
import com.whiteboard.model.Whiteboard;
import com.whiteboard.model.WhiteboardDocument;

public class WhiteboardMessageHandlerFactory implements MessageHandlerFactory<Whiteboard> {

    private WhiteboardDocument mWhiteboardDocument;

    public WhiteboardMessageHandlerFactory(WhiteboardDocument whiteboardDocument) {
        mWhiteboardDocument = whiteboardDocument;
    }

    @Override
    public MessageHandler build(Endpoint<Whiteboard> endpoint, byte[] bytes) {
        return new WhiteboardMessageHandler(endpoint, mWhiteboardDocument, bytes);
    }

}
