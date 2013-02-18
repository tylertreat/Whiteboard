package com.whiteboard.service.impl;

import android.graphics.Canvas;
import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.clarionmedia.infinitum.di.annotation.Bean;
import com.whiteboard.model.WhiteboardDocument;
import com.whiteboard.service.NotificationService;
import com.whiteboard.service.WhiteboardService;

import java.io.IOException;

@Bean("whiteboardService")
public class WhiteboardServiceImpl implements WhiteboardService {

    @Autowired
    private NotificationService mNotificationService;

    @Override
    public WhiteboardDocument createWhiteboard() {
        // TODO
        try {
            return new WhiteboardDocument(new Canvas(), "Tyler");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void inviteToWhiteboard(WhiteboardDocument whiteboard, String email) {
        if (!whiteboard.isShareEnabled())
            throw new IllegalArgumentException("Sharing is disabled for this whiteboard");
        mNotificationService.emailWhiteboardInvite(whiteboard, email);
    }

}
