package com.whiteboard.ui.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@code WhiteboardView} extends {@link View} by using a {@link Canvas} for drawing.
 * It handles all of the touch and motion events.
 */
public class WhiteboardView extends View {

    private static final int BACKGROUND_COLOR = Color.WHITE;
    private static final int BRUSH_COLOR = Color.BLACK;
    private static final int TRACKBALL_SCALE = 10;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Paint mPaint;
    private float mCurX;
    private float mCurY;
    private Queue<DrawingPoint> mLastDrawn;

    private enum PaintMode {
        Draw,
        Erase,
    }

    public WhiteboardView(Context context, AttributeSet attr) {
        super(context, attr);
        setFocusable(true);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mLastDrawn = new LinkedList<DrawingPoint>();
        setBackgroundColor(BACKGROUND_COLOR);
    }

    /**
     * Clear the whiteboard canvas.
     */
    public void clear() {
        if (mCanvas != null) {
            mPaint.setColor(BACKGROUND_COLOR);
            mCanvas.drawPaint(mPaint);
            invalidate();
        }
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
        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if (mBitmap != null) {
            newCanvas.drawBitmap(mBitmap, 0, 0, null);
        }
        mBitmap = newBitmap;
        mCanvas = newCanvas;
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
        paint(PaintMode.Draw, mCurX, mCurY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            // TODO Fire document update message
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
            mode = PaintMode.Draw;
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
                            event.getHistoricalOrientation(j, i),
                            event.getHistoricalAxisValue(MotionEvent.AXIS_DISTANCE, j, i),
                            event.getHistoricalAxisValue(MotionEvent.AXIS_TILT, j, i));
                }
            }
            for (int j = 0; j < P; j++) {
                paint(getPaintModeForTool(event.getToolType(j), mode),
                        event.getX(j),
                        event.getY(j),
                        event.getPressure(j),
                        event.getTouchMajor(j),
                        event.getTouchMinor(j),
                        event.getOrientation(j),
                        event.getAxisValue(MotionEvent.AXIS_DISTANCE, j),
                        event.getAxisValue(MotionEvent.AXIS_TILT, j));
            }
            mCurX = event.getX();
            mCurY = event.getY();
        }
        return true;
    }

    private PaintMode getPaintModeForTool(int toolType, PaintMode defaultMode) {
        if (toolType == MotionEvent.TOOL_TYPE_ERASER) {
            return PaintMode.Erase;
        }
        return defaultMode;
    }

    private void paint(PaintMode mode, float x, float y) {
        paint(mode, x, y, 1.0f, 0, 0, 0, 0, 0);
    }

    private void paint(PaintMode mode, float x, float y, float pressure,
                       float major, float minor, float orientation,
                       float distance, float tilt) {
        if (mBitmap != null) {
            if (major <= 0 || minor <= 0) {
                // If size is not available, use a default value.
                major = minor = 16;
            }

            switch (mode) {
                case Draw:
                    mPaint.setColor(BRUSH_COLOR);
                    mPaint.setAlpha(Math.min((int) (pressure * 128), 255));
                    drawOval(mCanvas, x, y, major, minor, orientation, mPaint);
                    break;

                case Erase:
                    mPaint.setColor(BACKGROUND_COLOR);
                    mPaint.setAlpha(Math.min((int) (pressure * 128), 255));
                    drawOval(mCanvas, x, y, major, minor, orientation, mPaint);
                    break;
            }
        }
        invalidate();
    }

    /**
     * Draw an oval.
     * <p/>
     * When the orienation is 0 radians, orients the major axis vertically,
     * angles less than or greater than 0 radians rotate the major axis left or right.
     */
    private final RectF mReusableOvalRect = new RectF();

    private void drawOval(Canvas canvas, float x, float y, float major, float minor,
                          float orientation, Paint paint) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate((float) (orientation * 180 / Math.PI), x, y);
        mReusableOvalRect.left = x - minor / 2;
        mReusableOvalRect.right = x + minor / 2;
        mReusableOvalRect.top = y - major / 2;
        mReusableOvalRect.bottom = y + major / 2;
        canvas.drawOval(mReusableOvalRect, paint);
        canvas.restore();
        mLastDrawn.add(new DrawingPoint(mReusableOvalRect));
    }

    private class DrawingPoint {

        private RectF mRectF;
        // TODO We need to store the Paint information too, but Paint is not serializable

        public DrawingPoint(RectF rectF) {
            mRectF = new RectF(rectF);
        }

        public RectF getRectF() {
            return mRectF;
        }
    }

}