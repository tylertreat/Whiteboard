/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whiteboard.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.digitalxyncing.communication.Endpoint;
import com.digitalxyncing.communication.MessageHandler;
import com.digitalxyncing.communication.MessageHandlerFactory;
import com.digitalxyncing.communication.impl.ZmqClientEndpoint;
import com.digitalxyncing.document.Message.MessageType;
import com.whiteboard.model.StringDocument;
import com.whiteboard.ui.view.PaintView;

import java.io.IOException;

public class TouchPaint extends Activity {

    PaintView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new PaintView(this);
        setContentView(mView);
        mView.requestFocus();

        final Endpoint<String> endpoint = new ZmqClientEndpoint<String>("10.0.2.2", 6100, 6969, new MessageHandlerFactory<String>() {
            @Override
            public MessageHandler build(Endpoint<String> endpoint, byte[] bytes) {
                return new LoggingHandler(bytes);
            }
        });
        endpoint.openInboundChannel();
        Log.e("TouchPaint", "Listening...");
        endpoint.openOutboundChannel();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        StringDocument stringDocument = new StringDocument("Hello Host, this is Client A!");
                        boolean result = endpoint.send(stringDocument);
                        Log.e("Client A", "Message sent: " + result);
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private class LoggingHandler extends MessageHandler {


        public LoggingHandler(byte[] message) {
            super(message);
        }

        @Override
        protected void handle(byte[] message, int pos, MessageType messageType) {
            try {
                if (messageType == MessageType.FULL_DOCUMENT) {
                    StringDocument stringDocument = new StringDocument(new String(message, pos, message.length - pos));
                    Log.e(getClass().getSimpleName(), stringDocument.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
