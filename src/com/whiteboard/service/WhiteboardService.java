package com.whiteboard.service;

import com.whiteboard.model.InviteToken;
import com.whiteboard.model.WhiteboardDocument;

public interface WhiteboardService {

    void inviteToWhiteboard(WhiteboardDocument whiteboard, String email, InviteToken token);

}
