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

import android.os.AsyncTask;
import android.os.Bundle;
import com.clarionmedia.infinitum.activity.InfinitumActivity;
import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.whiteboard.model.WhiteboardDocument;
import com.whiteboard.service.WhiteboardService;
import com.whiteboard.ui.view.PaintView;

public class WhiteboardActivity extends InfinitumActivity {

    private PaintView mView;

    @Autowired
    private WhiteboardService mWhiteboardService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new PaintView(this);
        setContentView(mView);
        mView.requestFocus();
        new InviteUserTask().execute();
    }

    private class InviteUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            WhiteboardDocument whiteboard = mWhiteboardService.createWhiteboard();
            mWhiteboardService.inviteToWhiteboard(whiteboard, "Tyler", "ttreat31@gmail.com");
            return null;
        }

    }

}
