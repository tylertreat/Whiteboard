package com.whiteboard.handler;

import com.clarionmedia.infinitum.logging.Logger;
import com.digitalxyncing.communication.MessageHandler;
import com.digitalxyncing.document.Message.MessageType;
import com.whiteboard.model.WhiteboardDocument;
import com.whiteboard.model.WhiteboardDocumentFragment;

public class WhiteboardMessageHandler extends MessageHandler {

    private WhiteboardDocument mWhiteboardDocument;
    private Logger mLogger;

    public WhiteboardMessageHandler(WhiteboardDocument whiteboardDocument, byte[] message) {
        super(message);
        mWhiteboardDocument = whiteboardDocument;
        mLogger = Logger.getInstance(getClass().getSimpleName());
    }

    @Override
    protected void handle(byte[] bytes, int i, MessageType messageType) {
        if (messageType == MessageType.DOCUMENT_FRAGMENT) {
            String json = new String(bytes, i, bytes.length - i);
            mLogger.debug(json);
            mWhiteboardDocument.update(new WhiteboardDocumentFragment(json));
        }
    }

}
