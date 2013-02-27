package com.whiteboard.handler;

import com.clarionmedia.infinitum.logging.Logger;
import com.digitalxyncing.communication.MessageHandler;
import com.digitalxyncing.document.Message.MessageType;

public class HostMessageHandler extends MessageHandler {

    private Logger mLogger;

    public HostMessageHandler(byte[] message) {
        super(message);
        mLogger = Logger.getInstance(getClass().getSimpleName());
    }

    @Override
    protected void handle(byte[] bytes, int i, MessageType messageType) {
        mLogger.debug(messageType.name() + ": " + new String(bytes, i, bytes.length));
    }

}