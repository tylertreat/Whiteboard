package com.whiteboard.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.whiteboard.ui.view.WhiteboardView;
import com.whiteboard.ui.view.WhiteboardView.DrawingPoint;

import java.util.Queue;

public class Whiteboard {

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private WhiteboardView mWhiteboardView;

    public Whiteboard(WhiteboardView whiteboardView, int width, int height) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mWhiteboardView = whiteboardView;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Canvas getCanvas() {
        return mCanvas;
    }

    public void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    public boolean update(Queue<DrawingPoint> drawingPoints) {
        return mWhiteboardView.update(drawingPoints);
    }
}
