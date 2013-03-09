package com.whiteboard.model;

import android.util.Log;
import com.digitalxyncing.document.impl.DocumentFragment;
import com.google.gson.Gson;
import com.whiteboard.ui.view.WhiteboardView.DrawingPoint;

public class WhiteboardDocumentFragment extends DocumentFragment<Whiteboard> {

    public WhiteboardDocumentFragment(DrawingPoint drawingPoint) {
        Gson gson = new Gson();
        String json = gson.toJson(drawingPoint, DrawingPoint.class);
        Log.d(getClass().getSimpleName(), json);
        data = json.getBytes();
    }

    public WhiteboardDocumentFragment(String json) {
        data = json.getBytes();
    }

}
