package com.whiteboard.ui.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.whiteboard.auth.SessionManager;
import com.whiteboard.model.Whiteboard;
import com.whiteboard.model.WhiteboardDocument;
import com.whiteboard.ui.activity.DocumentUpdateListener;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * {@code WhiteboardView} extends {@link View} by using a {@link Canvas} for drawing. It handles all of the touch and
 * motion events.
 */
public class WhiteboardView extends View {

    private static final int BACKGROUND_COLOR = Color.WHITE;
    private static final int BRUSH_COLOR = Color.BLACK;
    private static final int TRACKBALL_SCALE = 10;

    private Bitmap mBitmap;
    private WhiteboardDocument mWhiteboard;
    private final Paint mPaint;
    private float mCurX;
    private float mCurY;
    private Queue<DrawingPoint> mLastDrawn;
    private DocumentUpdateListener mUpdateListener;

    private enum PaintMode {
        ERASE,
        DRAW
    }

    public WhiteboardView(Context context, AttributeSet attr) {
        super(context, attr);
        setFocusable(true);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mLastDrawn = new LinkedList<DrawingPoint>();
        setBackgroundColor(BACKGROUND_COLOR);

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        try {
            Whiteboard whiteboard = new Whiteboard(this, width, height);
            mWhiteboard = new WhiteboardDocument(whiteboard, SessionManager.getUser().getDisplayName());
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    public void setUpdateListener(DocumentUpdateListener updateListener) {
        mUpdateListener = updateListener;
    }

    public boolean update(final Queue<DrawingPoint> drawingPoints) {
        return post(new Runnable() {
            @Override
            public void run() {
                while (!drawingPoints.isEmpty()) {
                    DrawingPoint drawingPoint = drawingPoints.remove();
                    drawOval(mWhiteboard.getCanvas(), drawingPoint.mX, drawingPoint.mY, drawingPoint.mMajor,
                            drawingPoint.mMinor, drawingPoint.mOrientation, drawingPoint.mPaint, true);
                    invalidate();
                }
            }
        });
    }

    public void clear() {
        if (mWhiteboard != null) {
            mPaint.setColor(BACKGROUND_COLOR);
            mWhiteboard.getCanvas().drawPaint(mPaint);
            invalidate();
        }
    }

    public WhiteboardDocument getDocument() {
        return mWhiteboard;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int curW = mBitmap != null ? mBitmap.getWidth() : 0;
        int curH = mBitmap != null ? mBitmap.getHeight() : 0;
        if (curW >= w && curH >= h) {
            return;
        }

        if (curW < w) curW = w;
        if (curH < h) curH = h;

        Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas(newBitmap);
        if (mBitmap != null) {
            newCanvas.drawBitmap(mBitmap, 0, 0, null);
        }
        mBitmap = newBitmap;
        mWhiteboard.setCanvas(newCanvas, newBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            final int N = event.getHistorySize();
            final float scaleX = event.getXPrecision() * TRACKBALL_SCALE;
            final float scaleY = event.getYPrecision() * TRACKBALL_SCALE;
            for (int i = 0; i < N; i++) {
                moveTrackball(event.getHistoricalX(i) * scaleX,
                        event.getHistoricalY(i) * scaleY);
            }
            moveTrackball(event.getX() * scaleX, event.getY() * scaleY);
        }
        return true;
    }

    private void moveTrackball(float deltaX, float deltaY) {
        final int curW = mBitmap != null ? mBitmap.getWidth() : 0;
        final int curH = mBitmap != null ? mBitmap.getHeight() : 0;

        mCurX = Math.max(Math.min(mCurX + deltaX, curW - 1), 0);
        mCurY = Math.max(Math.min(mCurY + deltaY, curH - 1), 0);
        paint(PaintMode.DRAW, mCurX, mCurY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            // Fire document update message
            if (mUpdateListener != null && mWhiteboard.isShareEnabled()) {
                mUpdateListener.onDocumentUpdate(mLastDrawn);
            }
            mLastDrawn.clear();
        }
        return onTouchOrHoverEvent(event, true);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return onTouchOrHoverEvent(event, false);
    }

    private boolean onTouchOrHoverEvent(MotionEvent event, boolean isTouch) {
        final int buttonState = event.getButtonState();

        PaintMode mode;
        if (isTouch || (buttonState & MotionEvent.BUTTON_PRIMARY) != 0) {
            // Draw paint when touching or if the primary button is pressed.
            mode = PaintMode.DRAW;
        } else {
            // Otherwise, do not paint anything.
            return false;
        }

        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE
                || action == MotionEvent.ACTION_HOVER_MOVE) {
            final int N = event.getHistorySize();
            final int P = event.getPointerCount();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < P; j++) {
                    paint(getPaintModeForTool(event.getToolType(j), mode),
                            event.getHistoricalX(j, i),
                            event.getHistoricalY(j, i),
                            event.getHistoricalPressure(j, i),
                            event.getHistoricalTouchMajor(j, i),
                            event.getHistoricalTouchMinor(j, i),
                            event.getHistoricalOrientation(j, i));
                }
            }
            for (int j = 0; j < P; j++) {
                paint(getPaintModeForTool(event.getToolType(j), mode),
                        event.getX(j),
                        event.getY(j),
                        event.getPressure(j),
                        event.getTouchMajor(j),
                        event.getTouchMinor(j),
                        event.getOrientation(j));
            }
            mCurX = event.getX();
            mCurY = event.getY();
        }
        return true;
    }

    private PaintMode getPaintModeForTool(int toolType, PaintMode defaultMode) {
        if (toolType == MotionEvent.TOOL_TYPE_ERASER) {
            return PaintMode.ERASE;
        }
        return defaultMode;
    }

    private void paint(PaintMode mode, float x, float y) {
        paint(mode, x, y, 1.0f, 0, 0, 0);
    }

    private void paint(PaintMode mode, float x, float y, float pressure,
                       float major, float minor, float orientation) {
        if (mBitmap != null) {
            if (major <= 0 || minor <= 0) {
                // If size is not available, use a default value.
                major = minor = 16;
            }

            switch (mode) {
                case DRAW:
                    mPaint.setColor(BRUSH_COLOR);
                    mPaint.setAlpha(Math.min((int) (pressure * 128), 255));
                    drawOval(mWhiteboard.getCanvas(), x, y, major, minor, orientation, mPaint, false);
                    break;

                case ERASE:
                    mPaint.setColor(BACKGROUND_COLOR);
                    mPaint.setAlpha(Math.min((int) (pressure * 128), 255));
                    drawOval(mWhiteboard.getCanvas(), x, y, major, minor, orientation, mPaint, false);
                    break;
            }
        }
        invalidate();
    }

    private final RectF mReusableOvalRect = new RectF();

    private void drawOval(Canvas canvas, float x, float y, float major, float minor,
                          float orientation, Paint paint, boolean historical) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate((float) (orientation * 180 / Math.PI), x, y);
        mReusableOvalRect.left = x - minor / 2;
        mReusableOvalRect.right = x + minor / 2;
        mReusableOvalRect.top = y - major / 2;
        mReusableOvalRect.bottom = y + major / 2;
        canvas.drawOval(mReusableOvalRect, mPaint); // TODO not using paint passed in
        canvas.restore();
        if (!historical) {
            mLastDrawn.add(new DrawingPoint(x, y, major, minor, orientation, paint));
        }
    }

    public class DrawingPoint {

        private float mX;
        private float mY;
        private float mMajor;
        private float mMinor;
        private float mOrientation;
        private Paint mPaint;

        public DrawingPoint(float x, float y, float major, float minor, float orientation, Paint paint) {
            mX = x;
            mY = y;
            mMajor = major;
            mMinor = minor;
            mOrientation = orientation;
            mPaint = new Paint(paint);
        }

    }

}