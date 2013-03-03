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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.EditText;
import com.clarionmedia.infinitum.activity.InfinitumActivity;
import com.clarionmedia.infinitum.activity.annotation.InjectLayout;
import com.clarionmedia.infinitum.activity.annotation.InjectView;
import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.clarionmedia.infinitum.logging.Logger;
import com.digitalxyncing.communication.Endpoint;
import com.digitalxyncing.communication.EndpointFactory;
import com.digitalxyncing.communication.HostEndpoint;
import com.whiteboard.R;
import com.whiteboard.auth.SessionManager;
import com.whiteboard.auth.TokenAuthenticator;
import com.whiteboard.handler.WhiteboardMessageHandlerFactory;
import com.whiteboard.model.InviteToken;
import com.whiteboard.model.WhiteboardDocument;
import com.whiteboard.model.WhiteboardDocumentFragment;
import com.whiteboard.service.TokenService;
import com.whiteboard.service.WhiteboardService;
import com.whiteboard.ui.view.WhiteboardView;
import com.whiteboard.util.NetworkUtils;

import java.io.IOException;

@InjectLayout(R.layout.activity_whiteboard)
public class WhiteboardActivity extends InfinitumActivity {

    @InjectView(R.id.whiteboard_view)
    private WhiteboardView mWhiteboard;

    @Autowired
    private WhiteboardService mWhiteboardService;

    @Autowired
    private TokenService mTokenService;

    @Autowired
    private EndpointFactory mEndpointFactory;

    @Autowired
    private TokenAuthenticator mTokenAuthenticator;

    private Endpoint<Canvas> mEndpoint;
    private Logger mLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogger = Logger.getInstance(getClass().getSimpleName());
        initializeEndpoint();
        mWhiteboard.requestFocus();
        mWhiteboard.setUpdateListener(new WhiteboardUpdateListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEndpoint != null) {
            mLogger.debug("Closing channels");
            mEndpoint.closeInboundChannel();
            mEndpoint.closeOutboundChannel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.whiteboard_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_full_screen:
                getActionBar().hide();
                return true;
            case R.id.menu_share:
                showShareDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeEndpoint() {
        try {
            setEndpoint(getIntent().getData());
        } catch (IOException e) {
            mLogger.error("Error while configuring endpoint", e);
        }
    }

    private void setEndpoint(Uri whiteboardUri) throws IOException {
        String connection;
        if (whiteboardUri != null) {
            String token = whiteboardUri.getQueryParameter("token");
            token = SessionManager.getUser().getEmail() + " " + token;
            connection = whiteboardUri.getQueryParameter("host");
            String[] hostAndPort = connection.split(":");
            new EndpointConnectionTask(hostAndPort[0], Integer.valueOf(hostAndPort[1]), token).execute();
        } else {
            String ip = NetworkUtils.getLocalIpAddress();
            int port = NetworkUtils.getAvailablePort();
            connection = ip + ':' + port;
            HostEndpoint<Canvas> endpoint = mEndpointFactory.buildHostEndpoint(port,
                    new WhiteboardMessageHandlerFactory(mWhiteboard.getDocument()),
                    mTokenAuthenticator);
            mEndpoint = endpoint;
            mWhiteboard.getDocument().setRequestConnection(ip + ":" + endpoint.getConnectionRequestPort());
        }
        mWhiteboard.getDocument().setShareConnection(connection);
    }

    private void showShareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_share, null);
        final EditText emailField = (EditText) layout.findViewById(R.id.email_field);
        builder.setView(layout)
                .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String email = emailField.getText().toString();
                        if (TextUtils.isEmpty(email))
                            return;
                        new ShareWhiteboardTask(mWhiteboard.getDocument()).execute(email);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    private class ShareWhiteboardTask extends AsyncTask<String, Void, Void> {

        private WhiteboardDocument mWhiteboardDoc;

        public ShareWhiteboardTask(WhiteboardDocument whiteboardDoc) {
            mWhiteboardDoc = whiteboardDoc;
        }

        @Override
        protected Void doInBackground(String... emails) {
            mWhiteboardDoc.setShareEnabled(true);
            for (String email : emails) {
                mLogger.debug("Inviting " + email + " to whiteboard");
                InviteToken token = mTokenService.createInviteToken(email);
                mWhiteboardService.inviteToWhiteboard(mWhiteboardDoc, email, token);
            }
            mLogger.debug("Opening inbound channel for whiteboard sharing at " + mWhiteboardDoc.getRequestConnection());
            mEndpoint.openInboundChannel();
            mEndpoint.openOutboundChannel();
            return null;
        }

    }

    private class EndpointConnectionTask extends AsyncTask<Void, Void, Void> {

        private String mHost;
        private int mPort;
        private String mToken;

        public EndpointConnectionTask(String host, int port, String token) {
            mHost = host;
            mPort = port;
            mToken = token;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mEndpoint = mEndpointFactory.buildClientEndpoint(mHost, mPort, mToken, Canvas.class,
                    new WhiteboardMessageHandlerFactory(mWhiteboard.getDocument()));
            mEndpoint.openInboundChannel();
            mEndpoint.openOutboundChannel();
            return null;
        }
    }

    private class WhiteboardUpdateListener implements DocumentUpdateListener {

        @Override
        public void onDocumentUpdate(WhiteboardDocumentFragment fragment) {
            mEndpoint.send(fragment);
        }

    }

}
