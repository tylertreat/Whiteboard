package com.whiteboard.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.digitalxyncing.document.impl.Document;
import com.digitalxyncing.document.impl.DocumentFragment;
import com.google.gson.Gson;
import com.whiteboard.ui.view.WhiteboardView.DrawingPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WhiteboardDocument extends Document<Whiteboard> {

    private Whiteboard mWhiteboard;
    private String mOwner;
    private boolean mIsShareEnabled;
    private String mShareConnection;
    private String mRequestConnection;
    private Gson mGson;

    public WhiteboardDocument(Whiteboard whiteboard, String owner) throws IOException {
        mWhiteboard = whiteboard;
        mOwner = owner;
        mGson = new Gson();
        setDocumentData(whiteboard.getBitmap());
    }

    @Override
    public boolean update(DocumentFragment<Whiteboard> documentFragment) {
        String json = new String(documentFragment.getData());
        DrawingPoint drawingPoint = mGson.fromJson(json, DrawingPoint.class);
        return mWhiteboard.update(drawingPoint);
    }

    @Override
    public Whiteboard getFullState() {
        return mWhiteboard;
    }

    @Override
    public String toString() {
        return mWhiteboard.toString();
    }

    public Canvas getCanvas() {
        return mWhiteboard.getCanvas();
    }

    public void setCanvas(Canvas canvas, Bitmap bitmap) {
        mWhiteboard.setCanvas(canvas);
        mWhiteboard.setBitmap(bitmap);
        setDocumentData(bitmap);
    }

    public boolean drawBitmap(Bitmap bitmap) {
        return mWhiteboard.update(bitmap);
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

    public String getShareConnection() {
        return mShareConnection;
    }

    public void setShareConnection(String connection) {
        mShareConnection = connection;
    }

    private void setDocumentData(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        data = stream.toByteArray();
    }

    public String getRequestConnection() {
        return mRequestConnection;
    }

    public void setRequestConnection(String requestConnection) {
        mRequestConnection = requestConnection;
    }
}
