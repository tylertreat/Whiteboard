package com.whiteboard.service;

import com.whiteboard.model.WhiteboardDocument;

public interface NotificationService {

    void emailWhiteboardInvite(WhiteboardDocument whiteboard, String name, String email);

}
