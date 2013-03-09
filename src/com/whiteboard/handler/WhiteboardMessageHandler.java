package com.whiteboard.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.clarionmedia.infinitum.logging.Logger;
import com.digitalxyncing.communication.Endpoint;
import com.digitalxyncing.communication.MessageHandler;
import com.digitalxyncing.document.Message.MessageType;
import com.digitalxyncing.document.impl.DocumentFragment;
import com.whiteboard.model.Whiteboard;
import com.whiteboard.model.WhiteboardDocument;
import com.whiteboard.model.WhiteboardDocumentFragment;

public class WhiteboardMessageHandler extends MessageHandler {

    private Endpoint<Whiteboard> mEndpoint;
    private WhiteboardDocument mWhiteboardDocument;
    private Logger mLogger;

    public WhiteboardMessageHandler(Endpoint<Whiteboard> endpoint, WhiteboardDocument whiteboardDocument,
                                    byte[] message) {
        super(message);
        mEndpoint = endpoint;
        mWhiteboardDocument = whiteboardDocument;
        mLogger = Logger.getInstance(getClass().getSimpleName());
    }

    @Override
    protected void handle(byte[] bytes, int i, MessageType messageType) {
        switch (messageType) {
            case DOCUMENT_FRAGMENT:
                String json = new String(bytes, i, bytes.length - i);
                mLogger.debug(json);
                DocumentFragment<Whiteboard> fragment = new WhiteboardDocumentFragment(json);
                mWhiteboardDocument.update(fragment);
                if (mEndpoint.isHost()) {
                    // Propagate update to clients
                    mEndpoint.send(fragment);
                }
                break;
            case FULL_DOCUMENT:
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, i, bytes.length - i);
                mWhiteboardDocument.drawBitmap(bitmap);
                break;
            case FULL_DOCUMENT_REQUEST:
                mEndpoint.send(mWhiteboardDocument);
                break;
        }
    }

}
