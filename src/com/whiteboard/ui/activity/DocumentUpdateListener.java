package com.whiteboard.ui.activity;

import com.whiteboard.ui.view.WhiteboardView.DrawingPoint;

import java.util.Queue;

public interface DocumentUpdateListener {

    void onDocumentUpdate(Queue<DrawingPoint> points);

}
