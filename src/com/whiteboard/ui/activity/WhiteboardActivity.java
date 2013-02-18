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
import com.digitalxyncing.communication.HostEndpoint;
import com.digitalxyncing.communication.impl.ZmqHostEndpoint;
import com.whiteboard.R;
import com.whiteboard.model.WhiteboardDocument;
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

    private HostEndpoint<Canvas> mHostEndpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        if (uri != null) {
            // TODO connect to host
        }
        mWhiteboard.requestFocus();
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

    private class ShareWhiteboardTask extends AsyncTask<String, Void, Integer> {

        private WhiteboardDocument mWhiteboardDoc;

        public ShareWhiteboardTask(WhiteboardDocument whiteboardDoc) {
            mWhiteboardDoc = whiteboardDoc;
        }

        @Override
        protected Integer doInBackground(String... emails) {
            mWhiteboardDoc.setShareEnabled(true);
            String connection = NetworkUtils.getLocalIpAddress();
            int port = 0;
            try {
                port = NetworkUtils.getAvailablePort();
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }
            mWhiteboardDoc.setConnection(connection + ':' + port);
            for (String email : emails) {
                mWhiteboardService.inviteToWhiteboard(mWhiteboardDoc, email);
            }
            return port;
        }

        @Override
        protected void onPostExecute(Integer port) {
            // TODO
            if (mHostEndpoint == null) {
                mHostEndpoint = new ZmqHostEndpoint<Canvas>(port, null);
            }
            mHostEndpoint.openInboundChannel();
        }
    }

}
