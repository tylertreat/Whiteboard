package com.whiteboard.ui.activity;

import com.digitalxyncing.document.impl.Document;
import com.digitalxyncing.document.impl.DocumentFragment;

import java.io.IOException;

public class StringDocument extends Document<String> {

    private String mDocument;

    public StringDocument(String document) throws IOException {
        super(document);
        mDocument = document;
    }

    @Override
    protected byte[] serialize(String s) {
        return s.getBytes();
    }

    @Override
    public boolean update(DocumentFragment<String> documentFragment) {
        mDocument += documentFragment.toString();
        return true;
    }

    @Override
    public String getFullState() {
        return mDocument;
    }

    @Override
    public String toString() {
        return mDocument;
    }

}
