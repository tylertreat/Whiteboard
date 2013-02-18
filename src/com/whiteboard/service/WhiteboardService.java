package com.whiteboard.service;

import com.whiteboard.model.WhiteboardDocument;

public interface WhiteboardService {

    WhiteboardDocument createWhiteboard();

    void inviteToWhiteboard(WhiteboardDocument whiteboard, String email);

}
