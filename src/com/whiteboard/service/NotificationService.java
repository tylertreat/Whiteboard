package com.whiteboard.service;

import com.whiteboard.model.InviteToken;
import com.whiteboard.model.WhiteboardDocument;

public interface NotificationService {

    void emailWhiteboardInvite(WhiteboardDocument whiteboard, String email, InviteToken token);

}
