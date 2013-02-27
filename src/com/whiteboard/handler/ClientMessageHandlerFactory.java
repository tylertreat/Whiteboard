package com.whiteboard.handler;

import android.graphics.Canvas;
import com.digitalxyncing.communication.Endpoint;
import com.digitalxyncing.communication.MessageHandler;
import com.digitalxyncing.communication.MessageHandlerFactory;

public class ClientMessageHandlerFactory implements MessageHandlerFactory<Canvas> {

    @Override
    public MessageHandler build(Endpoint<Canvas> canvasEndpoint, byte[] bytes) {
        return new ClientMessageHandler(bytes);
    }

}
