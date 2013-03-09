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

public class WhiteboardMessageHandler extends MessageHandler<Whiteboard> {

    private WhiteboardDocument mWhiteboardDocument;
    private Logger mLogger;

    public WhiteboardMessageHandler(Endpoint<Whiteboard> endpoint, WhiteboardDocument whiteboardDocument,
                                    byte[] message) {
        super(endpoint, message);
        mWhiteboardDocument = whiteboardDocument;
        mLogger = Logger.getInstance(getClass().getSimpleName());
    }

    @Override
    protected void handle(byte[] bytes, int i, String origin, MessageType messageType) {
        switch (messageType) {
            case DOCUMENT_FRAGMENT:
                String json = new String(bytes, i, bytes.length - i);
                mLogger.debug(json);
                DocumentFragment<Whiteboard> fragment = new WhiteboardDocumentFragment(json);
                fragment.setOrigin(origin);
                mWhiteboardDocument.update(fragment);
                if (endpoint.isHost()) {
                    // Propagate update to clients
                    endpoint.send(fragment);
                }
                break;
            case FULL_DOCUMENT:
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, i, bytes.length - i);
                mWhiteboardDocument.drawBitmap(bitmap);
                break;
            case FULL_DOCUMENT_REQUEST:
                endpoint.send(mWhiteboardDocument);
                break;
        }
    }

}
