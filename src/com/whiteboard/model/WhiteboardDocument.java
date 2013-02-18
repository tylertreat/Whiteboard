package com.whiteboard.model;

import android.graphics.Canvas;
import com.digitalxyncing.document.impl.Document;
import com.digitalxyncing.document.impl.DocumentFragment;

import java.io.IOException;

public class WhiteboardDocument extends Document<Canvas> {

    private Canvas mCanvas;
    private String mOwner;
    private boolean mIsShareEnabled;
    private String mConnection;

    public WhiteboardDocument(Canvas canvas, String owner) throws IOException {
        super(canvas);
        mCanvas = canvas;
        mOwner = owner;
    }

    @Override
    protected byte[] serialize(Canvas canvas) {
        return canvas.toString().getBytes();
    }

    @Override
    public boolean update(DocumentFragment<Canvas> documentFragment) {
        return false;
    }

    @Override
    public Canvas getFullState() {
        return mCanvas;
    }

    @Override
    public String toString() {
        return mCanvas.toString();
    }

    public String getOwner() {
        return mOwner;
    }

    public boolean isShareEnabled() {
        return mIsShareEnabled;
    }

    public void setShareEnabled(boolean isShareEnabled) {
        mIsShareEnabled = isShareEnabled;
    }

    public String getConnection() {
        return mConnection;
    }

    public void setConnection(String connection) {
        mConnection = connection;
    }
}
