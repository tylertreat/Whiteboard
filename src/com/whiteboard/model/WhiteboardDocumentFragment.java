package com.whiteboard.model;

import android.util.Log;
import com.digitalxyncing.document.impl.DocumentFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whiteboard.ui.view.WhiteboardView.DrawingPoint;

import java.lang.reflect.Type;
import java.util.Queue;

public class WhiteboardDocumentFragment extends DocumentFragment<Whiteboard> {

    public WhiteboardDocumentFragment(Queue<DrawingPoint> drawn) {
        Gson gson = new Gson();
        Type queueType = new TypeToken<Queue<DrawingPoint>>(){}.getType();
        String json = gson.toJson(drawn, queueType);
        Log.d(getClass().getSimpleName(), json);
        data = json.getBytes();
    }

    public WhiteboardDocumentFragment(String json) {
        data = json.getBytes();
    }

}
