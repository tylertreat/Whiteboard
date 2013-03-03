package com.whiteboard.service.impl;

import com.clarionmedia.infinitum.di.annotation.Autowired;
import com.clarionmedia.infinitum.di.annotation.Bean;
import com.whiteboard.model.InviteToken;
import com.whiteboard.model.WhiteboardDocument;
import com.whiteboard.service.NotificationService;
import com.whiteboard.service.WhiteboardService;

@Bean("whiteboardService")
public class WhiteboardServiceImpl implements WhiteboardService {

    @Autowired
    private NotificationService mNotificationService;

    @Override
    public void inviteToWhiteboard(WhiteboardDocument whiteboard, String email, InviteToken token) {
        if (!whiteboard.isShareEnabled())
            throw new IllegalArgumentException("Sharing is disabled for this whiteboard");
        mNotificationService.emailWhiteboardInvite(whiteboard, email, token);
    }

}
